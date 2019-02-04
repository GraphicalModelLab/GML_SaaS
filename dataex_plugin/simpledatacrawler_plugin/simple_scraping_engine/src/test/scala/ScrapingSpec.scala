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
import org.graphicalmodellab.api.DataCrawlerScrapingEngine
import javax.xml.bind.DatatypeConverter

import org.graphicalmodellab.encryption.{AESImpl, Encryption}
import org.graphicalmodellab.plugin.SimpleScrapingEngine
import org.scalatest.FlatSpec
import org.scalatest.mockito.MockitoSugar
import org.scalatest.mockito._
import org.scalatest._
import play.api.Configuration
import org.mockito.Mockito._

class ScrapingSpec extends FlatSpec with MockitoSugar{
  "SimpleScrapingEngine" should "correctly scape the content, given REGEX PATTERN 2" in {
    val companyid="test"
    val userid="test"
    val scraping:DataCrawlerScrapingEngine = new SimpleScrapingEngine();

    val testContent = "テストーも...<>!JI住所東京都葛飾区外観.ji...";
    val query = ".*住所(.+)外観.*"
    val answer = "東京都葛飾区"
    val scrapedContent = scraping.processContent(companyid,userid,testContent,query)
    assert(answer.equals(scrapedContent))
  }

  "SimpleScrapingEngine" should "correctly scape the content, given REGEX PATTERN 1" in {
    val companyid="test"
    val userid="test"
    val scraping:DataCrawlerScrapingEngine = new SimpleScrapingEngine();

    val testContent1 = "テストーも...<>!JI駐車場有.ji...";
    val testContent2 = "テストーも...<>!JI駐車場無.ji...";

    val query = ".*駐車場(有|無).*"

    val answer1 = "有"
    val scrapedContent1 = scraping.processContent(companyid,userid,testContent1,query)
    assert(answer1.equals(scrapedContent1))

    val answer2 = "無"
    val scrapedContent2 = scraping.processContent(companyid,userid,testContent2,query)
    assert(answer2.equals(scrapedContent2))
  }
}
