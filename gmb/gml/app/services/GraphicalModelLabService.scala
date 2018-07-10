package services

import gml._
import org.codehaus.jettison.json.JSONObject
import org.graphicalmodellab.auth.AuthDBClient
import org.graphicalmodellab.auth.facebookapps.FacebookAppsOpenIDConnector
import org.graphicalmodellab.auth.googleapps.GoogleAppsOpenIDConnector
import org.graphicalmodellab.elastic.ElasticSearchClient
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
        if(AuthDBClient.isValidToken(companyId,request.userid,request.token)) {
          val model: ModelSimpleCSV = new ModelSimpleCSV(request);
          model.training()
        }

      case None =>
        println("No request")
    }
    return trainingResponse(Status.INTERNAL_SERVER_ERROR, 1,"")
  }

  def save(companyId:String,request: Option[saveRequest]): saveResponse = {

    request match {
      case Some(request)=>
        if(AuthDBClient.isValidToken(companyId,request.userid,request.token)) {
          GmlDBClient.save(request)
          GmlElasticSearchClient.addDocument(request)
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
        }
      case None =>
        println("No request")
    }
    return searchResponse(Status.INTERNAL_SERVER_ERROR, 1, "[]")
  }
}
