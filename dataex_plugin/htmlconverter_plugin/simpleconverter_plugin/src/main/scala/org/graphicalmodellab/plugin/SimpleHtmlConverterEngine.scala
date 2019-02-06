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

import java.io.PrintWriter
import java.util.concurrent.atomic.AtomicInteger

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.graphicalmodellab.api.HtmlConverterEngine
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{allText => _, elementList => _, _}


class SimpleHtmlConverterEngine extends HtmlConverterEngine{
  val saveDir = "/tmp/"
  var file_counter:AtomicInteger = new AtomicInteger(0)
  override def init(): Unit = {}

  override def getHtmlConverterEngineName: String = "TableToCSVConverter"

  override def getSupportedTargetFormat: List[String] = {
    List[String](
      HtmlConverterEngine.TARGET_FORMAT_CSV
    )
  }

  override def convert(companyid: String, userid: String, content: String): Unit = {
    file_counter.addAndGet(1);

    val writer = new PrintWriter(saveDir + "converted_file_" + file_counter.get()+".csv", "UTF-8");

    val jsoupBrowser = JsoupBrowser()

    val trs: List[Element] = jsoupBrowser.parseString(content) >> elementList("table tbody tr")

    println()
    trs.foreach{
      tr =>
        val ths: List[Element] = tr >> elementList("th")

        if(ths.length > 0) {
          val tdText = ths(0) >> allText("th")

          writer.print(tdText)
          (1 until ths.length).foreach {
            index =>
              val tdText = ths(index) >> allText("th")
              writer.print("," + tdText)
          }

          writer.println()
        }

        val tds: List[Element] = tr >> elementList("td")

        if(tds.length > 0) {
          val tdText = tds(0) >> allText("th")

          writer.print(tdText)
          (1 until tds.length).foreach {
            index =>
              val tdText = tds(index) >> allText("td")
              writer.print("," + tdText)
          }

          writer.println()
        }
    }

    writer.close();
  }
}
