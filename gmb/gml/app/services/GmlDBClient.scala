package services

import com.datastax.driver.core.querybuilder.QueryBuilder
import gml.{listRequest, listResponse, saveRequest}
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

  def save(request: saveRequest):Unit ={

    // 1. Validate the token at first

    // 2. Update the engineer profile

    val query = QueryBuilder.update("master","model")
      .`with`(QueryBuilder.set("model",request.graph))
      .where(QueryBuilder.eq("companyid",request.companyid))
      .and(QueryBuilder.eq("userid",request.userid))
      .and(QueryBuilder.eq("modelid",request.companyid))

    client.executeStatement(query)

  }

  def list(request: listRequest):listResponse ={

    // 1. Validate the token at first

    // 2. Update the engineer profile

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
}
