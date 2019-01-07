package org.graphicalmodellab.plugin

import org.graphicalmodellab.api.DataExtractor

/**
  * Created by itomao on 12/18/18.
  */
class CassandraETLExtractor extends DataExtractor{
  override def init(): Unit = {}

  override def getExtractorName: String = "ETL to Cassandra"

  override def getExtractorParameterInfo: List[String] = List[String]("param1")

  override def process(companyid: String, userid: String,googleCredential: Map[String, Any], facebookCredential: Map[String, Any]): Unit = {}

}
