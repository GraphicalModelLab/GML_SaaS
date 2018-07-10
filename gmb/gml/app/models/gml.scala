import play.api.libs.json._

package object gml {

   val STATUS_NOT_REGISTERED = 900;
   val STATUS_NOT_VALIDATED = 901;
   val STATUS_PASSWORD_INVALID = 902;

   // 1. Registering Engineer
  // Changing Password Service
  case class edge(label1: String, label2: String)
  case class trainingRequest(code: Int, userid:String, token: String, companyid: String, datasource: String, algorithm: String, nodes: List[String], edges: List[edge])
  case class trainingResponse(code: Int, trainingSuccessCode: Int, gmlId: String)

  implicit lazy val trainingRequestReads: Reads[trainingRequest] = Reads[trainingRequest] {
    json => JsSuccess(trainingRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
      (json \ "token").as[String],
      (json \ "companyid").as[String],
      (json \ "datasource").as[String],
      (json \ "algorithm").as[String],
      (json \ "nodes").as[List[String]],
      (json \ "edges").as[List[edge]]
    ))
  }

  implicit lazy val edgeReads: Reads[edge] = Reads[edge] {
    json => JsSuccess(edge(
      (json \ "label1").as[String],
      (json \ "label2").as[String]
    ))
  }

  implicit lazy val trainingRequestWrites: Writes[trainingResponse] = Writes[trainingResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code),
      "trainingSuccessCode" -> Json.toJson(o.trainingSuccessCode),
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
  case class getRequest(code: Int, userid:String, token: String, companyid: String, modelid: String)
  case class getResponse(code: Int, listSuccessCode: Int, model: String)

  implicit lazy val getRequestReads: Reads[getRequest] = Reads[getRequest] {
    json => JsSuccess(getRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
      (json \ "token").as[String],
      (json \ "companyid").as[String],
      (json \ "modelid").as[String]
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
