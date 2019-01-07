/**
  * Copyright (C) 2018 Mao Ito
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package org.graphicalmodellab.api

import org.graphicalmodellab.api.graph_api._

object Model{
  val EVALUATION_METHOD_SIMPLE = "simple"
  val EVALUATION_METHOD_CROSS_VALIDATION = "cross_validation"

  val SHAPE_CIRCLE = "circle"
  val SHAPE_BOX = "box"
}

trait Model{
  def getModelName: String
  def getModelParameterInfo: List[String]
  def getSupportedEvaluationMethod: List[String]
  def getSupportedShape: List[String]

  def init(): Unit
  def training(graph:graph, datasource: String): Unit
  def testSimple(graph: graph, testsource : String, targetLabel: String): Double
  def testByCrossValidation(graph:graph, datasource: String, targetLabel: String, numOfSplit: Int): Double
  def exploreStructure(graph:graph, targetLabel: String, datasource: String): (graph, Double)
}
