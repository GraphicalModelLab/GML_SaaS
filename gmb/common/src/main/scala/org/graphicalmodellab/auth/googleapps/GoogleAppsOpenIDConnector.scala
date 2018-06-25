package org.graphicalmodellab.auth.googleapps

import org.graphicalmodellab.httprequest.HttpRequest
import com.google.api.client.googleapis.auth.oauth2.{GoogleIdToken, GoogleIdTokenVerifier}
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import org.graphicalmodellab.httprequest.HttpRequest
/**
  * Created by itomao on 1/14/17.
  */
object GoogleAppsOpenIDConnector {

  def getAccessToken(code: String, client_id: String, client_secret: String, redirect_uri: String, grant_type: String="authorization_code"): String={
    HttpRequest.postParams("https://accounts.google.com/o/oauth2/token",
      Seq[(String,String)](
        ("code",code),
        ("client_id",client_id),
        ("client_secret",client_secret),
        ("redirect_uri",redirect_uri),
        ("grant_type",grant_type)
      )
    )
  }

  def decodeToken(token: String) : Map[String,String] = {
    val idToken: GoogleIdToken = GoogleIdToken.parse(new JacksonFactory(), token)
    val verifier = new GoogleIdTokenVerifier(new NetHttpTransport(), new JacksonFactory())

    if(idToken.verify(verifier)){
      val payload = idToken.getPayload
      return Map[String, String] (
        "email" -> payload.getEmail
      )
    }

    Map[String, String]()
  }
}
