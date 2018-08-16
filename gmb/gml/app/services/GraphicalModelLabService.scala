package services

import java.util.ServiceLoader

import gml._
import org.graphicalmodellab.auth.AuthDBClient
import play.Play
import play.api.http.Status
import scala.collection.JavaConverters._

import scala.collection.mutable

/**
 * Created by ito_m on 9/11/16.
 */
class GraphicalModelLabService {
  val config = Play.application().configuration()
  var listOfModel: List[String] = null
  var modelMap: mutable.Map[String,Model] = mutable.Map[String,Model]()

  GmlDBClient.init(List[String]("localhost"));
  AuthDBClient.init(List[String]("localhost"));
  GmlElasticSearchClient.init("localhost");

  val TrainedModelMap: mutable.Map[String,Model] = mutable.Map[String,Model]()

  def training(token:String, companyId:String,request: Option[trainingRequest]): trainingResponse = {

    request match {
      case Some(request)=>
        if(AuthDBClient.isValidToken(companyId,request.userid,token)) {

          val model: Model = modelMap.get(request.graph.algorithm).get

          model.setup(SparkContext.sparkConf,SparkContext.sparkSession,request.graph.edges,request.graph.nodes,request.graph.commonProperties)
          model.training(request.datasource)
          TrainedModelMap.put("test",model)

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

          if(request.evaluationMethod == "simple") {
            val model: Model = TrainedModelMap.get("test").get
            val accuracy = model.testSimple(request.testsource, request.targetLabel)
            val accuracySummary = GmlDBClient.saveTestHistory(request, accuracy)

            return testResponse(Status.INTERNAL_SERVER_ERROR, 1, "", accuracySummary.toString)
          }else if(request.evaluationMethod == "cross-validation"){
            val K = 10;
            val model: Model = new ModelSimpleCSV();
            model.setup(SparkContext.sparkConf,SparkContext.sparkSession,request.graph.edges,request.graph.nodes,request.graph.commonProperties)

            val accuracy = model.testByCrossValidation(request.testsource, request.targetLabel,K)
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

  def getListOfModels(token:String, companyId: String, request: Option[getListOfAvailableModelsRequest]): getListOfAvailableModelsResponse={
    if(listOfModel == null) {
      var list = mutable.ListBuffer[String]()
      val ws = (ServiceLoader load classOf[Model]).asScala

      for (w <- ws) {
        list += w.getModelName
        modelMap.put(w.getModelName,w)
      }
      listOfModel = list.to
    }

    return getListOfAvailableModelsResponse(Status.OK,listOfModel.toList)
  }
}
