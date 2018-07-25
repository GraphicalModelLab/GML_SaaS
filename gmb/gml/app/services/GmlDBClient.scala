package services

import java.util.Date

import com.datastax.driver.core.querybuilder.QueryBuilder
import gml.{getRequest, getResponse, listRequest, listResponse, saveRequest}
import org.codehaus.jettison.json.JSONObject
import org.graphicalmodellab.cassandra.CassandraClient
import play.api.http.Status

import scala.collection.mutable

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
      .`with`(QueryBuilder.set("model","{\"modelversion\":\""+timestamp.getTime+"\","+"\"timestamp\":\""+timestamp.toString+"\","+"\"datetime\":"+timestamp.getTime+","+request.graph.substring(1)))
      .where(QueryBuilder.eq("companyid",request.companyid))
      .and(QueryBuilder.eq("userid",request.userid))
      .and(QueryBuilder.eq("modelid",request.modelid))
      .and(QueryBuilder.eq("datetime",timestamp.getTime))

    client.executeStatement(query)

    return timestamp
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
      .and(QueryBuilder.eq("datetime",request.datetime))

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
}
