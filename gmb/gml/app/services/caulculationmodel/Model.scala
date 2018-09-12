package services.caulculationmodel

import org.graphicalmodellab.api.graph_api._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  * Created by itomao on 6/15/18.
  */
trait Model{
  def getModelName: String
  def getModelParameterInfo: List[String]

  def init(): Unit
  def training(graph:graph, datasource: String): Unit
  def testSimple(graph: graph, testsource : String, targetLabel: String): Double
  def testByCrossValidation(graph:graph, datasource: String, targetLabel: String, numOfSplit: Int): Double
}
