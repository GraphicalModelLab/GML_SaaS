/**
  * Copyright (C) 2018 Mao Ito
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package services

import java.util.Date

import auth._
import com.google.inject.Inject
import auth.warmupResponse
import auth.warmupResponse
import org.graphicalmodellab.auth.AuthDBClient
import org.graphicalmodellab.auth.facebookapps.FacebookAppsOpenIDConnector
import org.graphicalmodellab.auth.googleapps.GoogleAppsOpenIDConnector
import org.codehaus.jettison.json.JSONObject
import org.graphicalmodellab.hash.Hash
import play.api.Configuration
import play.api.http.Status
import services.token.Token

class AuthServiceImpl @Inject() (config: Configuration, authDBClient: AuthDBClient, googleAppsOpenIDConnector: GoogleAppsOpenIDConnector,facebookAppsOpenIDConnector: FacebookAppsOpenIDConnector, hash: Hash, token: Token) extends AuthService{

  def helloworld(): String = "hello impl"

  def init() : Unit={
    authDBClient.init(config.get[String]("auth.cassandra.keyspace"),List[String](config.get[String]("auth.cassandra.host")))
  }

  // Call http://localhost:9098/helloworld to warmup this service
  @Inject
  def warmup(): warmupResponse={
    init();

    return warmupResponse(Status.OK)
  }

  def login(companyId:String,request: Option[loginRequest]): loginResponse = {

    request match {
      case Some(request)=>

        val accountInfo = authDBClient.getAccount(companyId,request.email)

        if(accountInfo.contains("password")){
          if(accountInfo.get("password").get.toString == hash.toHashString(request.password) && accountInfo.get("validated").get.toString == "true"){
            val accessToken = token.generateToken()

            authDBClient.updateAccesstoken(companyId,request.email, accessToken)

            return loginResponse(Status.OK, accessToken, accountInfo.get("role").get.toString)
          }else if(accountInfo.get("validated").get.toString != "true"){
            return loginResponse(auth.STATUS_NOT_VALIDATED, "", "")
          }else if(accountInfo.get("password").get.toString != hash.toHashString(request.password)){
            return loginResponse(auth.STATUS_PASSWORD_INVALID, "", "")
          }
        }else{
          return loginResponse(auth.STATUS_NOT_REGISTERED, "", "")
        }
      case None =>
        println("No request")
    }
    return loginResponse(Status.INTERNAL_SERVER_ERROR, "","")
  }

  def register(companyId:String, requestInfo: Option[registerRequest]): registerResponse = {

    requestInfo match {
      case Some(request)=>

        val validationToken = token.generateToken()
        authDBClient.registerAccount(companyId,request.email,validationToken,request.password,"")

        return registerResponse(Status.OK, validationToken)

      case None =>
        println("No request")
    }
    return registerResponse(Status.INTERNAL_SERVER_ERROR, "")
  }

  def registerCompany(companyId:String, requestInfo: Option[registerCompanyRequest]): registerCompanyResponse = {

    requestInfo match {
      case Some(request)=>
        var companyid = request.companycode

        if(!authDBClient.checkIfExist(companyid)){
          authDBClient.registerCompany(companyid,request.companyname)

          return registerCompanyResponse(Status.OK, Status.OK)
        }


      case None =>
        println("No request")
    }
    return registerCompanyResponse(Status.INTERNAL_SERVER_ERROR, -1)
  }

  def validation(companyId:String, request: Option[validationRequest]): validationResponse = {

    request match {
      case Some(request)=>

        val accessToken = token.generateToken()
        if(authDBClient.validateValidationCode(companyId,request.email,request.validationCode, accessToken)) {
          return validationResponse(Status.OK, accessToken)
        }

      case None =>
        println("No request")
    }
    return validationResponse(Status.INTERNAL_SERVER_ERROR, "")
  }

  def changeUserRole(companyId:String, request: Option[changeRoleRequest]): changeRoleResponse = {

    request match {
      case Some(request)=>

        var sysadminCompanyId = config.get[String]("sysadmin.companyid")
        if(companyId == sysadminCompanyId && request.role == "sysadmin") {
          authDBClient.changeRole(request.changingUserCompany, request.changingUserid, request.newRole)
        }else {
          if(request.role == "administrator") {
            if (authDBClient.checkIfAccountExist(companyId, request.changingUserid)) {
              authDBClient.changeRole(companyId, request.changingUserid, request.newRole)
            }
          }
        }

      case None =>
        println("No request")
    }
    return changeRoleResponse(Status.INTERNAL_SERVER_ERROR)
  }

  def changePassword(companyId:String, request: Option[changePasswordRequest]): changePasswordResponse = {

    request match {
      case Some(request)=>
        authDBClient.changePassword(companyId,request.userid,request.newPassword)
      case None =>
        println("No request")
    }
    return changePasswordResponse(Status.INTERNAL_SERVER_ERROR)
  }

  def removeAccount(companyId:String, request: Option[removeAccountRequest]): removeAccountResponse = {

    request match {
      case Some(request)=>
        authDBClient.removeAccount(companyId,request.userid)
      case None =>
        println("No request")
    }
    return removeAccountResponse(Status.INTERNAL_SERVER_ERROR)
  }

  def googleAppsAuthenticate(companyId:String,request: Option[googleAppsAuthenticateRequest]) :googleAppsAuthenticateResponse= {
    request match {
      case Some(request)=>

        val response = new JSONObject(googleAppsOpenIDConnector.getAccessToken(request.code,config.get[String]("googleapps.client_id"),config.get[String]("googleapps.client_secret"),config.get[String]("googleapps.redirect_uri")))

        if(response.has("id_token")) {
          val state = new JSONObject(request.state)
          state.getString("type") match {
            case "login" =>
              val decodedToken = googleAppsOpenIDConnector.decodeToken(response.getString("id_token"));
              val accessToken = token.generateToken()

              if (decodedToken.contains("email")) {
                authDBClient.registerGoogleAccount(
                  companyId,
                  decodedToken.get("email").get,
                  accessToken,
                  response.toString()
                )

                val roleInfo = authDBClient.getAccount(companyId, decodedToken.get("email").get)
                var roleString = ""
                if (roleInfo.contains("role")) {
                  roleInfo.get("role") match {
                    case Some(roleContent) => if (roleContent != null) roleString = roleContent.toString
                  }
                }

                return googleAppsAuthenticateResponse(Status.OK, decodedToken.get("email").get, accessToken, roleString)
              }

            case "connect" =>
              if (authDBClient.isValidToken(companyId, state.getString("userid"), state.getString("token"))) {
                val decodedToken = googleAppsOpenIDConnector.decodeToken(response.getString("id_token"));
                val accessToken = token.generateToken()

                if (decodedToken.contains("email")) {
                  authDBClient.registerGoogleConnect(
                    companyId,
                    state.getString("userid"),
                    new Date(),
                    accessToken,
                    response.toString()
                  )

                  val roleInfo = authDBClient.getAccount(companyId, decodedToken.get("email").get)
                  var roleString = ""
                  if (roleInfo.contains("role")) {
                    roleInfo.get("role") match {
                      case Some(roleContent) => if (roleContent != null) roleString = roleContent.toString
                    }
                  }

                  return googleAppsAuthenticateResponse(Status.OK, decodedToken.get("email").get, accessToken, roleString)
                }
              }
          }
        }

        return googleAppsAuthenticateResponse(auth.STATUS_NOT_VALIDATED,"","","")
      case None =>

        return googleAppsAuthenticateResponse(auth.STATUS_NOT_VALIDATED,"","","")
    }
    return googleAppsAuthenticateResponse(Status.INTERNAL_SERVER_ERROR,"","","")

  }

  def facebookAppsAuthenticate(companyId:String,request: Option[facebookAppsAuthenticateRequest]) :facebookAppsAuthenticateResponse= {
    request match {
      case Some(request)=>

        val response = new JSONObject(facebookAppsOpenIDConnector.getAccessToken(request.code,config.get[String]("facebookapps.client_id"),config.get[String]("facebookapps.client_secret"),config.get[String]("facebookapps.redirect_uri")))

        if(response.has("access_token")) {
          val state = new JSONObject(request.state)

          state.getString("type") match {
            case "login" =>
              val verificationResult = new JSONObject(facebookAppsOpenIDConnector.verifyAccessToken(response.getString("access_token"), config.get[String]("facebookapps.application_accesstoken")));

              if (verificationResult.has("data")) {
                if (verificationResult.getJSONObject("data").getBoolean("is_valid")) {
                  val accessToken = token.generateToken()

                  val meInfo = new JSONObject(facebookAppsOpenIDConnector.getMeInfo(response.getString("access_token")));

                  if (meInfo.has("email")) {
                    authDBClient.registerFacebookAccount(
                      companyId,
                      meInfo.getString("email"),
                      accessToken,
                      response.toString()
                    )

                    val roleInfo = authDBClient.getAccount(companyId, meInfo.getString("email"))
                    var roleString = ""
                    if (roleInfo.contains("role")) {
                      roleInfo.get("role") match {
                        case Some(roleContent) => if (roleContent != null) roleString = roleContent.toString
                      }
                    }
                    return facebookAppsAuthenticateResponse(Status.OK, meInfo.getString("email"), accessToken, roleString,-1L)
                  }
                }
              }
            case "connect" =>
              if (authDBClient.isValidToken(companyId, state.getString("userid"), state.getString("token"))) {

                val verificationResult = new JSONObject(facebookAppsOpenIDConnector.verifyAccessToken(response.getString("access_token"), config.get[String]("facebookapps.application_accesstoken")));

                if (verificationResult.has("data")) {
                  if (verificationResult.getJSONObject("data").getBoolean("is_valid")) {

                    val meInfo = new JSONObject(facebookAppsOpenIDConnector.getMeInfo(response.getString("access_token")));

                    if (meInfo.has("email")) {
                      authDBClient.registerFacebookConnect(
                        companyId,
                        state.getString("userid"),
                        new Date(),
                        meInfo.getString("email"),
                        response.getString("access_token"),
                        response.getString("token_type"),
                        response.getLong("expires_in")
                      );

                      val roleInfo = authDBClient.getAccount(companyId, meInfo.getString("email"))
                      var roleString = ""
                      if (roleInfo.contains("role")) {
                        roleInfo.get("role") match {
                          case Some(roleContent) => if (roleContent != null) roleString = roleContent.toString
                        }
                      }

                      return facebookAppsAuthenticateResponse(Status.OK, meInfo.getString("email"), state.getString("token"), roleString, response.getLong("expires_in"))
                    }
                  }
                }
              }
          }

        }

        return facebookAppsAuthenticateResponse(auth.STATUS_NOT_VALIDATED,"","","",-1L)
      case None =>

        return facebookAppsAuthenticateResponse(auth.STATUS_NOT_VALIDATED,"","","",-1L)
    }
    return facebookAppsAuthenticateResponse(Status.INTERNAL_SERVER_ERROR,"","","",-1L)

  }


  def getSocialConnectStatus(companyId:String,request: Option[socialConnectStatusRequest]) :socialConnectStatusResponse= {
    request match {
      case Some(request)=>

        if (authDBClient.isValidToken(companyId, request.userid, request.token)) {
          var facebookAlive = false;
          var facebookConnectionDate = new Date()
          var facebookTimePassed = -1L
          var facebookExpiresIn = -1L
          var facebookExpired = false

          var googleAlive = false;
          var googleConnectionDate = new Date()
          var googleTimePassed = -1L
          var googleExpiresIn = -1L
          var googleExpired = false

          val facebookAccountInfo = authDBClient.getFacebookAccount(companyId,request.userid)
          if(facebookAccountInfo.contains("access_token")){
            val registered_date = facebookAccountInfo.get("registered_date");
            val expires_in = facebookAccountInfo.get("expires_in")
            val now = new Date();

            val seconds = (now.getTime() - registered_date.get.asInstanceOf[Date].getTime())/1000;

            if(seconds < expires_in.get.asInstanceOf[Long]){
              // Still this connection is alive
              facebookAlive = true
              facebookConnectionDate = registered_date.get.asInstanceOf[Date]
              facebookTimePassed = seconds
              facebookExpiresIn = expires_in.get.asInstanceOf[Long]
              facebookExpired = false
            }else{
              facebookExpired = true
            }

          }

          val googleAccountInfo = authDBClient.getGoogleAccount(companyId,request.userid)
          if(googleAccountInfo.contains("accesstoken")){
            val registered_date = googleAccountInfo.get("registered_date");
            val googleAppsJSON = new JSONObject(googleAccountInfo.get("googleapps").get.toString)
            val expires_in = googleAppsJSON.getLong("expires_in")
            val now = new Date();

            val seconds = (now.getTime() - registered_date.get.asInstanceOf[Date].getTime())/1000;

            if(seconds < expires_in){
              // Still this connection is alive
              googleAlive = true
              googleConnectionDate = registered_date.get.asInstanceOf[Date]
              googleTimePassed = seconds
              googleExpiresIn = expires_in
            }else{
              googleExpired = true
            }
          }

          return socialConnectStatusResponse(
            auth.STATUS_NOT_VALIDATED,

            facebookAlive,
            facebookExpiresIn,
            facebookConnectionDate.getTime,
            facebookTimePassed,
            facebookExpired,

            googleAlive,
            googleExpiresIn,
            googleConnectionDate.getTime,
            googleTimePassed,
            googleExpired
          )
        }else{
          return socialConnectStatusResponse(Status.UNAUTHORIZED,false,-1L,-1L,-1L,false,false,-1L,-1L,-1L,false)
        }

        return socialConnectStatusResponse(auth.STATUS_NOT_VALIDATED,false,-1L,-1L,-1L,false,false,-1L,-1L,-1L,false)
      case None =>

        return socialConnectStatusResponse(auth.STATUS_NOT_VALIDATED,false,-1L,-1L,-1L,false,false,-1L,-1L,-1L,false)
    }
    return socialConnectStatusResponse(Status.INTERNAL_SERVER_ERROR,false,-1L,-1L,-1L,false,false,-1L,-1L,-1L,false)

  }

  def disconnectFacebookConnection(companyId:String,request: Option[disconnectFacebookRequest]) :disconnectFacebookResponse= {
    request match {
      case Some(request)=>

        if (authDBClient.isValidToken(companyId, request.userid, request.token)) {
          // 1. Deactivate access token by calling Facebook API

          // 2. Remove info from DB
          authDBClient.removeFacebookConnection(companyId,request.userid)

          return disconnectFacebookResponse(auth.STATUS_NOT_VALIDATED)
        }else{
          return disconnectFacebookResponse(Status.UNAUTHORIZED)
        }

        return disconnectFacebookResponse(auth.STATUS_NOT_VALIDATED)
      case None =>

        return disconnectFacebookResponse(auth.STATUS_NOT_VALIDATED)
    }
    return disconnectFacebookResponse(Status.INTERNAL_SERVER_ERROR)

  }
}
