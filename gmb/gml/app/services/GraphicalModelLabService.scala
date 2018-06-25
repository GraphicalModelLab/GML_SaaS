package services

import gml._
import org.codehaus.jettison.json.JSONObject
import org.graphicalmodellab.auth.AuthDBClient
import org.graphicalmodellab.auth.facebookapps.FacebookAppsOpenIDConnector
import org.graphicalmodellab.auth.googleapps.GoogleAppsOpenIDConnector
import org.graphicalmodellab.encryption.Encryption
import play.Play
import play.api.http.Status

/**
 * Created by ito_m on 9/11/16.
 */
class GraphicalModelLabService {
  val config = Play.application().configuration()

  def training(companyId:String,request: Option[trainingRequest]): trainingResponse = {

    request match {
      case Some(request)=>
        val model: ModelSimpleCSV = new ModelSimpleCSV(request);

        model.training()
      case None =>
        println("No request")
    }
    return trainingResponse(Status.INTERNAL_SERVER_ERROR, 1,"")
  }
}
