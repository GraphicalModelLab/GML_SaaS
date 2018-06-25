package org.graphicalmodellab.httprequest

import scalaj.http.{Http, HttpOptions, HttpResponse}

/**
  * Created by itomao on 1/14/17.
  */
object HttpRequest {
  def postJson(url: String, jsonString: String) : String= {
      Http(url).postData(jsonString)
        .header("Content-Type", "application/json")
        .header("Charset", "UTF-8")
        .option(HttpOptions.readTimeout(10000)).asString.body
  }

  def postParams(url: String, params: Seq[(String,String)]) : String= {
    Http(url).postForm(params).asString.body
  }

  def getJson(url: String) : String= {
    Http(url).option(HttpOptions.readTimeout(10000)).asString.body
  }
}
