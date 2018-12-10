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
import org.graphicalmodellab.auth.AuthDBClient
import org.graphicalmodellab.auth.facebookapps.FacebookAppsOpenIDConnector
import org.graphicalmodellab.auth.googleapps.GoogleAppsOpenIDConnector
import org.graphicalmodellab.encryption.Encryption
import org.graphicalmodellab.httprequest.HttpRequest
import org.codehaus.jettison.json.JSONObject
import play.Play
import play.api.http.Status

class AuthService {
  val config = Play.application().configuration()

  def login(companyId:String,request: Option[loginRequest]): loginResponse = {

    request match {
      case Some(request)=>

        val accountInfo = AuthDBClient.getAccount(companyId,request.email)

        if(accountInfo.contains("password")){
          if(accountInfo.get("password").get.toString == Encryption.toEncryptedString(request.password) && accountInfo.get("validated").get.toString == "true"){
            val accessToken = new Token().accessToken
            AuthDBClient.updateAccesstoken(companyId,request.email, accessToken)

            return loginResponse(Status.OK, accessToken, accountInfo.get("role").get.toString)
          }else if(accountInfo.get("validated").get.toString != "true"){
            return loginResponse(auth.STATUS_NOT_VALIDATED, "", "")
          }else if(accountInfo.get("password").get.toString != Encryption.toEncryptedString(request.password)){
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

        val validationToken = new Token().accessToken
        AuthDBClient.registerAccount(companyId,request.email,validationToken,request.password,"")

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

        if(!AuthDBClient.checkIfExist(companyid)){
          AuthDBClient.registerCompany(companyid,request.companyname)

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

        val accessToken = new Token().accessToken
        if(AuthDBClient.validateValidationCode(companyId,request.email,request.validationCode, accessToken)) {
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

        var sysadminCompanyId = config.getString("sysadmin.companyid")
        if(companyId == sysadminCompanyId && request.role == "sysadmin") {
          AuthDBClient.changeRole(request.changingUserCompany, request.changingUserid, request.newRole)
        }else {
          if(request.role == "administrator") {
            if (AuthDBClient.checkIfAccountExist(companyId, request.changingUserid)) {
              AuthDBClient.changeRole(companyId, request.changingUserid, request.newRole)
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
        AuthDBClient.changePassword(companyId,request.userid,request.newPassword)
      case None =>
        println("No request")
    }
    return changePasswordResponse(Status.INTERNAL_SERVER_ERROR)
  }

  def removeAccount(companyId:String, request: Option[removeAccountRequest]): removeAccountResponse = {

    request match {
      case Some(request)=>
        AuthDBClient.removeAccount(companyId,request.userid)
      case None =>
        println("No request")
    }
    return removeAccountResponse(Status.INTERNAL_SERVER_ERROR)
  }

  def googleAppsAuthenticate(companyId:String,request: Option[googleAppsAuthenticateRequest]) :googleAppsAuthenticateResponse= {
    request match {
      case Some(request)=>

        val response = new JSONObject(GoogleAppsOpenIDConnector.getAccessToken(request.code,config.getString("googleapps.client_id"),config.getString("googleapps.client_secret"),config.getString("googleapps.redirect_uri")))

        if(response.has("id_token")) {
          val state = new JSONObject(request.state)
          state.getString("type") match {
            case "login" =>
              val decodedToken = GoogleAppsOpenIDConnector.decodeToken(response.getString("id_token"));
              val accessToken = new Token().accessToken

              if (decodedToken.contains("email")) {
                AuthDBClient.registerGoogleAccount(
                  companyId,
                  decodedToken.get("email").get,
                  accessToken,
                  response.toString()
                )

                val roleInfo = AuthDBClient.getAccount(companyId, decodedToken.get("email").get)
                var roleString = ""
                if (roleInfo.contains("role")) {
                  roleInfo.get("role") match {
                    case Some(roleContent) => if (roleContent != null) roleString = roleContent.toString
                  }
                }

                return googleAppsAuthenticateResponse(Status.OK, decodedToken.get("email").get, accessToken, roleString)
              }

            case "connect" =>
              if (AuthDBClient.isValidToken(companyId, state.getString("userid"), state.getString("token"))) {
                val decodedToken = GoogleAppsOpenIDConnector.decodeToken(response.getString("id_token"));
                val accessToken = new Token().accessToken

                if (decodedToken.contains("email")) {
                  AuthDBClient.registerGoogleConnect(
                    companyId,
                    state.getString("userid"),
                    new Date(),
                    accessToken,
                    response.toString()
                  )

                  val roleInfo = AuthDBClient.getAccount(companyId, decodedToken.get("email").get)
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

        val response = new JSONObject(FacebookAppsOpenIDConnector.getAccessToken(request.code,config.getString("facebookapps.client_id"),config.getString("facebookapps.client_secret"),config.getString("facebookapps.redirect_uri")))

        if(response.has("access_token")) {
          val state = new JSONObject(request.state)

          state.getString("type") match {
            case "login" =>
              val verificationResult = new JSONObject(FacebookAppsOpenIDConnector.verifyAccessToken(response.getString("access_token"), config.getString("facebookapps.application_accesstoken")));

              if (verificationResult.has("data")) {
                if (verificationResult.getJSONObject("data").getBoolean("is_valid")) {
                  val accessToken = new Token().accessToken

                  val meInfo = new JSONObject(FacebookAppsOpenIDConnector.getMeInfo(response.getString("access_token")));

                  if (meInfo.has("email")) {
                    AuthDBClient.registerGoogleAccount(
                      companyId,
                      meInfo.getString("email"),
                      accessToken,
                      response.toString()
                    )

                    val roleInfo = AuthDBClient.getAccount(companyId, meInfo.getString("email"))
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
              if (AuthDBClient.isValidToken(companyId, state.getString("userid"), state.getString("token"))) {

                val verificationResult = new JSONObject(FacebookAppsOpenIDConnector.verifyAccessToken(response.getString("access_token"), config.getString("facebookapps.application_accesstoken")));

                if (verificationResult.has("data")) {
                  if (verificationResult.getJSONObject("data").getBoolean("is_valid")) {

                    val meInfo = new JSONObject(FacebookAppsOpenIDConnector.getMeInfo(response.getString("access_token")));

                    if (meInfo.has("email")) {
                      AuthDBClient.registerFacebookConnect(
                        companyId,
                        state.getString("userid"),
                        new Date(),
                        meInfo.getString("email"),
                        response.getString("access_token"),
                        response.getString("token_type"),
                        response.getLong("expires_in")
                      );

                      val roleInfo = AuthDBClient.getAccount(companyId, meInfo.getString("email"))
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

        if (AuthDBClient.isValidToken(companyId, request.userid, request.token)) {
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

          val facebookAccountInfo = AuthDBClient.getFacebookAccount(companyId,request.userid)
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

          val googleAccountInfo = AuthDBClient.getGoogleAccount(companyId,request.userid)
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

        if (AuthDBClient.isValidToken(companyId, request.userid, request.token)) {
          // 1. Deactivate access token by calling Facebook API

          // 2. Remove info from DB
          AuthDBClient.removeFacebookConnection(companyId,request.userid)

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
