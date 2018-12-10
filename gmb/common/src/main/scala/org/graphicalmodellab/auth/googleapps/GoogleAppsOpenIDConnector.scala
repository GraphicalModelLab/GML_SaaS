package org.graphicalmodellab.auth.googleapps

/*-
 * #%L
 * gml-common
 * %%
 * Copyright (C) 2018 Mao Ito
 * %%
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
 * #L%
 */

import org.graphicalmodellab.httprequest.HttpRequest
import com.google.api.client.googleapis.auth.oauth2.{GoogleIdToken, GoogleIdTokenVerifier}
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import org.graphicalmodellab.httprequest.HttpRequest

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
