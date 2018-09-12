package org.graphicalmodellab

import org.graphicalmodellab.api.graph_api._
import play.api.libs.json.{JsSuccess, Reads}

/**
  * Created by itomao on 9/12/18.
  */
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
