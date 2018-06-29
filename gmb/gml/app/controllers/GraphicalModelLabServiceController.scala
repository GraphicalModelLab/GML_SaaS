package controllers

import java.io.{PrintWriter, StringWriter}

import play.api.mvc.{Action, Controller}
import gml._
import org.graphicalmodellab.auth.AuthDBClient
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.{GmlDBClient, GraphicalModelLabService}


/**
 * Created by ito_m on 8/25/16.
 */
object GraphicalModelLabServiceController extends Controller {

  val gmlService = new GraphicalModelLabService();

  GmlDBClient.init(List[String]("localhost"));
  AuthDBClient.init(List[String]("localhost"))

  def helloworld() = {
    Action(request =>
      Ok("hello auth service")
    )
  }

  def training(companyId:String) = {
    Action(request =>
      try
        Ok(Json.toJson[trainingResponse](gmlService.training(companyId,Json.fromJson[trainingRequest](request.body.asJson.get).asOpt)))
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
        Ok(Json.toJson[saveResponse](gmlService.save(companyId,Json.fromJson[saveRequest](request.body.asJson.get).asOpt)))
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
        Ok(Json.toJson[listResponse](gmlService.list(companyId,Json.fromJson[listRequest](request.body.asJson.get).asOpt)))
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
