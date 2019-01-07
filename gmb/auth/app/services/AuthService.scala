package services

import java.util.Date

import auth.{changePasswordRequest, changePasswordResponse, changeRoleRequest, changeRoleResponse, disconnectFacebookRequest, disconnectFacebookResponse, facebookAppsAuthenticateRequest, facebookAppsAuthenticateResponse, googleAppsAuthenticateRequest, googleAppsAuthenticateResponse, loginRequest, loginResponse, registerCompanyRequest, registerCompanyResponse, registerRequest, registerResponse, removeAccountRequest, removeAccountResponse, socialConnectStatusRequest, socialConnectStatusResponse, validationRequest, validationResponse, warmupResponse}
import auth.warmupResponse
import org.codehaus.jettison.json.JSONObject
import org.graphicalmodellab.auth.facebookapps.FacebookAppsOpenIDConnector
import org.graphicalmodellab.auth.googleapps.GoogleAppsOpenIDConnector
import org.graphicalmodellab.encryption.AESImpl
import play.api.http.Status

/**
  * Created by itomao on 12/19/18.
  */
trait AuthService {
  def helloworld(): String
  def init() : Unit
  // Call http://localhost:9098/helloworld to warmup this service
  def warmup(): warmupResponse

  def login(companyId:String,request: Option[loginRequest]): loginResponse
  def register(companyId:String, requestInfo: Option[registerRequest]): registerResponse
  def registerCompany(companyId:String, requestInfo: Option[registerCompanyRequest]): registerCompanyResponse
  def validation(companyId:String, request: Option[validationRequest]): validationResponse
  def changeUserRole(companyId:String, request: Option[changeRoleRequest]): changeRoleResponse
  def changePassword(companyId:String, request: Option[changePasswordRequest]): changePasswordResponse
  def removeAccount(companyId:String, request: Option[removeAccountRequest]): removeAccountResponse
  def googleAppsAuthenticate(companyId:String,request: Option[googleAppsAuthenticateRequest]) :googleAppsAuthenticateResponse
  def facebookAppsAuthenticate(companyId:String,request: Option[facebookAppsAuthenticateRequest]) :facebookAppsAuthenticateResponse
  def getSocialConnectStatus(companyId:String,request: Option[socialConnectStatusRequest]) :socialConnectStatusResponse
  def disconnectFacebookConnection(companyId:String,request: Option[disconnectFacebookRequest]) :disconnectFacebookResponse
}
