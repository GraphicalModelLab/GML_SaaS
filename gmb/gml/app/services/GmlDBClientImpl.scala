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

import java.util.Date

import com.datastax.driver.core.querybuilder.QueryBuilder
import com.google.inject.Inject
import gml._
import org.codehaus.jettison.json.JSONObject
import org.graphicalmodellab.api.graph_api._
import org.graphicalmodellab.cassandra.CassandraClient
import play.api.http.Status
import play.api.libs.json.Json

import scala.collection.mutable

class GmlDBClientImpl @Inject () (client: CassandraClient) extends GmlDBClient{

  var _keyspace = "master"
  def init(keyspace: String,hosts: List[String]): Unit = {
    client.connect(List[String](
      "localhost"
    ))

    _keyspace = keyspace
  }

  def save(request: saveRequest): Date ={
    val timestamp = new Date();

    val query = QueryBuilder.update(_keyspace,"model")
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

    val query = QueryBuilder.update(_keyspace,"model_training_history")
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

    val query = QueryBuilder.update(_keyspace,"model_test_history")
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
      .from(_keyspace,"model_test_history")
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
              .from(_keyspace,"model")
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
      .from(_keyspace,"model")
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
      .from(_keyspace,"model_test_history")
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
