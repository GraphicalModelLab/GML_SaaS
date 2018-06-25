package services

import auth._
import org.graphicalmodellab.auth.AuthDBClient
import org.graphicalmodellab.auth.facebookapps.FacebookAppsOpenIDConnector
import org.graphicalmodellab.auth.googleapps.GoogleAppsOpenIDConnector
import org.graphicalmodellab.encryption.Encryption
import org.graphicalmodellab.httprequest.HttpRequest
import org.codehaus.jettison.json.JSONObject
import play.Play
import play.api.http.Status

/**
 * Created by ito_m on 9/11/16.
 */
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
          val decodedToken = GoogleAppsOpenIDConnector.decodeToken(response.getString("id_token"));
          val accessToken = new Token().accessToken

          if (decodedToken.contains("email")) {
            AuthDBClient.registerGoogleAccount(
              companyId,
              decodedToken.get("email").get,
              accessToken,
              response.toString()
            )

            val roleInfo = AuthDBClient.getAccount(companyId,decodedToken.get("email").get)
            var roleString = ""
            if(roleInfo.contains("role")) {
              roleInfo.get("role") match {
                case Some(roleContent) => if(roleContent != null) roleString = roleContent.toString
              }
            }

            return googleAppsAuthenticateResponse(Status.OK,decodedToken.get("email").get,accessToken,roleString)
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
          val verificationResult = new JSONObject(FacebookAppsOpenIDConnector.verifyAccessToken(response.getString("access_token"),config.getString("facebookapps.application_accesstoken")));

          if(verificationResult.has("data")) {
            if(verificationResult.getJSONObject("data").getBoolean("is_valid")) {
              val accessToken = new Token().accessToken

              val meInfo = new JSONObject(FacebookAppsOpenIDConnector.getMeInfo(response.getString("access_token")));

              if (meInfo.has("email")) {
                AuthDBClient.registerGoogleAccount(
                            companyId,
                            meInfo.getString("email"),
                            accessToken,
                            response.toString()
                )

               val roleInfo = AuthDBClient.getAccount(companyId,meInfo.getString("email"))
                var roleString = ""
                if(roleInfo.contains("role")) {
                  roleInfo.get("role") match {
                    case Some(roleContent) => if(roleContent != null) roleString = roleContent.toString
                  }
                }
                return facebookAppsAuthenticateResponse(Status.OK,meInfo.getString("email"),accessToken,roleString)
              }
            }
          }

        }

        return facebookAppsAuthenticateResponse(auth.STATUS_NOT_VALIDATED,"","","")
      case None =>

        return facebookAppsAuthenticateResponse(auth.STATUS_NOT_VALIDATED,"","","")
    }
    return facebookAppsAuthenticateResponse(Status.INTERNAL_SERVER_ERROR,"","","")

  }
}