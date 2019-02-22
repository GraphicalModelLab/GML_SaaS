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

package org.graphicalmodellab

import org.graphicalmodellab.api.graph_api._
import play.api.libs.json.{JsSuccess, Reads}

package object model {
  case class request(datasource: String, targetLabel: String, numOfSplit: Int,graph: graph)
  implicit lazy val requestReads: Reads[request] = Reads[request] {
    json => JsSuccess(request(
      (json \ "datasource").as[String],
      (json \ "targetLabel").as[String],
      (json \ "numOfSplit").as[Int],
      (json \ "graph").as[graph]
    ))
  }
}
