package services

import gml.{edge, node, property}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  * Created by itomao on 8/15/18.
  */
class ModelSimple2 extends Model{
  def getModelName: String = {
    return "gg"
  }

  def getModelParameterInfo: List[String] = List[String](
    "distributionTesting"
  )

  def setup(_sparkConf: SparkConf, _sparkSession: SparkSession, edges:List[edge], nodes: List[node], commonProperties: List[property]): Unit = {

  }

  def training(datasource: String): Unit ={

  }

  def testSimple(testsource : String, targetLabel: String): Double={
    return 0
  }

  def testByCrossValidation(datasource: String,targetLabel: String, numOfSplit: Int): Double={
    return 0.1
  }

}
