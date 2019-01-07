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

import java.nio.ByteBuffer
import java.util.Date

import com.datastax.driver.core.DataType
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.google.inject.Inject
import org.graphicalmodellab.cassandra.CassandraClient
import org.graphicalmodellab.encryption.Encryption
import org.graphicalmodellab.hash.Hash

class AuthDBClientEncryptingImpl @Inject()(hash: Hash, client: CassandraClient, encryption: Encryption) extends AuthDBClient{
  val STATUS_INVALID_ACCESS_TOKEN = 10000
  var _keyspace = "master"

  def init(keyspace: String, hosts: List[String]): Unit = {
    client.connect(List[String](
    "localhost"
    ))

    _keyspace = keyspace
  }

  def registerCompany(companyid:String,companyname: String) : Unit={
    val query = QueryBuilder.update(_keyspace,"company")
      .`with`(QueryBuilder.set("companyname",companyname))
      .where(QueryBuilder.eq("companyid",companyid))

    client.executeStatement(query)
  }

  def checkIfExist(companyid: String) : Boolean ={
    val query = QueryBuilder.select()
      .all()
      .from(_keyspace, "company")
      .where(QueryBuilder.eq("companyid", companyid))

    val iterator = client.executeStatement(query).iterator()
    if (iterator.hasNext) {
      return true
    }

    return false
  }

  def checkIfAccountExist(companyid: String,id: String) : Boolean ={
    val query = QueryBuilder.select()
      .all()
      .from(_keyspace, "account_encrypted")
      .where(QueryBuilder.eq("id",id)).and(QueryBuilder.eq("companyid",companyid))

    val iterator = client.executeStatement(query).iterator()
    if (iterator.hasNext) {
      return true
    }

    return false
  }

  def registerAccount(companyId: String,id:String, validationtoken: String, password: String, role:String): Unit =  {
    val query = QueryBuilder.update(_keyspace,"account_encrypted")
      .`with`(QueryBuilder.set("validationtoken",validationtoken))
      .and(QueryBuilder.set("validated","false"))
      .and(QueryBuilder.set("password",hash.toHashString(password)))
      .and(QueryBuilder.set("role",role))
      .where(QueryBuilder.eq("id",id)).and(QueryBuilder.eq("companyid",companyId))

    client.executeStatement(query)
  }

  def getAccount(companyId: String,id:String): Map[String,Any]={
    val result = collection.mutable.Map[String,Any]()
    if(id.length > 0) {
      val query = QueryBuilder.select()
        .all()
        .from(_keyspace, "account_encrypted")
        .where(QueryBuilder.eq("id", id)).and(QueryBuilder.eq("companyid",companyId))

      val iterator = client.executeStatement(query).iterator()
      if (iterator.hasNext) {
        val row = iterator.next()

        val definition = row.getColumnDefinitions.asList()
        (0 until definition.size()).foreach {
          index =>
            if(definition.get(index).getName == "accesstoken"){
              result(definition.get(index).getName) = encryption.decrypt(row.getBytes("accesstoken").array())
            }else if(definition.get(index).getName == "facebookapps"){
              if(row.getBytes("facebookapps") != null) result(definition.get(index).getName) = encryption.decrypt(row.getBytes("facebookapps").array())
            }else if(definition.get(index).getName == "googleapps"){
              if(row.getBytes("googleapps") != null) result(definition.get(index).getName) = encryption.decrypt(row.getBytes("googleapps").array())
            }else {
              val definitionCheck1 = definition.get(index).getType.getName + "," + DataType.varchar().getName
              val booleCheck = definition.get(index).getType.getName.toString == "set"
              val booleCheck2 = definition.get(index).getType.getName.toString == DataType.varchar().getName.toString
              if (definition.get(index).getType.getName.toString == DataType.varchar().getName.toString) {
                result(definition.get(index).getName) = row.getString(definition.get(index).getName)
              } else if (definition.get(index).getType.getName.toString == "set") {
                result(definition.get(index).getName) = row.getSet[String](definition.get(index).getName, classOf[String])
              }
            }
        }
      }
    }

    return result.toMap
  }

  def updateAccesstoken(companyId: String,id:String, accesstoken: String): Unit ={
    val query = QueryBuilder.update(_keyspace,"account_encrypted")
      .`with`(QueryBuilder.set("accesstoken",ByteBuffer.wrap(encryption.encrypt(accesstoken))))
      .where(QueryBuilder.eq("id",id)).and(QueryBuilder.eq("companyid",companyId))

    client.executeStatement(query)
  }


  def validateAccessToken(companyId: String,id:String, accesstoken: String): Boolean ={
    val accountInfo = getAccount(companyId,id)
    if(accountInfo.contains("accesstoken")){
      return accountInfo.get("accesstoken").get.toString == accesstoken
    }

    return false
  }

  def isValidLogin(companyId: String,id:String, password:String): Boolean = {
    val account = getAccount(companyId,id)

    if(account.contains("password")){
      return account.get("password").get.toString == hash.toHashString(password) && account.get("validated").get.toString == "true"
    }

    false
  }

  def validateValidationCode(companyId: String,id:String, validationCode:String, accessToken:String) : Boolean = {
    val account = getAccount(companyId,id)
    if(account.contains("validationtoken")){
      if(validationCode == account.get("validationtoken").get.toString){
        hasValidatedValidationCode(companyId,id,accessToken)
        return true
      }
    }
    false
  }

  def hasValidatedValidationCode(companyId: String,id:String, accessToken: String): Unit = {
    val query = QueryBuilder.update(_keyspace,"account_encrypted")
      .`with`(QueryBuilder.set("validated","true"))
      .and(QueryBuilder.set("accesstoken",accessToken))
      .where(QueryBuilder.eq("id",id)).and(QueryBuilder.eq("companyid",companyId))

    client.executeStatement(query)
  }

  def isValidToken(companyId: String, id: String, token: String): Boolean = {
    getAccount(companyId, id)
      .getOrElse("accesstoken","") == token
  }

  def isValidTokenAndRole(companyId:String, id:String, token: String, role: String): Boolean ={
    val accountInfo = getAccount(companyId,id)

    return accountInfo.getOrElse("accesstoken","") == token && accountInfo.getOrElse("role","") == role
  }

  def changeRole(companyId: String,id:String, newRole: String): Unit = {
    val query = QueryBuilder.update(_keyspace,"account_encrypted")
      .`with`(QueryBuilder.set("role",newRole))
      .where(QueryBuilder.eq("id",id)).and(QueryBuilder.eq("companyid",companyId))

    client.executeStatement(query)
  }

  def changePassword(companyId: String,id:String, newPassword: String): Unit = {
    val query = QueryBuilder.update(_keyspace,"account_encrypted")
      .`with`(QueryBuilder.set("password",hash.toHashString(newPassword)))
      .where(QueryBuilder.eq("id",id)).and(QueryBuilder.eq("companyid",companyId))

    client.executeStatement(query)
  }

  def removeAccount(companyId: String,id:String): Unit = {
    // Account
    val query = QueryBuilder.delete().from(_keyspace,"account_encrypted")
      .where(QueryBuilder.eq("id",id)).and(QueryBuilder.eq("companyid",companyId))

    // Resume

    // Project info?

    client.executeStatement(query)
  }

  def registerGoogleAccount(companyId: String, id:String, accesstoken: String, googleapps: String): Unit =  {
    val query = QueryBuilder.update(_keyspace,"account_encrypted")
      .`with`(QueryBuilder.set("accesstoken",ByteBuffer.wrap(encryption.encrypt(accesstoken))))
      .and(QueryBuilder.set("validated","false"))
      .and(QueryBuilder.set("googleapps",ByteBuffer.wrap(encryption.encrypt(googleapps))))
      .where(QueryBuilder.eq("id",id)).and(QueryBuilder.eq("companyid",companyId))

    client.executeStatement(query)
  }

  def registerGoogleConnect(companyId: String, id:String, registered_date: Date, accesstoken: String, googleapps: String): Unit =  {
    val query = QueryBuilder.update(_keyspace,"social_connect_google_encrypted")
      .`with`(QueryBuilder.set("accesstoken",ByteBuffer.wrap(encryption.encrypt(accesstoken))))
      .and(QueryBuilder.set("googleapps",ByteBuffer.wrap(encryption.encrypt(googleapps))))
      .and(QueryBuilder.set("registered_date",registered_date.getTime()))
      .where(QueryBuilder.eq("id",id)).and(QueryBuilder.eq("companyid",companyId))

    client.executeStatement(query)
  }

  def registerFacebookAccount(companyId: String, id:String, accesstoken: String, facebookapps: String): Unit =  {
    val query = QueryBuilder.update(_keyspace,"account_encrypted")
      .`with`(QueryBuilder.set("accesstoken",ByteBuffer.wrap(encryption.encrypt(accesstoken))))
      .and(QueryBuilder.set("validated","false"))
      .and(QueryBuilder.set("facebookapps",ByteBuffer.wrap(encryption.encrypt(facebookapps))))
      .where(QueryBuilder.eq("id",id)).and(QueryBuilder.eq("companyid",companyId))

    client.executeStatement(query)
  }

  def registerFacebookConnect(companyId: String, id:String, registered_date: Date, email: String, access_token: String, token_type: String, expires_in: Long): Unit =  {
    val query = QueryBuilder.update(_keyspace,"social_connect_facebook_encrypted")
      .`with`(QueryBuilder.set("access_token",ByteBuffer.wrap(encryption.encrypt(access_token))))
      .and(QueryBuilder.set("registered_date",registered_date.getTime()))
      .and(QueryBuilder.set("token_type",token_type))
      .and(QueryBuilder.set("expires_in",expires_in))
      .and(QueryBuilder.set("email",email))
      .where(QueryBuilder.eq("id",id)).and(QueryBuilder.eq("companyid",companyId))

    client.executeStatement(query)
  }

  def getFacebookAccount(companyId: String,id:String): Map[String,Any]={
    val result = collection.mutable.Map[String,Any]()
    if(id.length > 0) {
      val query = QueryBuilder.select()
        .all()
        .from(_keyspace, "social_connect_facebook_encrypted")
        .where(QueryBuilder.eq("id", id)).and(QueryBuilder.eq("companyid",companyId))

      val iterator = client.executeStatement(query).iterator()
      if (iterator.hasNext) {
        val row = iterator.next()

        val definition = row.getColumnDefinitions.asList()
        (0 until definition.size()).foreach {
          index =>
            val definitionCheck1 = definition.get(index).getType.getName+","+DataType.varchar().getName
            val booleCheck = definition.get(index).getType.getName.toString == "set"
            val booleCheck2 = definition.get(index).getType.getName.toString == DataType.varchar().getName.toString
            if(definition.get(index).getName == "access_token"){
              result(definition.get(index).getName) = encryption.decrypt(row.getBytes("access_token").array())
            }else {
              if (definition.get(index).getType.getName.toString == DataType.varchar().getName.toString) {
                result(definition.get(index).getName) = row.getString(definition.get(index).getName)
              } else if (definition.get(index).getType.getName.toString == "set") {
                result(definition.get(index).getName) = row.getSet[String](definition.get(index).getName, classOf[String])
              } else if (definition.get(index).getType.getName.toString == "bigint") {
                result(definition.get(index).getName) = row.getLong(definition.get(index).getName)
              } else if (definition.get(index).getType.getName.toString == "timestamp") {
                result(definition.get(index).getName) = row.getTimestamp(definition.get(index).getName)
              }
            }
        }
      }
    }

    return result.toMap
  }


  def removeFacebookConnection(companyId: String,id:String): Unit = {
    // Account
    val query = QueryBuilder.delete().from(_keyspace,"social_connect_facebook")
      .where(QueryBuilder.eq("id",id)).and(QueryBuilder.eq("companyid",companyId))

    client.executeStatement(query)
  }

  def getGoogleAccount(companyId: String,id:String): Map[String,Any]={
    val result = collection.mutable.Map[String,Any]()
    if(id.length > 0) {
      val query = QueryBuilder.select()
        .all()
        .from(_keyspace, "social_connect_google_encrypted")
        .where(QueryBuilder.eq("id", id)).and(QueryBuilder.eq("companyid",companyId))

      val iterator = client.executeStatement(query).iterator()
      if (iterator.hasNext) {
        val row = iterator.next()

        val definition = row.getColumnDefinitions.asList()
        (0 until definition.size()).foreach {
          index =>
            val definitionCheck1 = definition.get(index).getType.getName+","+DataType.varchar().getName
            val booleCheck = definition.get(index).getType.getName.toString == "set"
            val booleCheck2 = definition.get(index).getType.getName.toString == DataType.varchar().getName.toString
            if(definition.get(index).getName == "accesstoken"){
              result(definition.get(index).getName) = encryption.decrypt(row.getBytes("accesstoken").array())
            }else if(definition.get(index).getName == "googleapps"){
              result(definition.get(index).getName) = encryption.decrypt(row.getBytes("googleapps").array())
            }else{
              if (definition.get(index).getType.getName.toString == DataType.varchar().getName.toString) {
                result(definition.get(index).getName) = row.getString(definition.get(index).getName)
              } else if (definition.get(index).getType.getName.toString == "set") {
                result(definition.get(index).getName) = row.getSet[String](definition.get(index).getName, classOf[String])
              } else if (definition.get(index).getType.getName.toString == "bigint") {
                result(definition.get(index).getName) = row.getLong(definition.get(index).getName)
              } else if (definition.get(index).getType.getName.toString == "timestamp") {
                result(definition.get(index).getName) = row.getTimestamp(definition.get(index).getName)
              }
            }
        }
      }
    }

    return result.toMap
  }

}
