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

}
