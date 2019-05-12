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


package org.graphicalmodellab.plugin

import java.net.URLEncoder

import com.typesafe.config.ConfigFactory
import org.codehaus.jettison.json.JSONObject
import org.graphicalmodellab.api.DataCrawlerSearchEngine
import org.graphicalmodellab.httpclient.{HttpClient, HttpClientImpl}

class YahooGeoSearchEngineRealEstate  extends DataCrawlerSearchEngine{
  val httpClient: HttpClient = new HttpClientImpl()

  override def init(): Unit = {}

  override def getSearchEngineName: String = "YahooGeoSearchEngineRealEstateV1"

  override def process(companyid: String, userid: String, query: String): List[String] = {
//    val encodedQuery = URLEncoder.encode(query,"UTF-8")
    return List[String]( s"""https://map.yahooapis.jp/search/local/V1/localSearch?query=$query&output=json""")
  }

}
