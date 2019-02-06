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

import java.util.ServiceLoader

import com.google.inject.Inject
import gml._
import org.graphicalmodellab.api._
import org.graphicalmodellab.api.graph_api.graph
import org.graphicalmodellab.auth.AuthDBClient
import play.api.{Configuration, Logger}
import play.api.http.Status
import play.api.libs.json.Json

import scala.collection.JavaConverters._
import scala.collection.mutable

class GraphicalModelLabServiceImpl @Inject() (config: Configuration,gmlDBClient: GmlDBClient, gmlElasticSearchClient:GmlElasticSearchClient,authDBClient:AuthDBClient) extends GraphicalModelLabService{
  var listOfModel: mutable.ListBuffer[String] = mutable.ListBuffer[String]()
  var modelMap: mutable.Map[String,Model] = mutable.Map[String,Model]()

  var listOfExtractors: mutable.ListBuffer[String] = mutable.ListBuffer[String]()
  var extractorMap: mutable.Map[String,DataExtractor] = mutable.Map[String,DataExtractor]()

  var listOfDataCrawlerSearchEngines: mutable.ListBuffer[String] = mutable.ListBuffer[String]()
  var dataCrawlerSearchEngineMap: mutable.Map[String,DataCrawlerSearchEngine] = mutable.Map[String,DataCrawlerSearchEngine]()

  var listOfDataCrawlerScrapingEngines: mutable.ListBuffer[String] = mutable.ListBuffer[String]()
  var dataCrawlerScrapingEngineMap: mutable.Map[String,DataCrawlerScrapingEngine] = mutable.Map[String,DataCrawlerScrapingEngine]()

  var listOfDataCrawlerEngines: mutable.ListBuffer[String] = mutable.ListBuffer[String]()
  var dataCrawlerEngineMap: mutable.Map[String,DataCrawlerEngine] = mutable.Map[String,DataCrawlerEngine]()

  var listOfHtmlConverterEngines: mutable.ListBuffer[String] = mutable.ListBuffer[String]()
  var htmlConverterEngineMap: mutable.Map[String,HtmlConverterEngine] = mutable.Map[String,HtmlConverterEngine]()

  def helloworld(): String = "hello impl"

  def init() {
    Logger.info("Setup Connection to DB and Elastic Search..")
    gmlDBClient.init(config.get[String]("gml.cassandra.keyspace"),List[String](config.get[String]("gml.cassandra.host")));
    authDBClient.init(config.get[String]("auth.cassandra.keyspace"),List[String](config.get[String]("auth.cassandra.host")));
    gmlElasticSearchClient.init(config.get[String]("gml.elasticsearch.host"));
  }

  def getModelId(algorithm: String): String = algorithm;
  def getExtractorId(extractorName: String): String = extractorName;

  // Call http://localhost:9098/helloworld to warmup this service
  @Inject
  def warmup(): warmupResponse = {
    init()

    // Initialize Available Models
    Logger.info("Loading/Initialize Available Models..")
    var modelList = mutable.ListBuffer[String]()
    val models = (ServiceLoader load classOf[org.graphicalmodellab.api.Model]).asScala

    for (w <- models) {
      println("Loading Model : "+w.getModelName+"..")
      modelList += w.getModelName
      w.init()
      modelMap.put(getModelId(w.getModelName),w)
    }
    listOfModel = modelList

    // Initialize Available Extractors
    println("Loading/Initialize Available Extractors..")
    var extractorList = mutable.ListBuffer[String]()
    val extractors = (ServiceLoader load classOf[org.graphicalmodellab.api.DataExtractor]).asScala

    for (w <- extractors) {
      println("Loading Extractor : "+w.getExtractorName+"..")
      extractorList += w.getExtractorName
      w.init()
      extractorMap.put(getExtractorId(w.getExtractorName),w)
    }
    listOfExtractors = extractorList

    println("Loading/Initialize Available Data Crawler Search Engines..")
    var searchEngineList = mutable.ListBuffer[String]()
    val searchEngines = (ServiceLoader load classOf[org.graphicalmodellab.api.DataCrawlerSearchEngine]).asScala

    for (w <- searchEngines) {
      println("Loading Data Crawler Search Engine : "+w.getSearchEngineName+"..")
      searchEngineList += w.getSearchEngineName
      w.init()
      dataCrawlerSearchEngineMap.put(getExtractorId(w.getSearchEngineName),w)
    }
    listOfDataCrawlerSearchEngines = searchEngineList

    println("Loading/Initialize Available Data Crawler Scraping Engines..")
    var scrapingEngineList = mutable.ListBuffer[String]()
    val scrapingEngines = (ServiceLoader load classOf[org.graphicalmodellab.api.DataCrawlerScrapingEngine]).asScala

    for (w <- scrapingEngines) {
      println("Loading Data Crawler Scraping Engine : "+w.getScrapingEngineName+"..")
      scrapingEngineList += w.getScrapingEngineName
      w.init()
      dataCrawlerScrapingEngineMap.put(getExtractorId(w.getScrapingEngineName),w)
    }
    listOfDataCrawlerScrapingEngines = scrapingEngineList

    println("Loading/Initialize Available Data Crawler Engines..")
    var crawlerEngineList = mutable.ListBuffer[String]()
    val crawlerEngines = (ServiceLoader load classOf[org.graphicalmodellab.api.DataCrawlerEngine]).asScala

    for (w <- crawlerEngines) {
      println("Loading Data Crawler Engine : "+w.getDataCrawlerEngineName+"..")
      crawlerEngineList += w.getDataCrawlerEngineName
      w.init()
      dataCrawlerEngineMap.put(getExtractorId(w.getDataCrawlerEngineName),w)
    }
    listOfDataCrawlerEngines = crawlerEngineList

    println("Loading/Initialize Available Data Crawler Engines..")
    var htmlConverterEngineList = mutable.ListBuffer[String]()
    val htmlConverterEngines = (ServiceLoader load classOf[org.graphicalmodellab.api.HtmlConverterEngine]).asScala

    for (w <- htmlConverterEngines) {
      println("Loading Html Converter Engine : "+w.getHtmlConverterEngineName+"..")
      htmlConverterEngineList += w.getHtmlConverterEngineName
      w.init()
      htmlConverterEngineMap.put(getExtractorId(w.getHtmlConverterEngineName),w)
    }
    listOfHtmlConverterEngines = htmlConverterEngineList

    return warmupResponse(Status.OK)
  }

  def training(token:String, companyId:String,request: Option[trainingRequest]): trainingResponse = {

    request match {
      case Some(request)=>
        if(authDBClient.isValidToken(companyId,request.userid,token)) {

          val model: Model = modelMap.get(getModelId(request.graph.algorithm)).get

          model.training(request.graph,request.datasource)

          gmlDBClient.saveTrainingHistory(request)
        }else{
          return trainingResponse(Status.UNAUTHORIZED, 1,"")
        }

      case None =>
        println("No request")
    }
    return trainingResponse(Status.INTERNAL_SERVER_ERROR, 1,"")
  }

  def test(token: String, companyId:String,request: Option[testRequest]): testResponse = {

    request match {
      case Some(request)=>
        if(authDBClient.isValidToken(companyId,request.userid,token)) {

          val model: Model = modelMap.get(getModelId(request.graph.algorithm)).get
          if(request.evaluationMethod == Model.EVALUATION_METHOD_SIMPLE) {
            val accuracy = model.testSimple(request.graph,request.testsource, request.targetLabel)
            val accuracySummary = gmlDBClient.saveTestHistory(request, accuracy)

            return testResponse(Status.INTERNAL_SERVER_ERROR, 1, "", accuracySummary.toString)
          }else if(request.evaluationMethod == Model.EVALUATION_METHOD_CROSS_VALIDATION){
            val K = 10;

            val accuracy = model.testByCrossValidation(request.graph,request.testsource, request.targetLabel,K)
            val accuracySummary = gmlDBClient.saveTestHistory(request, accuracy)

            print("Cross validated result:"+ accuracy)
            return testResponse(Status.INTERNAL_SERVER_ERROR, 1, "", accuracySummary.toString)

          }else{

          }

        }else{
          return testResponse(Status.UNAUTHORIZED, 1,"","")
        }

      case None =>
        println("No request")
    }
    return testResponse(Status.INTERNAL_SERVER_ERROR, 1,"","")
  }

  def save(token:String, companyId:String,request: Option[saveRequest]): saveResponse = {

    request match {
      case Some(request)=>
        if(authDBClient.isValidToken(companyId,request.userid,token)) {
          val timestamp = gmlDBClient.save(request)
          gmlElasticSearchClient.addDocument(request)
        }else{
          return saveResponse(Status.UNAUTHORIZED, 1)
        }

        return saveResponse(Status.OK, 1)
      case None =>
        println("No request")
    }
    return saveResponse(Status.INTERNAL_SERVER_ERROR, 1)
  }

  def list(token: String, companyId:String,request: Option[listRequest]): listResponse = {

    request match {
      case Some(request)=>
        if(authDBClient.isValidToken(companyId,request.userid,token)) {
          return gmlDBClient.list(request)
        }else{
          return listResponse(Status.UNAUTHORIZED, 1, List[String]())
        }
      case None =>
        println("No request")
    }
    return listResponse(Status.INTERNAL_SERVER_ERROR, 1, List[String]())
  }

  def get(token:String, companyId:String,request: Option[getRequest]): getResponse = {

    request match {
      case Some(request)=>
        if(authDBClient.isValidToken(companyId,request.userid,token)) {
          return gmlDBClient.get(request)
        }else{
          return getResponse(Status.UNAUTHORIZED, 1, null)
        }
      case None =>
        println("No request")
    }
    return getResponse(Status.INTERNAL_SERVER_ERROR, 1, null)
  }

  def getModelParameter(token:String, companyId:String,request: Option[getModelParameterRequest]): getModelParameterResponse = {

    request match {
      case Some(request)=>
        if(authDBClient.isValidToken(companyId,request.userid,token)) {

          if(modelMap.contains(request.algorithm)) {
            val model: Model = modelMap.get(getModelId(request.algorithm)).get

            return getModelParameterResponse(
              Status.OK, 1, request.algorithm, model.getModelParameterInfo, model.getSupportedEvaluationMethod, model.getSupportedShape)
          }else{
            return getModelParameterResponse(STATUS_MODEL_ALGORITHM_NOT_FOUND, 1, null, List[String](),List[String](),List[String]())
          }
        }else{
          return getModelParameterResponse(Status.UNAUTHORIZED, 1, null, List[String](),List[String](),List[String]())
        }
      case None =>
        println("No request")
    }
    return getModelParameterResponse(Status.INTERNAL_SERVER_ERROR, 1, null, List[String](),List[String](),List[String]())
  }

  def search(token:String, companyId:String,request: Option[searchRequest]): searchResponse = {

    request match {
      case Some(request)=>
        if(authDBClient.isValidToken(companyId,request.userid,token)) {
          val result = gmlElasticSearchClient.searchDocument(request.query)
          return searchResponse(Status.OK, 1, result)
        }else{
          return searchResponse(Status.UNAUTHORIZED, 1, "[]")
        }
      case None =>
        println("No request")
    }
    return searchResponse(Status.INTERNAL_SERVER_ERROR, 1, "[]")
  }

  def getTestHistory(token:String, companyId:String,request: Option[getTestHistoryRequest]): getTestHistoryResponse = {

    request match {
      case Some(request)=>
        if(authDBClient.isValidToken(companyId,request.userid,token)) {
          val testHistory = gmlDBClient.getTestHistory(request)

          return new getTestHistoryResponse(
            Status.OK,
            Status.OK,
            testHistory.toList
          )
        }else{
          return getTestHistoryResponse(Status.UNAUTHORIZED, 1, List[String]())
        }
      case None =>
        println("No request")
    }
    return getTestHistoryResponse(Status.INTERNAL_SERVER_ERROR, 1, List[String]())
  }
  def getModelInTestHistory(token:String, companyId:String,request: Option[getModelInHistoryRequest]): getModelInHistoryResponse = {

    request match {
      case Some(request)=>
        if(authDBClient.isValidToken(companyId,request.userid,token)) {
          return gmlDBClient.getModelInHistory(request)
        }else{
          return getModelInHistoryResponse(Status.UNAUTHORIZED, 1, null)
        }
      case None =>
        println("No request")
    }
    return getModelInHistoryResponse(Status.INTERNAL_SERVER_ERROR, 1, null)
  }

  def getListOfModels(): getListOfAvailableModelsResponse={
    if(listOfModel.size > 0) {
      return getListOfAvailableModelsResponse(Status.OK, listOfModel.toList)
    }else{
      return getListOfAvailableModelsResponse(STATUS_MODEL_ALGORITHM_ANY_PLUGIN_NOT_FOUND, listOfModel.toList)
    }
  }

  def getExploredGraph(token:String, companyId:String,request: Option[exploreGraphRequest]): exploreGraphResponse={

    request match {
      case Some(request)=>
        if(authDBClient.isValidToken(companyId,request.userid,token)) {

          val model: Model = modelMap.get(getModelId(request.graph.algorithm)).get

          val newGraph: (graph, Double) = model.exploreStructure(request.graph, request.targetLabel,request.datasource);

          return exploreGraphResponse(Status.OK, 1, Json.stringify(Json.toJson(newGraph._1)), newGraph._2)
        }else{
          return exploreGraphResponse(Status.UNAUTHORIZED, 1, "", 0.0)
        }
      case None =>
        println("No request")
    }


    return exploreGraphResponse(Status.INTERNAL_SERVER_ERROR, 1, "", 0.0 )
  }

  def getListOfExtractors(): getListOfAvailableExtractorsResponse={
    val extractorParamMap = collection.mutable.Map[String,List[String]]()
    listOfExtractors.toList.foreach{
      id=>
        extractorParamMap.put(id,extractorMap.get(id).get.getExtractorParameterInfo)

    }
    return getListOfAvailableExtractorsResponse(Status.OK,listOfExtractors.toList,extractorParamMap.toMap)
  }

  def executeExtractor(token:String, companyId:String,request: Option[executeExtractorRequest]): executeExtractorResponse={

    request match {
      case Some(request)=>
        if(authDBClient.isValidToken(companyId,request.userid,token)) {

          val extractor: DataExtractor = extractorMap.get(request.extractorId).get

          extractor.process(companyId,request.userid, Map[String,Any](),Map[String,Any]())

          return executeExtractorResponse(Status.OK)
        }else{
          return executeExtractorResponse(Status.UNAUTHORIZED)
        }
      case None =>
        println("No request")
    }


    return executeExtractorResponse(Status.INTERNAL_SERVER_ERROR)
  }

  def getListOfCrawlerSearchEngine(): getListOfAvailableDataCrawlerSearchEngineResponse={
    val searchEngineParamMap = collection.mutable.Map[String,List[String]]()
    return getListOfAvailableDataCrawlerSearchEngineResponse(Status.OK,listOfDataCrawlerSearchEngines.toList,searchEngineParamMap.toMap)
  }

  def executeCrawlerSearchEngine(token:String, companyId:String,request: Option[executeDataCrawlerSearchEngineRequest]): executeDataCrawlerSearchEngineResponse={

    request match {
      case Some(request)=>
        if(authDBClient.isValidToken(companyId,request.userid,token)) {

          val searchEngine: DataCrawlerSearchEngine = dataCrawlerSearchEngineMap.get(request.searchEngineId).get

          val urls:List[String] = searchEngine.process(companyId,request.userid, request.query)

          return executeDataCrawlerSearchEngineResponse(Status.OK,urls,urls)
        }else{
          return executeDataCrawlerSearchEngineResponse(Status.UNAUTHORIZED,null,null)
        }
      case None =>
        println("No request")
    }


    return executeDataCrawlerSearchEngineResponse(Status.INTERNAL_SERVER_ERROR,null,null)
  }

  def getListOfCrawlerScrapingEngine(): getListOfAvailableDataCrawlerScrapingEngineResponse={
    val scrapingEngineParamMap = collection.mutable.Map[String,List[String]]()
    return getListOfAvailableDataCrawlerScrapingEngineResponse(Status.OK,listOfDataCrawlerScrapingEngines.toList,scrapingEngineParamMap.toMap)
  }

  def executeCrawlerScrapingEngine(token:String, companyId:String,request: Option[executeDataCrawlerScrapingEngineRequest]): executeDataCrawlerScrapingEngineResponse={

    request match {
      case Some(request)=>
        if(authDBClient.isValidToken(companyId,request.userid,token)) {

          val scrapingEngine: DataCrawlerScrapingEngine = dataCrawlerScrapingEngineMap.get(request.scrapingEngineId).get

          val data: String = scrapingEngine.processUrl(companyId,request.userid, request.url, request.query)

          return executeDataCrawlerScrapingEngineResponse(Status.OK,data)
        }else{
          return executeDataCrawlerScrapingEngineResponse(Status.UNAUTHORIZED,null)
        }
      case None =>
        println("No request")
    }


    return executeDataCrawlerScrapingEngineResponse(Status.INTERNAL_SERVER_ERROR,null)
  }

  def getListOfCrawlerEngine(): getListOfAvailableDataCrawlerEngineResponse={
    val crawlerEngineParamMap = collection.mutable.Map[String,List[String]]()
    return getListOfAvailableDataCrawlerEngineResponse(Status.OK,listOfDataCrawlerEngines.toList,crawlerEngineParamMap.toMap)
  }

  def executeCrawlerEngine(token:String, companyId:String,request: Option[executeDataCrawlerEngineRequest]): executeDataCrawlerEngineResponse={

    request match {
      case Some(request)=>
        if(authDBClient.isValidToken(companyId,request.userid,token)) {

          val crawlerEngine: DataCrawlerEngine = dataCrawlerEngineMap.get(request.crawlerEngineId).get
          val scrapingEngine: DataCrawlerScrapingEngine = dataCrawlerScrapingEngineMap.get(request.scrapingEngineId).get
          val searchEngine: DataCrawlerSearchEngine = dataCrawlerSearchEngineMap.get(request.searchEngineId).get

          crawlerEngine.process(
            companyId,
            request.userid,
            request.datasource,
            searchEngine,
            scrapingEngine,
            request.newColumns
          )

          return executeDataCrawlerEngineResponse(Status.OK)
        }else{
          return executeDataCrawlerEngineResponse(Status.UNAUTHORIZED)
        }
      case None =>
        println("No request")
    }


    return executeDataCrawlerEngineResponse(Status.INTERNAL_SERVER_ERROR)
  }

  def getListOfHtmlConverterEngine(): getListOfAvailableHtmlConverterEngineResponse={
    val crawlerEngineParamMap = collection.mutable.Map[String,List[String]]()
    return getListOfAvailableHtmlConverterEngineResponse(Status.OK,listOfHtmlConverterEngines.toList,crawlerEngineParamMap.toMap)
  }

  def executeHtmlConverterEngine(token:String, companyId:String,request: Option[executeHtmlConverterEngineRequest]): executeHtmlConverterEngineResponse={

    request match {
      case Some(request)=>
        if(authDBClient.isValidToken(companyId,request.userid,token)) {

          val htmlConverterEngine: HtmlConverterEngine = htmlConverterEngineMap.get(request.converterId).get
          htmlConverterEngine.convert(
            companyId,
            request.userid,
            request.content
          )

          return executeHtmlConverterEngineResponse(Status.OK)
        }else{
          return executeHtmlConverterEngineResponse(Status.UNAUTHORIZED)
        }
      case None =>
        println("No request")
    }


    return executeHtmlConverterEngineResponse(Status.INTERNAL_SERVER_ERROR)
  }
}
