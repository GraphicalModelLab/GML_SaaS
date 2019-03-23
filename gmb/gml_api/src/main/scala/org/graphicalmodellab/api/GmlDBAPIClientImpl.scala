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

package org.graphicalmodellab.api

import java.util.Date

import com.datastax.driver.core.querybuilder.QueryBuilder
import com.google.inject.Inject
import org.codehaus.jettison.json.JSONObject
import org.graphicalmodellab.api.graph_api._
import org.graphicalmodellab.cassandra.CassandraClient
import play.api.http.Status
import play.api.libs.json.Json

import scala.collection.mutable

class GmlDBAPIClientImpl @Inject () (client: CassandraClient) extends GmlDBAPIClient{

  var _keyspace = "master"
  def init(keyspace: String,hosts: List[String]): Unit = {
    client.connect(List[String](
      "localhost"
    ))

    _keyspace = keyspace
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
}
