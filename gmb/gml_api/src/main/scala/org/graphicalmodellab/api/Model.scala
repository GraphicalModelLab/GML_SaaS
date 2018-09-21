package org.graphicalmodellab.api

import org.graphicalmodellab.api.graph_api._

/**
  * Created by itomao on 6/15/18.
  */
object Model{
  val EVALUATION_METHOD_SIMPLE = "simple"
  val EVALUATION_METHOD_CROSS_VALIDATION = "cross_validation"
}

trait Model{
  def getModelName: String
  def getModelParameterInfo: List[String]
  def getSupportedEvaluationMethod: List[String]

  def init(): Unit
  def training(graph:graph, datasource: String): Unit
  def testSimple(graph: graph, testsource : String, targetLabel: String): Double
  def testByCrossValidation(graph:graph, datasource: String, targetLabel: String, numOfSplit: Int): Double
}
