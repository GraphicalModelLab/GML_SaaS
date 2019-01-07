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

package org.graphicalmodellab.auth

import java.util.Date

trait AuthDBClient {
  def init(keyspace:String, hosts: List[String]): Unit
  def registerCompany(companyid:String,companyname: String) : Unit
  def checkIfExist(companyid: String) : Boolean
  def checkIfAccountExist(companyid: String,id: String) : Boolean
  def registerAccount(companyId: String,id:String, validationtoken: String, password: String, role:String): Unit
  def getAccount(companyId: String,id:String): Map[String,Any]
  def updateAccesstoken(companyId: String,id:String, accesstoken: String): Unit
  def validateAccessToken(companyId: String,id:String, accesstoken: String): Boolean
  def isValidLogin(companyId: String,id:String, password:String): Boolean
  def validateValidationCode(companyId: String,id:String, validationCode:String, accessToken:String) : Boolean
  def hasValidatedValidationCode(companyId: String,id:String, accessToken: String): Unit
  def isValidToken(companyId: String, id: String, token: String): Boolean
  def isValidTokenAndRole(companyId:String, id:String, token: String, role: String): Boolean
  def changeRole(companyId: String,id:String, newRole: String): Unit
  def changePassword(companyId: String,id:String, newPassword: String): Unit
  def removeAccount(companyId: String,id:String): Unit
  def registerGoogleAccount(companyId: String, id:String, accesstoken: String, googleapps: String): Unit
  def registerGoogleConnect(companyId: String, id:String, registered_date: Date, accesstoken: String, googleapps: String): Unit
  def registerFacebookAccount(companyId: String, id:String, accesstoken: String, facebookapps: String): Unit
  def registerFacebookConnect(companyId: String, id:String, registered_date: Date, email: String, access_token: String, token_type: String, expires_in: Long): Unit
  def getFacebookAccount(companyId: String,id:String): Map[String,Any]
  def removeFacebookConnection(companyId: String,id:String): Unit
  def getGoogleAccount(companyId: String,id:String): Map[String,Any]
}
