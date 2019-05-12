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

import java.io.{BufferedReader, FileReader, PrintWriter}
import java.net.URLEncoder

import com.typesafe.config.ConfigFactory
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._
import org.graphicalmodellab.api.graph_api.executeDataCrawlerEngineProperty
import org.graphicalmodellab.api.{DataCrawlerEngine, DataCrawlerScrapingEngine, DataCrawlerSearchEngine}
import org.graphicalmodellab.httpclient.{HttpClient, HttpClientImpl}

import scala.collection.mutable

class YahooGeoDataCrawlerEngineRealEstate extends DataCrawlerEngine{
  val httpClient: HttpClient = new HttpClientImpl()
  val config = ConfigFactory.load("yahoo_geo_datacrawler_retail.conf")
  var appid = config.getString("appid")

  // Hold query information up to 10
  val queryCacheLimit = 20
  var queryCachePivot = 0
  var queryCache: mutable.MutableList[(String, String)] = mutable.MutableList[(String,String)]()
  (0 to queryCacheLimit).foreach(index=> queryCache :+= ("",""))

  val saveDir = "/tmp/"

  override def init(): Unit = {}

  override def getDataCrawlerEngineName: String = "YahooGeoCrawlerRealEstateV1"

  override def process(companyid: String, userid: String, datasource: String, searchEngine: DataCrawlerSearchEngine, scrapingEngine: DataCrawlerScrapingEngine,newColumns: List[executeDataCrawlerEngineProperty]): Unit = {
    val newColumnMap: mutable.Map[String, List[executeDataCrawlerEngineProperty]] = mutable.Map[String, List[executeDataCrawlerEngineProperty]]()
    newColumns.foreach {
      newColumn =>
        var properties: List[executeDataCrawlerEngineProperty] = null;
        if (newColumnMap.contains(newColumn.sourceColumn)) {
          properties = newColumnMap.get(newColumn.sourceColumn).get
        } else {
          properties = List[executeDataCrawlerEngineProperty]();
        }

        properties :+= newColumn

        newColumnMap(newColumn.sourceColumn) = properties
    }


    val writer = new PrintWriter(saveDir + "crawled_" + datasource.split("/").last, "UTF-8");

    val br = new BufferedReader(new FileReader(datasource))
    var line: String = null;
    var header = br.readLine();
    val headerTk = header.split(",");
    val newHeader = new StringBuilder();
    newHeader.append(header);

    val headerIndexMap: mutable.Map[String, Int] = mutable.Map[String, Int]()
    (0 until headerTk.size).foreach {
      index =>
        headerIndexMap(headerTk(index)) = index
    }

    newColumnMap.foreach {
      case (sourceColumn, newColumns) =>
        newColumns.foreach {
          newColumn =>
            newHeader.append(",")
            newHeader.append(newColumn.newColumnTitle)
        }
    }
    writer.println(newHeader.toString())

    line = br.readLine()
    while (line!= null) {
      // process the line.
      val body = line.split(",")

      val newLine = new StringBuilder();
      newLine.append(line);

      newColumnMap.foreach{
        case (sourceColumn, newColumns) =>

          val scrapedLinkQuery = body(headerIndexMap.get(sourceColumn).get)

          newColumns.foreach{
            newColumn =>

              var bodyString: String = ""
              if(queryCache.exists(_._1 == scrapedLinkQuery+":"+newColumn.newColumnQuery)){
                println("Found the same one in the query Cache:"+scrapedLinkQuery);
                bodyString = queryCache.find(_._1 == scrapedLinkQuery+":"+newColumn.newColumnQuery).get._2
              }else {
                println("Could not Found the same one in the query Cache:"+scrapedLinkQuery);
                println(queryCache.map((pair:(String,String)) => pair._1).toList)
                val scrapedLinks = searchEngine.process(companyid,userid,scrapedLinkQuery)

                if(scrapedLinks.size > 0) {
                  println("call "+scrapedLinks(0)+"&appid="+appid)
                  val hostParam = scrapedLinks(0).split("\\?")
                  val params = hostParam(1).split("\\&")

                  val newUrl = hostParam(0)+"?"
                  var newParams = ""

                  params.foreach(
                    param =>
                      if(param.startsWith("query")) newParams += "&query="+URLEncoder.encode(param.split("=")(1)+" "+newColumn.newColumnQuery,"UTF-8")
                      else                          newParams += "&"+param
                  )

                  bodyString = httpClient.getJson(
                    newUrl+newParams+"&appid="+appid
                  )
                }

                println("cachelimit:"+queryCacheLimit)
                if(queryCachePivot >= queryCacheLimit) {
                  queryCachePivot = 0
                }
                queryCache(queryCachePivot) = (scrapedLinkQuery+":"+newColumn.newColumnQuery, bodyString)

                queryCachePivot += 1
                // Put sleep always if we crawl data in order to avoid causing too much traffic to the site
                Thread.sleep(1000)
              }

              var newColumnData = ""
              try {
                newColumnData = scrapingEngine.processContent(companyid, userid, bodyString, newColumn.newColumnQuery)
              }catch{
                case e: Exception =>
                  println("fail to scrape the content")
                  println("content : "+bodyString)
                  println("query : "+newColumn.newColumnQuery)
              }
              newLine.append(",")
              newLine.append(newColumnData);
          }
      }

      writer.println(newLine.toString())

      line = br.readLine()
    }

    br.close()
    writer.close();
  }
}
