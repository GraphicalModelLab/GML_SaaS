package services

import gml._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.codehaus.jettison.json.JSONObject
import org.graphicalmodellab.auth.AuthDBClient
import org.graphicalmodellab.auth.facebookapps.FacebookAppsOpenIDConnector
import org.graphicalmodellab.auth.googleapps.GoogleAppsOpenIDConnector
import org.graphicalmodellab.elastic.ElasticSearchClient
import org.graphicalmodellab.encryption.Encryption
import play.Play
import play.api.http.Status

import scala.collection.mutable

/**
 * Created by ito_m on 9/11/16.
 */
class GraphicalModelLabService {
  val config = Play.application().configuration()

  val TrainedModelMap: mutable.Map[String,ModelSimpleCSV] = mutable.Map[String,ModelSimpleCSV]()

  // If we define spark conf here, I get some error
//  val sparkConf = new SparkConf().setAppName("Model").setMaster("local")
//  //  val sparkContext = new SparkContext(sparkConf)
//  val sparkSession = SparkSession.builder().config(sparkConf).getOrCreate()

  def training(companyId:String,request: Option[trainingRequest]): trainingResponse = {

    request match {
      case Some(request)=>
        if(AuthDBClient.isValidToken(companyId,request.userid,request.token)) {
          val model: ModelSimpleCSV = new ModelSimpleCSV(request.graph.edges,request.graph.nodes,request.graph.commonProperties);
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

  def test(companyId:String,request: Option[testRequest]): testResponse = {

    request match {
      case Some(request)=>
        if(AuthDBClient.isValidToken(companyId,request.userid,request.token)) {

          if(request.evaluationMethod == "simple") {
            val model: ModelSimpleCSV = TrainedModelMap.get("test").get
            val accuracy = model.testSimple(request.testsource, request.targetLabel)
            val accuracySummary = GmlDBClient.saveTestHistory(request, accuracy)

            return testResponse(Status.INTERNAL_SERVER_ERROR, 1, "", accuracySummary.toString)
          }else if(request.evaluationMethod == "cross-validation"){
            val K = 10;
            val model: ModelSimpleCSV = new ModelSimpleCSV(request.graph.edges,request.graph.nodes,request.graph.commonProperties);

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

  def save(companyId:String,request: Option[saveRequest]): saveResponse = {

    request match {
      case Some(request)=>
        if(AuthDBClient.isValidToken(companyId,request.userid,request.token)) {
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

  def list(companyId:String,request: Option[listRequest]): listResponse = {

    request match {
      case Some(request)=>
        if(AuthDBClient.isValidToken(companyId,request.userid,request.token)) {
          return GmlDBClient.list(request)
        }else{
          return listResponse(Status.UNAUTHORIZED, 1, List[String]())
        }
      case None =>
        println("No request")
    }
    return listResponse(Status.INTERNAL_SERVER_ERROR, 1, List[String]())
  }

  def get(companyId:String,request: Option[getRequest]): getResponse = {

    request match {
      case Some(request)=>
        if(AuthDBClient.isValidToken(companyId,request.userid,request.token)) {
          return GmlDBClient.get(request)
        }else{
          return getResponse(Status.UNAUTHORIZED, 1, null)
        }
      case None =>
        println("No request")
    }
    return getResponse(Status.INTERNAL_SERVER_ERROR, 1, null)
  }

  def search(companyId:String,request: Option[searchRequest]): searchResponse = {

    request match {
      case Some(request)=>
        if(AuthDBClient.isValidToken(companyId,request.userid,request.token)) {
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

  def history(companyId:String,request: Option[historyRequest]): historyResponse = {

    request match {
      case Some(request)=>
        if(AuthDBClient.isValidToken(companyId,request.userid,request.token)) {
          val testHistory = GmlDBClient.getTestHistory(request)

          return new historyResponse(
            Status.OK,
            Status.OK,
            testHistory.toList
          )
        }else{
          return historyResponse(Status.UNAUTHORIZED, 1, List[String]())
        }
      case None =>
        println("No request")
    }
    return historyResponse(Status.INTERNAL_SERVER_ERROR, 1, List[String]())
  }
  def getHistory(companyId:String,request: Option[getHistoryRequest]): getHistoryResponse = {

    request match {
      case Some(request)=>
        if(AuthDBClient.isValidToken(companyId,request.userid,request.token)) {
          return GmlDBClient.getHistory(request)
        }else{
          return getHistoryResponse(Status.UNAUTHORIZED, 1, null)
        }
      case None =>
        println("No request")
    }
    return getHistoryResponse(Status.INTERNAL_SERVER_ERROR, 1, null)
  }
}
