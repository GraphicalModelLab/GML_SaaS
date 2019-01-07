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

package org.graphicalmodellab.httpclient

import scalaj.http.{Http, HttpOptions, HttpResponse}

class HttpClientImpl extends HttpClient{
  def postJson(url: String, jsonString: String) : String= {
      Http(url).postData(jsonString)
        .header("Content-Type", "application/json")
        .header("Charset", "UTF-8")
        .option(HttpOptions.readTimeout(10000)).asString.body
  }

  def postParams(url: String, params: Seq[(String,String)]) : String= {
    Http(url).postForm(params).asString.body
  }

  def getJson(url: String) : String= {
    Http(url).option(HttpOptions.readTimeout(10000)).asString.body
  }
}
