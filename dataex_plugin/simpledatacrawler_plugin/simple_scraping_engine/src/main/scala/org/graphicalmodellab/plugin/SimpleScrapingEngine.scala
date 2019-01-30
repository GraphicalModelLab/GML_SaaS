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

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.graphicalmodellab.api.DataCrawlerScrapingEngine
import org.graphicalmodellab.httpclient.{HttpClient, HttpClientImpl}

import scala.util.matching.Regex

class SimpleScrapingEngine extends DataCrawlerScrapingEngine{
  val httpClient: HttpClient = new HttpClientImpl()

  override def init(): Unit = {}

  override def getScrapingEngineName: String = "SimpleScraping"

  override def processUrl(companyid: String, userid: String, url:String, query: String): String = {
    val jsoupBrowser = JsoupBrowser()
    val bodyString = jsoupBrowser.parseString(httpClient.getRawHtml(url)) >> allText("body");

    return processContent(companyid,userid,bodyString,query)
  }
  override def processContent(companyid: String, userid: String, text:String, query: String): String = {
    val Pattern = s"""$query""".r

    val Pattern(output) = text
    return output
  }
}
