package services

import java.util.Date

import com.datastax.driver.core.querybuilder.QueryBuilder
import gml._
import org.codehaus.jettison.json.JSONObject
import org.graphicalmodellab.cassandra.CassandraClient
import play.api.http.Status
import play.api.libs.json.Json

import scala.collection.mutable
import org.graphicalmodellab.api.graph_api._
/**
 * Created by ito_m on 9/26/16.
 */
object GmlDBClient {
  var client = new CassandraClient()

  def init(hosts: List[String]): Unit = {
    client.connect(List[String](
      "localhost"
    ))
  }

  def save(request: saveRequest): Date ={
    val timestamp = new Date();

    val query = QueryBuilder.update("master","model")
      .`with`(QueryBuilder.set("model","{\"modelversion\":\""+timestamp.getTime+"\","+"\"timestamp\":\""+timestamp.toString+"\","+"\"datetime\":"+timestamp.getTime+","+Json.toJson[graph](request.graph).toString().substring(1)))
      .and(QueryBuilder.set("datetime",timestamp.getTime))
      .where(QueryBuilder.eq("companyid",request.companyid))
      .and(QueryBuilder.eq("userid",request.userid))
      .and(QueryBuilder.eq("modelid",request.graph.modelid))

    client.executeStatement(query)

    return timestamp
  }

  def saveTrainingHistory(request: trainingRequest): Date ={
    val timestamp = new Date();

    val query = QueryBuilder.update("master","model_training_history")
      .`with`(QueryBuilder.set("model","{\"modelversion\":\""+timestamp.getTime+"\","+"\"timestamp\":\""+timestamp.toString+"\","+"\"datetime\":"+timestamp.getTime+","+Json.toJson[graph](request.graph).toString().substring(1)))
      .and(QueryBuilder.set("training_data",request.datasource))
      .where(QueryBuilder.eq("companyid",request.companyid))
      .and(QueryBuilder.eq("userid",request.userid))
      .and(QueryBuilder.eq("modelid",request.graph.modelid))
      .and(QueryBuilder.eq("datetime",timestamp.getTime))


    client.executeStatement(query)

    return timestamp
  }

  def saveTestHistory(request: testRequest, accuracy: Double): JSONObject ={
    val timestamp = new Date();

    val accuracyJSON = new JSONObject();
    accuracyJSON.put("accuracy",accuracy);
    accuracyJSON.put("evaluationMethod",request.evaluationMethod);

    val query = QueryBuilder.update("master","model_test_history")
      .`with`(QueryBuilder.set("model","{\"modelversion\":\""+timestamp.getTime+"\","+"\"timestamp\":\""+timestamp.toString+"\","+"\"datetime\":"+timestamp.getTime+","+Json.toJson[graph](request.graph).toString().substring(1)))
      .and(QueryBuilder.set("test_data",request.testsource))
      .and(QueryBuilder.set("accuracy",accuracyJSON.toString))
      .where(QueryBuilder.eq("companyid",request.companyid))
      .and(QueryBuilder.eq("userid",request.userid))
      .and(QueryBuilder.eq("modelid",request.graph.modelid))
      .and(QueryBuilder.eq("datetime",timestamp.getTime))


    client.executeStatement(query)

    return accuracyJSON
  }
  def getTestHistory(request: getTestHistoryRequest): List[String] ={
    val query = QueryBuilder.select()
      .all()
      .from("master","model_test_history")
      .where(QueryBuilder.eq("companyid",request.companyid))
      .and(QueryBuilder.eq("userid",request.model_userid))
      .and(QueryBuilder.eq("modelid",request.modelid))

    client.executeStatement(query)

    val history = mutable.ListBuffer[String]()

    val iterator = client.executeStatement(query).iterator()
    while (iterator.hasNext) {
      val next = iterator.next()

      history += "{\"model\":"+next.getString("model")+",\"test_data\":\""+next.getString("test_data")+"\",\"info\":"+next.getString("accuracy")+",\"time\":"+next.getTimestamp("datetime").getTime+"}"
    }

    return history.toList
  }


  def list(request: listRequest):listResponse ={

    val query = QueryBuilder.select()
            .all()
              .from("master","model")
              .where(QueryBuilder.eq("companyid",request.companyid))

    client.executeStatement(query)

    val models = mutable.ListBuffer[String]()

    val iterator = client.executeStatement(query).iterator()
    while (iterator.hasNext) {
      models += iterator.next().getString("model")
    }

    return new listResponse(
                Status.OK,
                Status.OK,
                models.toList
      )
  }

  def get(request: getRequest):getResponse ={

    val query = QueryBuilder.select()
      .all()
      .from("master","model")
      .where(QueryBuilder.eq("companyid",request.companyid))
      .and(QueryBuilder.eq("userid",request.userid))
      .and(QueryBuilder.eq("modelid",request.modelid))

    client.executeStatement(query)

    var model:String = null

    val iterator = client.executeStatement(query).iterator()
    if (iterator.hasNext) {
      model = iterator.next().getString("model")
    }

    return new getResponse(
      Status.OK,
      Status.OK,
      model
    )
  }

  def getModelInHistory(request: getModelInHistoryRequest):getModelInHistoryResponse ={

    val query = QueryBuilder.select()
      .all()
      .from("master","model_test_history")
      .where(QueryBuilder.eq("companyid",request.companyid))
      .and(QueryBuilder.eq("userid",request.userid))
      .and(QueryBuilder.eq("modelid",request.modelid))
      .and(QueryBuilder.eq("datetime",request.datetime))

    client.executeStatement(query)

    var model:String = null

    val iterator = client.executeStatement(query).iterator()
    if (iterator.hasNext) {
      model = iterator.next().getString("model")
    }

    return new getModelInHistoryResponse(
      Status.OK,
      Status.OK,
      model
    )
  }
}
