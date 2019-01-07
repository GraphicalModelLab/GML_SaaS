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

package org.graphicalmodellab.auth.facebookapps

import com.google.inject.Inject
import org.graphicalmodellab.httpclient.{HttpClient, HttpClientImpl}

class FacebookAppsOpenIDConnectorImplV1 @Inject() (httpClient: HttpClient)  extends FacebookAppsOpenIDConnector{
  def getAccessToken(code: String, client_id: String, client_secret: String, redirect_uri: String): String={
    httpClient.getJson(
      s"""https://graph.facebook.com/v2.8/oauth/access_token?client_id=$client_id&redirect_uri=$redirect_uri&client_secret=$client_secret&code=$code"""
    )
  }

  def verifyAccessToken(accesstoken: String, application_accesstoken: String): String={
    httpClient.getJson(
      s"""https://graph.facebook.com/debug_token?input_token=$accesstoken&access_token=$application_accesstoken"""
    )
  }

  def getMeInfo(accesstoken: String): String={
    httpClient.getJson(
      s"""https://graph.facebook.com/me?access_token=$accesstoken&fields=id,email"""
    )
  }
}
