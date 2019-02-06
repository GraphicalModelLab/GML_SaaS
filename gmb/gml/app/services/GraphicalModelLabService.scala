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

package services

import gml.{warmupResponse, _}

trait GraphicalModelLabService {
  def init() : Unit

  def helloworld(): String

  def getModelId(algorithm: String): String
  def getExtractorId(extractorName: String): String

  // Call http://localhost:9098/helloworld to warmup this service
  def warmup(): warmupResponse

  def training(token:String, companyId:String,request: Option[trainingRequest]): trainingResponse
  def test(token: String, companyId:String,request: Option[testRequest]): testResponse
  def save(token:String, companyId:String,request: Option[saveRequest]): saveResponse
  def list(token: String, companyId:String,request: Option[listRequest]): listResponse
  def get(token:String, companyId:String,request: Option[getRequest]): getResponse
  def getModelParameter(token:String, companyId:String,request: Option[getModelParameterRequest]): getModelParameterResponse
  def search(token:String, companyId:String,request: Option[searchRequest]): searchResponse
  def getTestHistory(token:String, companyId:String,request: Option[getTestHistoryRequest]): getTestHistoryResponse
  def getModelInTestHistory(token:String, companyId:String,request: Option[getModelInHistoryRequest]): getModelInHistoryResponse
  def getListOfModels(): getListOfAvailableModelsResponse
  def getExploredGraph(token:String, companyId:String,request: Option[exploreGraphRequest]): exploreGraphResponse
  def getListOfExtractors(): getListOfAvailableExtractorsResponse
  def executeExtractor(token:String, companyId:String,request: Option[executeExtractorRequest]): executeExtractorResponse
  def getListOfCrawlerSearchEngine(): getListOfAvailableDataCrawlerSearchEngineResponse
  def executeCrawlerSearchEngine(token:String, companyId:String,request: Option[executeDataCrawlerSearchEngineRequest]): executeDataCrawlerSearchEngineResponse
  def getListOfCrawlerScrapingEngine(): getListOfAvailableDataCrawlerScrapingEngineResponse
  def executeCrawlerScrapingEngine(token:String, companyId:String,request: Option[executeDataCrawlerScrapingEngineRequest]): executeDataCrawlerScrapingEngineResponse
  def getListOfCrawlerEngine(): getListOfAvailableDataCrawlerEngineResponse
  def executeCrawlerEngine(token:String, companyId:String,request: Option[executeDataCrawlerEngineRequest]): executeDataCrawlerEngineResponse
  def getListOfHtmlConverterEngine(): getListOfAvailableHtmlConverterEngineResponse
  def executeHtmlConverterEngine(token:String, companyId:String,request: Option[executeHtmlConverterEngineRequest]): executeHtmlConverterEngineResponse
}
