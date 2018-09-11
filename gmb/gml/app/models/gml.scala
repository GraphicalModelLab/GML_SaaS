import play.api.libs.json._

import org.graphicalmodellab.api.graph_api._

package object gml {

   val STATUS_NOT_REGISTERED = 900;
   val STATUS_NOT_VALIDATED = 901;
   val STATUS_PASSWORD_INVALID = 902;

   // 1. Registering Engineer
  case class trainingRequest(code: Int, userid:String, companyid: String, graph: graph, datasource: String)
  case class trainingResponse(code: Int, trainingSuccessCode: Int, modelId: String)

  implicit lazy val trainingRequestReads: Reads[trainingRequest] = Reads[trainingRequest] {
    json => JsSuccess(trainingRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
      (json \ "companyid").as[String],
      (json \ "graph").as[graph],
      (json \ "datasource").as[String]
    ))
  }

  implicit lazy val trainingRequestWrites: Writes[trainingResponse] = Writes[trainingResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code),
      "trainingSuccessCode" -> Json.toJson(o.trainingSuccessCode),
      "modelId" -> Json.toJson(o.modelId)
    ).filter(_._2 != JsNull))
  }

  // 2. test request
  case class testRequest(code: Int, userid:String, companyid: String, graph: graph, evaluationMethod: String, testsource: String, targetLabel:String)
  case class testResponse(code: Int, testSuccessCode: Int, modelid: String, accuracy: String)

  implicit lazy val testRequestReads: Reads[testRequest] = Reads[testRequest] {
    json => JsSuccess(testRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
      (json \ "companyid").as[String],
      (json \ "graph").as[graph],
      (json \ "evaluationMethod").as[String],
      (json \ "testsource").as[String],
      (json \ "targetLabel").as[String]
    ))
  }

  implicit lazy val testRequestWrites: Writes[testResponse] = Writes[testResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code),
      "testSuccessCode" -> Json.toJson(o.testSuccessCode),
      "modelid" -> Json.toJson(o.modelid),
      "accuracy" -> Json.toJson(o.accuracy)
    ).filter(_._2 != JsNull))
  }

  // 2. Save models
  case class saveRequest(code: Int, userid:String, companyid: String, graph: graph)
  case class saveResponse(code: Int, saveSuccessCode: Int)

  implicit lazy val saveRequestReads: Reads[saveRequest] = Reads[saveRequest] {
    json => JsSuccess(saveRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
      (json \ "companyid").as[String],
      (json \ "graph").as[graph]
    ))
  }

  implicit lazy val saveRequestWrites: Writes[saveResponse] = Writes[saveResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code),
      "saveSuccessCode" -> Json.toJson(o.saveSuccessCode)
    ).filter(_._2 != JsNull))
  }

  // 3. list models
  case class listRequest(code: Int, userid:String, companyid: String)
  case class listResponse(code: Int, listSuccessCode: Int, models: List[String])

  implicit lazy val listRequestReads: Reads[listRequest] = Reads[listRequest] {
    json => JsSuccess(listRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
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
  case class getRequest(code: Int, userid:String, companyid: String, modelid: String)
  case class getResponse(code: Int, listSuccessCode: Int, model: String)

  implicit lazy val getRequestReads: Reads[getRequest] = Reads[getRequest] {
    json => JsSuccess(getRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
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

  // 3. get models
  case class getModelParameterRequest(code: Int, userid:String, companyid: String, algorithm: String)
  case class getModelParameterResponse(code: Int, listSuccessCode: Int, algorithm: String, parameter: List[String])

  implicit lazy val getModelParameterRequestReads: Reads[getModelParameterRequest] = Reads[getModelParameterRequest] {
    json => JsSuccess(getModelParameterRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
      (json \ "companyid").as[String],
      (json \ "algorithm").as[String]
    ))
  }

  implicit lazy val getModelParameterRequestWrites: Writes[getModelParameterResponse] = Writes[getModelParameterResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code),
      "listSuccessCode" -> Json.toJson(o.listSuccessCode),
      "algorithm" -> Json.toJson(o.algorithm),
      "parameter" -> Json.toJson(o.parameter)
    ).filter(_._2 != JsNull))
  }

  // 3. search models
  case class searchRequest(code: Int, userid:String, companyid: String, query: String)
  case class searchResponse(code: Int, listSuccessCode: Int, result: String)

  implicit lazy val searchRequestReads: Reads[searchRequest] = Reads[searchRequest] {
    json => JsSuccess(searchRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
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

  // 3. search models
  case class getTestHistoryRequest(code: Int, userid:String, companyid: String, model_userid: String, modelid: String)
  case class getTestHistoryResponse(code: Int, listSuccessCode: Int, history: List[String])

  implicit lazy val historyRequestReads: Reads[getTestHistoryRequest] = Reads[getTestHistoryRequest] {
    json => JsSuccess(getTestHistoryRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
      (json \ "companyid").as[String],
      (json \ "model_userid").as[String],
      (json \ "modelid").as[String]
    ))
  }

  implicit lazy val historyRequestWrites: Writes[getTestHistoryResponse] = Writes[getTestHistoryResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code),
      "listSuccessCode" -> Json.toJson(o.listSuccessCode),
      "history" -> Json.toJson(o.history)
    ).filter(_._2 != JsNull))
  }

  // 3. get models
  case class getModelInHistoryRequest(code: Int, userid:String, companyid: String, modelid: String, datetime: Long)
  case class getModelInHistoryResponse(code: Int, listSuccessCode: Int, model: String)

  implicit lazy val getHistoryRequestReads: Reads[getModelInHistoryRequest] = Reads[getModelInHistoryRequest] {
    json => JsSuccess(getModelInHistoryRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
      (json \ "companyid").as[String],
      (json \ "modelid").as[String],
      (json \ "datetime").as[Long]
    ))
  }

  implicit lazy val getHistoryRequestWrites: Writes[getModelInHistoryResponse] = Writes[getModelInHistoryResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code),
      "listSuccessCode" -> Json.toJson(o.listSuccessCode),
      "model" -> Json.toJson(o.model)
    ).filter(_._2 != JsNull))
  }


  // 4. get list of available models
  case class getListOfAvailableModelsRequest(code: Int, userid:String, companyid: String)
  case class getListOfAvailableModelsResponse(code: Int, modelAlgorithmIds: List[String])

  implicit lazy val getListOfAvailableModelsRequestReads: Reads[getListOfAvailableModelsRequest] = Reads[getListOfAvailableModelsRequest] {
    json => JsSuccess(getListOfAvailableModelsRequest(
      (json \ "code").as[Int],
      (json \ "userid").as[String],
      (json \ "companyid").as[String]
    ))
  }

  implicit lazy val getListOfAvailableModelsResponseWrites: Writes[getListOfAvailableModelsResponse] = Writes[getListOfAvailableModelsResponse] {
    o => JsObject(Seq(
      "code" -> Json.toJson(o.code),
      "modelAlgorithmIds" -> Json.toJson(o.modelAlgorithmIds)
    ).filter(_._2 != JsNull))
  }
}
