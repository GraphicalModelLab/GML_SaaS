import play.api.libs.json._

package object gml {

   val STATUS_NOT_REGISTERED = 900;
   val STATUS_NOT_VALIDATED = 901;
   val STATUS_PASSWORD_INVALID = 902;

   // 1. Registering Engineer
  // Changing Password Service
  case class property(name: String, value: String)
  case class node(label: String, x: Double, y: Double, disable: Boolean, properties: List[property])
  case class edge(label1: String, label2: String,x1: Double, y1: Double,x2: Double, y2: Double)
  case class trainingRequest(code: Int, userid:String, token: String, companyid: String, datasource: String, algorithm: String, nodes: List[node], edges: List[edge], commonProperties: List[property])
  case class trainingResponse(code: Int, trainingSuccessCode: Int, gmlId: String)

  implicit lazy val trainingRequestReads: Reads[trainingRequest] = Reads[trainingRequest] {
    json => JsSuccess(trainingRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
      (json \ "token").as[String],
      (json \ "companyid").as[String],
      (json \ "datasource").as[String],
      (json \ "algorithm").as[String],
      (json \ "nodes").as[List[node]],
      (json \ "edges").as[List[edge]],
      (json \ "commonProperties").as[List[property]]
    ))
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

  implicit lazy val nodeReads: Reads[node] = Reads[node] {
    json => JsSuccess(node(
      (json \ "label").as[String],
      (json \ "x").as[Double],
      (json \ "y").as[Double],
      (json \ "disable").as[Boolean],
      (json \ "properties").as[List[property]]
    ))
  }

  implicit lazy val propertyReads: Reads[property] = Reads[property] {
    json => JsSuccess(property(
      (json \ "name").as[String],
      (json \ "value").as[String]
    ))
  }

  implicit lazy val trainingRequestWrites: Writes[trainingResponse] = Writes[trainingResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code),
      "trainingSuccessCode" -> Json.toJson(o.trainingSuccessCode),
      "gmlId" -> Json.toJson(o.gmlId)
    ).filter(_._2 != JsNull))
  }

  // 2. test request
  case class testRequest(code: Int, userid:String, token: String, companyid: String, testsource: String, algorithm: String, gmlId: String, targetLabel:String)
  case class testResponse(code: Int, testSuccessCode: Int, gmlId: String)

  implicit lazy val testRequestReads: Reads[testRequest] = Reads[testRequest] {
    json => JsSuccess(testRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
      (json \ "token").as[String],
      (json \ "companyid").as[String],
      (json \ "testsource").as[String],
      (json \ "algorithm").as[String],
      (json \ "gmlId").as[String],
      (json \ "targetLabel").as[String]
    ))
  }

  implicit lazy val testRequestWrites: Writes[testResponse] = Writes[testResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code),
      "testSuccessCode" -> Json.toJson(o.testSuccessCode),
      "gmlId" -> Json.toJson(o.gmlId)
    ).filter(_._2 != JsNull))
  }

  // 2. Save models
  case class saveRequest(code: Int, userid:String, token: String, companyid: String, modelid: String, graph: String)
  case class saveResponse(code: Int, saveSuccessCode: Int)

  implicit lazy val saveRequestReads: Reads[saveRequest] = Reads[saveRequest] {
    json => JsSuccess(saveRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
      (json \ "token").as[String],
      (json \ "companyid").as[String],
      (json \ "modelid").as[String],
      (json \ "graph").as[String]
    ))
  }

  implicit lazy val saveRequestWrites: Writes[saveResponse] = Writes[saveResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code),
      "saveSuccessCode" -> Json.toJson(o.saveSuccessCode)
    ).filter(_._2 != JsNull))
  }

  // 3. list models
  case class listRequest(code: Int, userid:String, token: String, companyid: String)
  case class listResponse(code: Int, listSuccessCode: Int, models: List[String])

  implicit lazy val listRequestReads: Reads[listRequest] = Reads[listRequest] {
    json => JsSuccess(listRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
      (json \ "token").as[String],
      (json \ "companyid").as[String]
    ))
  }

  implicit lazy val listRequestWrites: Writes[listResponse] = Writes[listResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code),
      "listSuccessCode" -> Json.toJson(o.listSuccessCode),
      "models" -> Json.toJson(o.models)
    ).filter(_._2 != JsNull))
  }

  // 3. get models
  case class getRequest(code: Int, userid:String, token: String, companyid: String, modelid: String, datetime: Long)
  case class getResponse(code: Int, listSuccessCode: Int, model: String)

  implicit lazy val getRequestReads: Reads[getRequest] = Reads[getRequest] {
    json => JsSuccess(getRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
      (json \ "token").as[String],
      (json \ "companyid").as[String],
      (json \ "modelid").as[String],
      (json \ "datetime").as[Long]
    ))
  }

  implicit lazy val getRequestWrites: Writes[getResponse] = Writes[getResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code),
      "listSuccessCode" -> Json.toJson(o.listSuccessCode),
      "model" -> Json.toJson(o.model)
    ).filter(_._2 != JsNull))
  }

  // 3. search models
  case class searchRequest(code: Int, userid:String, token: String, companyid: String, query: String)
  case class searchResponse(code: Int, listSuccessCode: Int, result: String)

  implicit lazy val searchRequestReads: Reads[searchRequest] = Reads[searchRequest] {
    json => JsSuccess(searchRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
      (json \ "token").as[String],
      (json \ "companyid").as[String],
      (json \ "query").as[String]
    ))
  }

  implicit lazy val searchRequestWrites: Writes[searchResponse] = Writes[searchResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code),
      "listSuccessCode" -> Json.toJson(o.listSuccessCode),
      "result" -> Json.toJson(o.result)
    ).filter(_._2 != JsNull))
  }
}
