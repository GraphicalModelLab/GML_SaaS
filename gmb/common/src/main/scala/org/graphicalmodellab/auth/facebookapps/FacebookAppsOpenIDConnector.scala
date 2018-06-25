package org.graphicalmodellab.auth.facebookapps

import org.graphicalmodellab.httprequest.HttpRequest
import org.graphicalmodellab.httprequest.HttpRequest

/**
  * Created by itomao on 1/14/17.
  */
object FacebookAppsOpenIDConnector {
  def getAccessToken(code: String, client_id: String, client_secret: String, redirect_uri: String): String={
    HttpRequest.getJson(
      s"""https://graph.facebook.com/v2.8/oauth/access_token?client_id=$client_id&redirect_uri=$redirect_uri&client_secret=$client_secret&code=$code"""
    )
  }

  def verifyAccessToken(accesstoken: String, application_accesstoken: String): String={
    HttpRequest.getJson(
      s"""https://graph.facebook.com/debug_token?input_token=$accesstoken&access_token=$application_accesstoken"""
    )
  }

  def getMeInfo(accesstoken: String): String={
    HttpRequest.getJson(
      s"""https://graph.facebook.com/me?access_token=$accesstoken&fields=id,email"""
    )
  }
}
