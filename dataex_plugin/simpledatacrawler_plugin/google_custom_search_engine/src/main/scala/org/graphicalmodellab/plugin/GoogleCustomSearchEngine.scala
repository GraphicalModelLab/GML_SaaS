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

class GoogleCustomSearchEngine extends DataCrawlerSearchEngine{
  val httpClient: HttpClient = new HttpClientImpl()
  val config = ConfigFactory.load("google_custom_search_engine.conf")

  val cx = config.getString("cx")
  val key = config.getString("key")

  override def init(): Unit = {}

  override def getSearchEngineName: String = "Google"

  override def process(companyid: String, userid: String, query: String): List[String] = {
    val encodedQuery = URLEncoder.encode(query,"UTF-8")

    println("call to "+s"""https://www.googleapis.com/customsearch/v1?q=$encodedQuery&cx=$cx&key=$key""")
    val content = httpClient.getJson(
      s"""https://www.googleapis.com/customsearch/v1?q=$encodedQuery&cx=$cx&key=$key"""
    )
    val json = new JSONObject(content)

    if(json.has("items")){
      val items = json.getJSONArray("items")
      if(items.length() > 0){
        val firstItem = items.getJSONObject(0);

        return List[String](firstItem.getString("link"))
      }
    }else if(json.has("spelling")){
      val encodedRewriteQuery = URLEncoder.encode(json.getJSONObject("spelling").getString("correctedQuery"),"UTF-8")
      println("rewrite call to "+s"""https://www.googleapis.com/customsearch/v1?q=$encodedRewriteQuery&cx=$cx&key=$key""")
      val rewriteContent = httpClient.getJson(
        s"""https://www.googleapis.com/customsearch/v1?q=$encodedQuery&cx=$cx&key=$key"""
      )
      val rewriteJSON = new JSONObject(rewriteContent)
      if(rewriteJSON.has("items")) {
        val items = rewriteJSON.getJSONArray("items")
        if (items.length() > 0) {
          val firstItem = items.getJSONObject(0);

          return List[String](firstItem.getString("link"))
        }
      }

      if(rewriteJSON.has("error")){
        println("Error Occured")
        println(json)
      }
    }

    if(json.has("error")){
      println("Error Occured")
      println(json)
    }

    return List[String]()
  }
}
