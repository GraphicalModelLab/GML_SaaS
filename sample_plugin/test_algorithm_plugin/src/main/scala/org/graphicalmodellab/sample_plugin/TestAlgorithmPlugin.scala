package org.graphicalmodellab.sample_plugin

import org.graphicalmodellab.api.Model
import org.graphicalmodellab.api.graph_api.graph
/**
  * Created by itomao on 12/5/18.
  */
class TestAlgorithmPlugin extends Model{
  // This name appears in UI as one of algorithms for calculating graph
  def getModelName: String = "Test"

  // This indicates what parameters can be set for this algorithm
  // This parameter will be auto-completed suggestion by UI
  def getModelParameterInfo: List[String] = List[String](
    "distribution"
  )

  // This parameter tells UI which evaluation method can be applied to this algorithm
  def getSupportedEvaluationMethod: List[String] = List[String] (
    //    Model.EVALUATION_METHOD_SIMPLE,
    Model.EVALUATION_METHOD_CROSS_VALIDATION
  )

  // This method is called when this plugin is loaded
  override def init(): Unit = {
    println("FInished loading TestAlgorithmPlugin...")
  }

  override def training(graph: graph, datasource: String): Unit = {}

  override def testSimple(graph: graph, testsource: String, targetLabel: String): Double = -1

  override def testByCrossValidation(graph: graph, datasource: String, targetLabel: String, numOfSplit: Int): Double = -1

  override def exploreStructure(graph: graph, targetLabel: String, datasource: String): (graph, Double) = (graph,-1)
}
