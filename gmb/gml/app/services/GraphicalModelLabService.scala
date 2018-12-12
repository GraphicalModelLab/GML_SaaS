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

import gml._
import org.graphicalmodellab.api.{DataExtractor, Model}
import org.graphicalmodellab.api.graph_api.graph
import org.graphicalmodellab.auth.AuthDBClient
import play.Play
import play.api.Logger
import play.api.http.Status
import play.api.libs.json.Json

import scala.collection.JavaConverters._
import scala.collection.mutable

class GraphicalModelLabService {
  var listOfModel: mutable.ListBuffer[String] = mutable.ListBuffer[String]()
  var listOfExtractors: mutable.ListBuffer[String] = mutable.ListBuffer[String]()
  var modelMap: mutable.Map[String,Model] = mutable.Map[String,Model]()
  var extractorMap: mutable.Map[String,DataExtractor] = mutable.Map[String,DataExtractor]()

  Logger.info("Setup Connection to DB and Elastic Search..")
  GmlDBClient.init(List[String]("localhost"));
  AuthDBClient.init(List[String]("localhost"));
  GmlElasticSearchClient.init("localhost");

  def getModelId(algorithm: String): String = algorithm;
  def getExtractorId(extractorName: String): String = extractorName;

  // Call http://localhost:9098/helloworld to warmup this service
  def warmup(): warmupResponse = {

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
    Logger.info("Loading/Initialize Available Extractors..")
    var extractorList = mutable.ListBuffer[String]()
    val extractors = (ServiceLoader load classOf[org.graphicalmodellab.api.DataExtractor]).asScala

    for (w <- extractors) {
      println("Loading Extractor : "+w.getExtractorName+"..")
      extractorList += w.getExtractorName
      w.init()
      extractorMap.put(getExtractorId(w.getExtractorName),w)
    }
    listOfExtractors = extractorList

    return warmupResponse(Status.OK)
  }

  def training(token:String, companyId:String,request: Option[trainingRequest]): trainingResponse = {

    request match {
      case Some(request)=>
        if(AuthDBClient.isValidToken(companyId,request.userid,token)) {

          val model: Model = modelMap.get(getModelId(request.graph.algorithm)).get

          model.training(request.graph,request.datasource)

          GmlDBClient.saveTrainingHistory(request)
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
        if(AuthDBClient.isValidToken(companyId,request.userid,token)) {

          val model: Model = modelMap.get(getModelId(request.graph.algorithm)).get
          if(request.evaluationMethod == Model.EVALUATION_METHOD_SIMPLE) {
            val accuracy = model.testSimple(request.graph,request.testsource, request.targetLabel)
            val accuracySummary = GmlDBClient.saveTestHistory(request, accuracy)

            return testResponse(Status.INTERNAL_SERVER_ERROR, 1, "", accuracySummary.toString)
          }else if(request.evaluationMethod == Model.EVALUATION_METHOD_CROSS_VALIDATION){
            val K = 10;

            val accuracy = model.testByCrossValidation(request.graph,request.testsource, request.targetLabel,K)
            val accuracySummary = GmlDBClient.saveTestHistory(request, accuracy)

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
        if(AuthDBClient.isValidToken(companyId,request.userid,token)) {
          val timestamp = GmlDBClient.save(request)
          GmlElasticSearchClient.addDocument(request)
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
        if(AuthDBClient.isValidToken(companyId,request.userid,token)) {
          return GmlDBClient.list(request)
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
        if(AuthDBClient.isValidToken(companyId,request.userid,token)) {
          return GmlDBClient.get(request)
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
        if(AuthDBClient.isValidToken(companyId,request.userid,token)) {

          val model: Model = modelMap.get(getModelId(request.algorithm)).get

          return getModelParameterResponse(
              Status.OK, 1, request.algorithm, model.getModelParameterInfo,model.getSupportedEvaluationMethod)
        }else{
          return getModelParameterResponse(Status.UNAUTHORIZED, 1, null, List[String](),List[String]())
        }
      case None =>
        println("No request")
    }
    return getModelParameterResponse(Status.INTERNAL_SERVER_ERROR, 1, null, List[String](),List[String]())
  }

  def search(token:String, companyId:String,request: Option[searchRequest]): searchResponse = {

    request match {
      case Some(request)=>
        if(AuthDBClient.isValidToken(companyId,request.userid,token)) {
          val result = GmlElasticSearchClient.searchDocument(request.query)
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
        if(AuthDBClient.isValidToken(companyId,request.userid,token)) {
          val testHistory = GmlDBClient.getTestHistory(request)

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
        if(AuthDBClient.isValidToken(companyId,request.userid,token)) {
          return GmlDBClient.getModelInHistory(request)
        }else{
          return getModelInHistoryResponse(Status.UNAUTHORIZED, 1, null)
        }
      case None =>
        println("No request")
    }
    return getModelInHistoryResponse(Status.INTERNAL_SERVER_ERROR, 1, null)
  }

  def getListOfModels(): getListOfAvailableModelsResponse={
    return getListOfAvailableModelsResponse(Status.OK,listOfModel.toList)
  }

  def getExploredGraph(token:String, companyId:String,request: Option[exploreGraphRequest]): exploreGraphResponse={

    request match {
      case Some(request)=>
        if(AuthDBClient.isValidToken(companyId,request.userid,token)) {

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
        if(AuthDBClient.isValidToken(companyId,request.userid,token)) {

          val extractor: DataExtractor = extractorMap.get(request.extractorId).get

          extractor.process(companyId,request.userid)

          return executeExtractorResponse(Status.OK)
        }else{
          return executeExtractorResponse(Status.UNAUTHORIZED)
        }
      case None =>
        println("No request")
    }


    return executeExtractorResponse(Status.INTERNAL_SERVER_ERROR)
  }

}
