package org.graphicalmodellab.model

import com.typesafe.config.Config
import org.apache.log4j.LogManager
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.linalg.{Matrix, Vectors}
import org.apache.spark.mllib.stat.distribution.MultivariateGaussian
import org.apache.spark.mllib.stat.{MultivariateStatisticalSummary, Statistics}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.graphicalmodellab.api.graph_api._
import org.scalactic._
import play.api.libs.json._
import spark.jobserver._
import spark.jobserver.api.{JobEnvironment, SingleProblem, ValidationProblem}

import scala.collection.mutable
import scala.util.Try
import scalaj.http.Base64

/**
  * This is SparkJobSever Job
  * https://medium.com/@jwhitebear/beginners-guide-to-getting-started-with-spark-jobserver-2e3d4362ee6e
  *
  * How to run Spark Job Server:
  *
  * cd /Users/itomao/OSS/spark-jobserver-master/
  * sbt
  * > job-server-extras/reStart
  *
  * Sample :
  *  curl --data-binary @job-server-tests/target/scala-2.11/job-server-tests_2.11-0.8.1-SNAPSHOT.jar localhost:8090/jars/test
  *  curl -d "input.string = a b c a b see" "localhost:8090/jobs?appName=test&classPath=spark.jobserver.WordCountExample"
  *
  * Generate jar file:
  *  sbt assembly # under this project
  *
  * Generate Context:
  *  curl -d "" 'http://localhost:8090/contexts/multi?num-cpu-cores=1&memory-per-node=512m&spark.executor.instances=1&context-factory=spark.jobserver.context.SessionContextFactory'
  *
  * Register:
  *  curl --data-binary @/Users/itomao/git/GML_SaaS/model/sample_model/multivariateguassian/target/scala-2.11/multivariateguassian_2.11-0.1-SNAPSHOT.jar localhost:8090/jars/why
  *
  * Execute:
  *  curl -d "input.string = a b c a b see" "localhost:8090/jobs?appName=why&context=multi&classPath=org.graphicalmodellab.model.TestByCrossValidation"
  *
  * Test History:
  *  1. 10 fold cross validation :
  *
  * Created by itomao on 9/11/18.
  */
object Kernel{
  def GuassianKernel(data: Double): Double = Math.exp(-data*data/2)/Math.sqrt(2*Math.PI);
}

object TestByCrossValidation extends SparkSessionJob{
  // Setup Logger for Spark, /Users/itomao/OSS/spark-2.2.0-bin-hadoop2.7/conf/log4j.properties
  val log = LogManager.getRootLogger

  var allEdges: List[edge] = null;
  var allNodes: List[node] = null;
  var allNodesMap: Map[String, node] = null;

  var invertedIndex: Map[String, Int] = null;

  var commonDistribution: String = null;
  var distributionMap: mutable.Map[String, String] = null;

  var commonBandwidth: Double = 10.0;
  var bandwidthMap: mutable.Map[String, Double] = null;

  var categoricalPossibleValues : collection.mutable.Map[String, Set[String]] = null;

  type JobData = String
  type JobOutput = collection.Map[String, String]

  override def runJob(sparkSession: SparkSession, runtime: JobEnvironment, data: JobData): JobOutput = {
    val json = new String(Base64.decode(data))

    val request: request = Json.fromJson[request](Json.parse(json)).get

    val graph = request.graph
    val datasource = request.datasource
    val targetLabel = request.targetLabel
    val numOfSplit = request.numOfSplit

    val csvData = sparkSession.read.csv(datasource)

    setup(graph.edges,graph.nodes,graph.commonProperties)
    initCategoryMap(csvData)

    val target = targetLabel;
    val targetIndex = invertedIndex.get(target).get

    val weight = mutable.ArrayBuffer[Double]();
    (0 until numOfSplit).foreach(index => weight += (1/numOfSplit.toDouble))

    val splittedDf = csvData.randomSplit(weight.toArray)

    var averagedAccuracy = 0.0
    (0 until numOfSplit).foreach{
      index=>
        val testDF = splittedDf(index);
        var trainingDF:Dataset[Row] = null

        var firstIndex = -1;
        if(index == 0){
          trainingDF = splittedDf(index+1)
          firstIndex = index + 1
        }else{
          trainingDF = splittedDf(index-1)
          firstIndex = index - 1
        }


        (0 until numOfSplit).foreach{
          index2 =>
            if(index != index2 && firstIndex != index2){
              trainingDF.union(splittedDf(index2))
            }
        }

        // Calculate accuracy for this fold
        var count = 0;
        var total = 0;

        for(row <- testDF.collect().toList){
          val cols = row.toSeq.toList.asInstanceOf[List[String]]
          var maxLabel = ""
          var maxValue = - Double.MaxValue

          for (targetValue <- categoricalPossibleValues.get(target).get){
            val prob = test(bandwidthMap.get(target).get,target,targetValue, cols, trainingDF)

//            println("  predict "+target+" = "+prob)
            if(prob > maxValue){
              maxLabel = targetValue;
              maxValue = prob;
            }
          }

          println("  selected "+maxLabel+" : "+cols(targetIndex))
          if(cols(targetIndex) == maxLabel){
            count += 1
          }
          total += 1
        }

        println("acurracy("+index+")="+(count.toDouble/total.toDouble))

        averagedAccuracy += (count.toDouble/total.toDouble)
    }


    (0 until numOfSplit).foreach {
      index =>
        val testDF = splittedDf(index);

        println("testDF(" + index + ") size : " + testDF.collect().size)

    }

    Map(
      "accurarcy" -> (averagedAccuracy/numOfSplit).toString
    )
  }

  def initCategoryMap(csvData: DataFrame): Unit ={
    val categoricalKeySet = distributionMap.filter(f => f._2 == "categorical").keySet
    println("find category Map for :"+categoricalKeySet+","+distributionMap)

    for(key <- categoricalKeySet) {
      val index = invertedIndex.get(key).get
      val possibleValue = collection.mutable.Set[String]()
      val final_rdd_dense = csvData.rdd.map(_.getString(index)).distinct().collect()

      categoricalPossibleValues.put(key, final_rdd_dense.toSet)
    }
    println("Initialized Category Map");
    println(categoricalPossibleValues)
  }

  override def validate(sparkSession: SparkSession, runtime: JobEnvironment, config: Config):
  JobData Or Every[ValidationProblem] = {
    println(config)
    Try(config.getString("input.string"))
      .map(words => Good(words))
      .getOrElse(Bad(One(SingleProblem("No input.string param"))))
  }

  def setup(edges:List[edge], nodes: List[node], commonProperties: List[property]): Unit ={

    allEdges = edges
    allNodes = nodes
    allNodesMap = allNodes.zipWithIndex.map { case (v, i) => v.label -> v }.toMap

    invertedIndex = allNodes.zipWithIndex.map { case (v, i) => v.label -> i }.toMap

    commonDistribution = commonProperties.filter(_.name == "distribution")(0).value
    distributionMap = mutable.Map[String,String]()
    for(node <- allNodes){
      val distribution = if(node.properties.filter(_.name == "distribution").size > 0) node.properties.filter(_.name == "distribution")(0).value else commonDistribution;
      distributionMap.put(node.label,distribution)
    }

    commonBandwidth = commonProperties.filter(_.name == "bandwidth")(0).value.toDouble
    bandwidthMap = mutable.Map[String,Double]()
    for(node <- allNodes){
      val bandwidth = if(node.properties.filter(_.name == "bandwidth").size > 0) node.properties.filter(_.name == "bandwidth")(0).value else commonBandwidth;
      bandwidthMap.put(node.label,bandwidth.toString.toDouble)
    }

    categoricalPossibleValues = collection.mutable.Map[String,Set[String]]()
  }

  def test(bandwidth: Double,targetLabel: String, targetValue: String, attr_values: List[String], trainingData: Dataset[Row]): Double={
    var totalProb = 1.0
    (0 until allNodes.length).foreach {
      nodeIndex =>

        if(!allNodes(nodeIndex).disable) {

          val categoryLabelList = collection.mutable.ListBuffer[String]();
          val realLabelList = collection.mutable.ListBuffer[String]();

          val dependentIndices = collection.mutable.ListBuffer[Int]();
          (0 until allEdges.length).foreach {
            edgeIndex =>

              if (allEdges(edgeIndex).label2 == allNodes(nodeIndex).label) {
                distributionMap.get(allEdges(edgeIndex).label1).get match {
                  case "categorical" =>
                    categoryLabelList += (allEdges(edgeIndex).label1)
                  case "real" =>
                    realLabelList += (allEdges(edgeIndex).label1)
                }
              }
          }

          //        println("categorical Label List : "+categoryLabelList.mkString(","))
          //        println("real Label List : "+realLabelList.mkString(","))

          val dependentCategoryValues = mutable.Map[String, String]()
          for (category <- categoryLabelList) {
            if (category == targetLabel) {
              dependentCategoryValues.put(category, targetValue)
            } else {
              val categoryIndex = invertedIndex.get(category).get
              dependentCategoryValues.put(category, attr_values(categoryIndex))
            }
          }


          // 1. Calculation for Numerator, e.g. P(A,B,C)
          val nodeValue = if (allNodes(nodeIndex).label == targetLabel) targetValue else attr_values(nodeIndex)

          var numerator = 0.0
          if (distributionMap.get(allNodes(nodeIndex).label).get == "categorical") {
            numerator = predict(bandwidth,(dependentCategoryValues + (allNodes(nodeIndex).label -> nodeValue)).toMap, realLabelList.toList, attr_values,trainingData)
          } else {
            numerator = predict(bandwidth,dependentCategoryValues.toMap, realLabelList.toList :+ allNodes(nodeIndex).label, attr_values,trainingData)
          }

          // 2. Calculation for Denominator, e.g. P(B,C)
          val denominator = predict(bandwidth,dependentCategoryValues.toMap, realLabelList.toList, attr_values,trainingData)

          totalProb *= (numerator / denominator)
        }
    }

    return totalProb
  }


  def predict(bandwidth: Double, categoryValues: Map[String, String], realLabelIndices:List[String], attr_values: List[String],trainingData: Dataset[Row]): Double ={
    if(realLabelIndices.size == 0){
      // Here, we return 1.0 when there is no dependent real variables, which means that we assume Uniform Distribution
      return 1.0
    }

    // 1. We restrict Data by Categorical Variables
    var rdd_dense: RDD[Row] = trainingData.rdd;
    for (category <- categoryValues) {
      val categoryIndex = invertedIndex.get(category._1).get
      val categoryValue = category._2
      rdd_dense = rdd_dense.filter(x =>
        x.getString(categoryIndex) == categoryValue
      )
    }
    //     // 2. This model assumes that multi dimensional kernels can be computed via Multiplicative Kernel
    val indices = realLabelIndices.map(label => invertedIndex.get(label))
    val numOfRows = rdd_dense.count()
    //     println("numOfRows : "+numOfRows)
    val kernelizedValues = rdd_dense.map {
      x =>

        val values = collection.mutable.ListBuffer[Double]()
        indices.foreach {
          index =>
            //             println(Kernel.GuassianKernel((attr_values(index.get).toDouble - x.getString(index.get).toDouble)/bandwidth)+":"+attr_values(index.get) +","+x.getString(index.get)+","+bandwidth)
            val value = (Kernel.GuassianKernel((attr_values(index.get).toDouble - x.getString(index.get).toDouble)/bandwidth))/(numOfRows*bandwidth)
            values += value
        }
        Vectors.dense(values.toArray[Double])
    }.reduce {
      (a, b) =>
        Vectors.dense((a.toArray, b.toArray).zipped.map(_ + _))
    }
    //
    //     println("kernelized values");
    //     println(kernelizedValues)
    // 3. Then simply multiply each kernel value to get the final prediction value
    var predict = 1.0

    kernelizedValues.toArray.foreach( kernel => predict *= kernel)

    return predict;
  }
}
