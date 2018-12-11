package services.test

import org.graphicalmodellab.api.DataExtractor

/**
  * Created by itomao on 12/11/18.
  */
class Extractor1 extends DataExtractor{
  override def init(): Unit = {}

  override def getExtractorName: String = "ETL to Hive"

  override def getExtractorParameterInfo: List[String] = List[String]("param1")

  override def process(companyid: String, userid: String): Unit = {}
}
