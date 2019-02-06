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

package controllers

import java.io.{PrintWriter, StringWriter}
import javax.inject.Inject

import com.google.inject.Singleton
import gml._
import play.api.mvc._
import org.graphicalmodellab.auth.AuthDBClient
import org.graphicalmodellab.elastic.ElasticSearchClient
import play.api.libs.json.Json
import services.{GraphicalModelLabService, GraphicalModelLabServiceImpl}
import play.api.Logger
import org.graphicalmodellab.api.graph_api._

@Singleton
class GraphicalModelLabServiceController @Inject() (gmlService: GraphicalModelLabService, components: ControllerComponents) (implicit assetsFinder: AssetsFinder) extends AbstractController(components) {

  def index() = {
    Action(request =>
      Ok(gmlService.helloworld())
    )
  }

  //http://localhost:9098/helloworld
  def helloworld() = {
    Action(request =>
      Ok(Json.toJson[warmupResponse](gmlService.warmup()))
    )
  }

  def training(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[trainingResponse](gmlService.training(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          Json.fromJson[trainingRequest](request.body.asJson.get).asOpt)))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def test(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[testResponse](gmlService.test(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          Json.fromJson[testRequest](request.body.asJson.get).asOpt)))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def save(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[saveResponse](gmlService.save(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          Json.fromJson[saveRequest](request.body.asJson.get).asOpt)))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def list(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[listResponse](gmlService.list(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          Some(listRequest(0, request.getQueryString("userid").get, companyId))
        )))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def get(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[getResponse](gmlService.get(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          Some(getRequest(0, request.getQueryString("userid").get, companyId, request.getQueryString("modelid").get))
        )))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def getModelParameter(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[getModelParameterResponse](gmlService.getModelParameter(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          Some(getModelParameterRequest(0, request.getQueryString("userid").get, companyId, request.getQueryString("algorithm").get))
        )))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def search(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[searchResponse](gmlService.search(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          Some(searchRequest(0, request.getQueryString("userid").get, companyId, request.getQueryString("query").get))
        )))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def getTestHistory(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[getTestHistoryResponse](gmlService.getTestHistory(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          Some(getTestHistoryRequest(0, request.getQueryString("userid").get, companyId, request.getQueryString("model_userid").get, request.getQueryString("modelid").get))
        )))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def getModelInTestHistory(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[getModelInHistoryResponse](gmlService.getModelInTestHistory(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          Some(getModelInHistoryRequest(0, request.getQueryString("userid").get, companyId, request.getQueryString("modelid").get, request.getQueryString("datetime").get.toLong))
        )))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def getListOfAvailableModels(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[getListOfAvailableModelsResponse](gmlService.getListOfModels(
          //          request.headers.get("Authorization").get.substring(7),
          //          companyId,
          //          Some(getListOfAvailableModelsRequest(0,request.getQueryString("userid").get,companyId))
          //          Json.fromJson[getListOfAvailableModelsRequest](request.body.asJson.get).asOpt
        )))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }


  def getExploredGraph(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[exploreGraphResponse](gmlService.getExploredGraph(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          //          Some(getListOfAvailableModelsRequest(0,request.getQueryString("userid").get,companyId))
          Json.fromJson[exploreGraphRequest](request.body.asJson.get).asOpt
        )))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def getListOfAvailableExtractors(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[getListOfAvailableExtractorsResponse](gmlService.getListOfExtractors(
          //          request.headers.get("Authorization").get.substring(7),
          //          companyId,
          //          Some(getListOfAvailableModelsRequest(0,request.getQueryString("userid").get,companyId))
          //          Json.fromJson[getListOfAvailableModelsRequest](request.body.asJson.get).asOpt
        )))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def executeExtractor(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[executeExtractorResponse](gmlService.executeExtractor(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          Json.fromJson[executeExtractorRequest](request.body.asJson.get).asOpt
        )))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def getListOfAvailableDataCrawlerSearchEngine(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[getListOfAvailableDataCrawlerSearchEngineResponse](gmlService.getListOfCrawlerSearchEngine(
          //          request.headers.get("Authorization").get.substring(7),
          //          companyId,
          //          Some(getListOfAvailableModelsRequest(0,request.getQueryString("userid").get,companyId))
          //          Json.fromJson[getListOfAvailableModelsRequest](request.body.asJson.get).asOpt
        )))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def executeCrawlerSearchEngine(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[executeDataCrawlerSearchEngineResponse](gmlService.executeCrawlerSearchEngine(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          Json.fromJson[executeDataCrawlerSearchEngineRequest](request.body.asJson.get).asOpt
        )))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def getListOfAvailableDataCrawlerScrapingEngine(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[getListOfAvailableDataCrawlerScrapingEngineResponse](gmlService.getListOfCrawlerScrapingEngine(
          //          request.headers.get("Authorization").get.substring(7),
          //          companyId,
          //          Some(getListOfAvailableModelsRequest(0,request.getQueryString("userid").get,companyId))
          //          Json.fromJson[getListOfAvailableModelsRequest](request.body.asJson.get).asOpt
        )))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def executeCrawlerScrapingEngine(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[executeDataCrawlerScrapingEngineResponse](gmlService.executeCrawlerScrapingEngine(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          Json.fromJson[executeDataCrawlerScrapingEngineRequest](request.body.asJson.get).asOpt
        )))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def getListOfAvailableDataCrawlerEngine(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[getListOfAvailableDataCrawlerEngineResponse](gmlService.getListOfCrawlerEngine(
          //          request.headers.get("Authorization").get.substring(7),
          //          companyId,
          //          Some(getListOfAvailableModelsRequest(0,request.getQueryString("userid").get,companyId))
          //          Json.fromJson[getListOfAvailableModelsRequest](request.body.asJson.get).asOpt
        )))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def executeCrawlerEngine(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[executeDataCrawlerEngineResponse](gmlService.executeCrawlerEngine(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          Json.fromJson[executeDataCrawlerEngineRequest](request.body.asJson.get).asOpt
        )))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def getListOfAvailableHtmlConverterEngine(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[getListOfAvailableHtmlConverterEngineResponse](gmlService.getListOfHtmlConverterEngine(
          //          request.headers.get("Authorization").get.substring(7),
          //          companyId,
          //          Some(getListOfAvailableModelsRequest(0,request.getQueryString("userid").get,companyId))
          //          Json.fromJson[getListOfAvailableModelsRequest](request.body.asJson.get).asOpt
        )))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }

  def executeHtmlConverterEngine(companyId: String) = {
    Action(request =>
      try
        Ok(Json.toJson[executeHtmlConverterEngineResponse](gmlService.executeHtmlConverterEngine(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          Json.fromJson[executeHtmlConverterEngineRequest](request.body.asJson.get).asOpt
        )))
      catch {
        case (err: Throwable) => {

          val sw = new StringWriter
          err.printStackTrace(new PrintWriter(sw))
          err.printStackTrace()

          BadRequest("Failure")
        }
      }
    )
  }
}
