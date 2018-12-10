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

package controllers

import java.io.{PrintWriter, StringWriter}

import auth._
import org.graphicalmodellab.auth.AuthDBClient
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.AuthService

object AuthController extends Controller {

  val authService = new AuthService()
  AuthDBClient.init(List[String]("localhost"))

  def helloworld() = {
    Action(request =>
      Ok("hello auth service")
    )
  }

  def login(companyId:String) = {
    Action(request =>
      try
        Ok(Json.toJson[loginResponse](authService.login(companyId,Json.fromJson[loginRequest](request.body.asJson.get).asOpt)))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def register(companyId:String) = {
    Action(request =>
      try
        Ok(Json.toJson[registerResponse](authService.register(companyId,Json.fromJson[registerRequest](request.body.asJson.get).asOpt)))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def registerCompany(companyId:String) = {
    Action(request =>
      try
        Ok(Json.toJson[registerCompanyResponse](authService.registerCompany(companyId,Json.fromJson[registerCompanyRequest](request.body.asJson.get).asOpt)))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def validation(companyId:String) = {
    Action(request =>
      try
        Ok(Json.toJson[validationResponse](authService.validation(companyId,Json.fromJson[validationRequest](request.body.asJson.get).asOpt)))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def changeRole(companyId:String) = {
    Action(request =>
      try
        Ok(Json.toJson[changeRoleResponse](authService.changeUserRole(companyId,Json.fromJson[changeRoleRequest](request.body.asJson.get).asOpt)))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def changePassword(companyId:String) = {
    Action(request =>
      try
        Ok(Json.toJson[changePasswordResponse](authService.changePassword(companyId,Json.fromJson[changePasswordRequest](request.body.asJson.get).asOpt)))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def removeAccount(companyId:String) = {
    Action(request =>
      try
        Ok(Json.toJson[removeAccountResponse](authService.removeAccount(companyId,Json.fromJson[removeAccountRequest](request.body.asJson.get).asOpt)))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def googleAppsAuthenticate(companyId:String) = {
    Action(request =>
      try
        Ok(Json.toJson[googleAppsAuthenticateResponse](authService.googleAppsAuthenticate(companyId,Json.fromJson[googleAppsAuthenticateRequest](request.body.asJson.get).asOpt)))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def facebookAppsAuthenticate(companyId:String) = {
    Action(request =>
      try
        Ok(Json.toJson[facebookAppsAuthenticateResponse](authService.facebookAppsAuthenticate(companyId,Json.fromJson[facebookAppsAuthenticateRequest](request.body.asJson.get).asOpt)))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def getSocialConnectStatus(companyId:String) = {
    Action(request =>
      try
        Ok(Json.toJson[socialConnectStatusResponse](authService.getSocialConnectStatus(companyId,Json.fromJson[socialConnectStatusRequest](request.body.asJson.get).asOpt)))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def disconnectFacebookConnection(companyId:String) = {
    Action(request =>
      try
        Ok(Json.toJson[disconnectFacebookResponse](authService.disconnectFacebookConnection(companyId,Json.fromJson[disconnectFacebookRequest](request.body.asJson.get).asOpt)))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }
}
