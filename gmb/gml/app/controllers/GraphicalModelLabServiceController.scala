package controllers

import java.io.{PrintWriter, StringWriter}

import play.api.mvc.{Action, Controller}
import gml._
import org.graphicalmodellab.auth.AuthDBClient
import org.graphicalmodellab.elastic.ElasticSearchClient
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.GraphicalModelLabService
import play.api.Logger
import org.graphicalmodellab.api.graph_api._

/**
 * Created by ito_m on 8/25/16.
 */
object GraphicalModelLabServiceController extends Controller {

  val gmlService = new GraphicalModelLabService();

  //http://localhost:9098/helloworld
  def helloworld() = {
    Action(request =>
      Ok(Json.toJson[warmupResponse](gmlService.warmup()))
    )
  }

  def training(companyId:String) = {
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

  def test(companyId:String) = {
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

  def save(companyId:String) = {
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

  def list(companyId:String) = {
    Action(request =>
      try
        Ok(Json.toJson[listResponse](gmlService.list(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          Some(listRequest(0,request.getQueryString("userid").get,companyId))
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

  def get(companyId:String) = {
    Action(request =>
      try
        Ok(Json.toJson[getResponse](gmlService.get(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          Some(getRequest(0,request.getQueryString("userid").get,companyId,request.getQueryString("modelid").get))
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

  def getModelParameter(companyId:String) = {
    Action(request =>
      try
        Ok(Json.toJson[getModelParameterResponse](gmlService.getModelParameter(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          Some(getModelParameterRequest(0,request.getQueryString("userid").get,companyId,request.getQueryString("algorithm").get))
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

  def search(companyId:String) = {
    Action(request =>
      try
        Ok(Json.toJson[searchResponse](gmlService.search(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          Some(searchRequest(0,request.getQueryString("userid").get,companyId,request.getQueryString("query").get))
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

  def getTestHistory(companyId:String) = {
    Action(request =>
      try
        Ok(Json.toJson[getTestHistoryResponse](gmlService.getTestHistory(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          Some(getTestHistoryRequest(0,request.getQueryString("userid").get,companyId,request.getQueryString("model_userid").get,request.getQueryString("modelid").get))
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

  def getModelInTestHistory(companyId:String) = {
    Action(request =>
      try
        Ok(Json.toJson[getModelInHistoryResponse](gmlService.getModelInTestHistory(
          request.headers.get("Authorization").get.substring(7),
          companyId,
          Some(getModelInHistoryRequest(0,request.getQueryString("userid").get,companyId,request.getQueryString("modelid").get,request.getQueryString("datetime").get.toLong))
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

  def getListOfAvailableModels(companyId:String) = {
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


  def getExploredGraph(companyId:String) = {
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
}
