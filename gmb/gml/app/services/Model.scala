package services

import gml.{edge, node, property, trainingRequest}
import org.apache.spark.SparkConf
import org.apache.spark.mllib.stat.distribution.MultivariateGaussian
import org.apache.spark.sql.SparkSession

import scala.collection.mutable

/**
  * Created by itomao on 6/15/18.
  */
trait Model{
  def getModelName: String
  def setup(_sparkConf: SparkConf, _sparkSession: SparkSession, edges:List[edge], nodes: List[node], commonProperties: List[property]): Unit
  def training(datasource: String): Unit
  def testSimple(testsource : String, targetLabel: String): Double
  def testByCrossValidation(datasource: String,targetLabel: String, numOfSplit: Int): Double
}
