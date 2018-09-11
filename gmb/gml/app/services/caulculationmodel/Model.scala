package services.caulculationmodel

import org.graphicalmodellab.api.graph_api._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import services.SparkProcessManager

/**
  * Created by itomao on 6/15/18.
  */
trait Model{
  def getModelName: String
  def getModelParameterInfo: List[String]
  def setup(_sparkConf: SparkConf, _sparkSession: SparkSession, edges:List[edge], nodes: List[node], commonProperties: List[property]): Unit
  def setup(userid: String,processManager: SparkProcessManager, graph: graph): Unit
  def training(datasource: String): Unit
  def testSimple(testsource : String, targetLabel: String): Double
  def testByCrossValidation(datasource: String,targetLabel: String, numOfSplit: Int): Double
}
