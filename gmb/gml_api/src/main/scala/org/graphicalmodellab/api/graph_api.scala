package org.graphicalmodellab.api

import play.api.libs.json._

/**
  * Created by itomao on 9/11/18.
  */
package object graph_api {

  // Graph Properties
  case class property(name: String, value: String)
  case class node(label: String, x: Double, y: Double, disable: Boolean, properties: List[property])
  case class edge(label1: String, label2: String,x1: Double, y1: Double,x2: Double, y2: Double)
  case class graph(modelid: String,modelname: String, modeltag: String, modeldescription: String, userid: String, algorithm: String, nodes: List[node], edges: List[edge], commonProperties: List[property])

  implicit lazy val graphReads: Reads[graph] = Reads[graph] {
    json => JsSuccess(graph(
      (json \ "modelid").as[String],
      (json \ "modelname").as[String],
      (json \ "modeltag").as[String],
      (json \ "modeldescription").as[String],
      (json \ "userid").as[String],
      (json \ "algorithm").as[String],
      (json \ "nodes").as[List[node]],
      (json \ "edges").as[List[edge]],
      (json \ "commonProperties").as[List[property]]
    ))
  }

  implicit lazy val graphWrites: Writes[graph] = Writes[graph] {
    o => JsObject(Seq(
      "modelid" -> Json.toJson(o.modelid),
      "modelname" -> Json.toJson(o.modelname),
      "modeltag" -> Json.toJson(o.modeltag),
      "modeldescription" -> Json.toJson(o.modeldescription),
      "userid" -> Json.toJson(o.userid),
      "algorithm" -> Json.toJson(o.algorithm),
      "nodes" -> Json.toJson(o.nodes),
      "edges" -> Json.toJson(o.edges),
      "commonProperties" -> Json.toJson(o.commonProperties)
    ).filter(_._2 != JsNull))
  }


  implicit lazy val edgeReads: Reads[edge] = Reads[edge] {
    json => JsSuccess(edge(
      (json \ "label1").as[String],
      (json \ "label2").as[String],
      (json \ "x1").as[Double],
      (json \ "y1").as[Double],
      (json \ "x2").as[Double],
      (json \ "y2").as[Double]
    ))
  }

  implicit lazy val edgeWrites: Writes[edge] = Writes[edge] {
    o => JsObject(Seq(
      "label1" -> Json.toJson(o.label1),
      "label2" -> Json.toJson(o.label2),
      "x1" -> Json.toJson(o.x1),
      "y1" -> Json.toJson(o.y1),
      "x2" -> Json.toJson(o.x2),
      "y2" -> Json.toJson(o.y2)
    ).filter(_._2 != JsNull))
  }

  implicit lazy val nodeReads: Reads[node] = Reads[node] {
    json => JsSuccess(node(
      (json \ "label").as[String],
      (json \ "x").as[Double],
      (json \ "y").as[Double],
      (json \ "disable").as[Boolean],
      (json \ "properties").as[List[property]]
    ))
  }

  implicit lazy val nodeWrites: Writes[node] = Writes[node] {
    o => JsObject(Seq(
      "label" -> Json.toJson(o.label),
      "x" -> Json.toJson(o.x),
      "y" -> Json.toJson(o.y),
      "disable" -> Json.toJson(o.disable),
      "properties" -> Json.toJson(o.properties)
    ).filter(_._2 != JsNull))
  }

  implicit lazy val propertyReads: Reads[property] = Reads[property] {
    json => JsSuccess(property(
      (json \ "name").as[String],
      (json \ "value").as[String]
    ))
  }

  implicit lazy val propertyWrites: Writes[property] = Writes[property] {
    o => JsObject(Seq(
      "name" -> Json.toJson(o.name),
      "value" -> Json.toJson(o.value)
    ).filter(_._2 != JsNull))
  }
}
