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

import org.codehaus.jettison.json.JSONObject
import org.graphicalmodellab.api.graph_api.testRequest
trait GmlDBAPIClient {
  def init(keyspace: String,hosts: List[String]): Unit
  def saveTestHistory(request: testRequest, accuracy: Double): JSONObject
}
