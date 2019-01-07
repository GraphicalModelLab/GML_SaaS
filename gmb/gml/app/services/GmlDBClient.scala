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

import gml.{getModelInHistoryRequest, getModelInHistoryResponse, getRequest, getResponse, getTestHistoryRequest, listRequest, listResponse, saveRequest, testRequest, trainingRequest}
import org.codehaus.jettison.json.JSONObject

trait GmlDBClient {
  def init(keyspace: String,hosts: List[String]): Unit
  def save(request: saveRequest): Date
  def saveTrainingHistory(request: trainingRequest): Date
  def saveTestHistory(request: testRequest, accuracy: Double): JSONObject
  def getTestHistory(request: getTestHistoryRequest): List[String]
  def list(request: listRequest):listResponse
  def get(request: getRequest):getResponse
  def getModelInHistory(request: getModelInHistoryRequest):getModelInHistoryResponse
}
