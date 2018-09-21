package org.graphicalmodellab.model

import org.apache.log4j.LogManager
import org.apache.spark.mllib.linalg.{Matrix, Vectors}
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.stat.distribution.MultivariateGaussian
import org.apache.spark.mllib.stat.{MultivariateStatisticalSummary, Statistics}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.graphicalmodellab.api.graph_api._
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.{SparkConf, SparkContext}
import spark.jobserver._
import spark.jobserver.api.{JobEnvironment, SingleProblem, ValidationProblem}
import org.scalactic._
import play.api.libs.json._
import scala.collection.mutable
import scala.util.Try
import org.graphicalmodellab.model._

import scalaj.http.Base64

/**
  * This is SparkJobSever Job
  * https://medium.com/@jwhitebear/beginners-guide-to-getting-started-with-spark-jobserver-2e3d4362ee6e
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
  * Created by itomao on 9/11/18.
  */


object TestByCrossValidation extends SparkSessionJob{
  // Setup Logger for Spark, /Users/itomao/OSS/spark-2.2.0-bin-hadoop2.7/conf/log4j.properties
  val log = LogManager.getRootLogger

  var allEdges: List[edge] = null;
  var allNodes: List[node] = null;
  var allNodesMap: Map[String, node] = null;

  var invertedIndex: Map[String, Int] = null;
  var commonDistribution: String = null;
  var distributionMap: mutable.Map[String, String] = null;
  var categoricalPossibleValues : collection.mutable.Map[String, Set[String]] = null;
  var guassianHyperParam : mutable.Map[String, MultivariateGaussian] = null;

  def getJointId(nodesLabel: List[String]): String=nodesLabel.sorted.mkString(".")

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

    categoricalPossibleValues = collection.mutable.Map[String,Set[String]]()
    //    collection.mutable.Map[String,collection.mutable.Set[String]]("category" -> collection.mutable.Set[String]("red","white"))

    guassianHyperParam = mutable.Map[String, MultivariateGaussian]()
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

            training(trainingDF)

            // Calculate accuracy for this fold
            var count = 0;
            var total = 0;

            for(row <- testDF.collect().toList){
              val cols = row.toSeq.toList.asInstanceOf[List[String]]
              var maxLabel = ""
              var maxValue = - Double.MaxValue

              for (targetValue <- categoricalPossibleValues.get(target).get){
                val prob = test(target,targetValue, cols)

    //            println("  predict "+target+" = "+prob)
                if(prob > maxValue){
                  maxLabel = targetValue;
                  maxValue = prob;
                }
              }

    //          println("  selected "+maxLabel+" , "+maxValue)
              if(cols(targetIndex) == maxLabel){
                count += 1
              }
              total += 1
            }

    //        println("acurracy("+index+")="+(count.toDouble/total.toDouble))

            averagedAccuracy += (count.toDouble/total.toDouble)
    }


    (0 until numOfSplit).foreach {
      index =>
            val testDF = splittedDf(index);

    //        println("testDF(" + index + ") size : " + testDF.collect().size)

    }

    log.info("accuray : "+averagedAccuracy/numOfSplit)

    Map(
       "accurarcy" -> (averagedAccuracy/numOfSplit).toString
    )
  }

  override def validate(sparkSession: SparkSession, runtime: JobEnvironment, config: Config):
  JobData Or Every[ValidationProblem] = {
    println(config)
    Try(config.getString("input.string"))
      .map(words => Good(words))
      .getOrElse(Bad(One(SingleProblem("No input.string param"))))
  }

  def training(csvData: DataFrame): Unit ={
    initCategoryMap(csvData)

    (0 until allNodes.length).foreach {
      nodeIndex => print(nodeIndex)

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

          println("categorical Label List : " + categoryLabelList.mkString(","))
          println("real Label List : " + realLabelList.mkString(","))

          // 1. Calculation for Numerator, e.g. P(A,B,C)
          if (distributionMap.get(allNodes(nodeIndex).label).get == "categorical") {
            training_helper(categoryLabelList.toList :+ allNodes(nodeIndex).label, Map[String, String](), 0, realLabelList.toList, csvData)
          } else {
            training_helper(categoryLabelList.toList, Map[String, String](), 0, realLabelList.toList :+ allNodes(nodeIndex).label, csvData)
          }

          // 1. Calculation for Denominator, e.g. P(B,C)
          training_helper(categoryLabelList.toList, Map[String, String](), 0, realLabelList.toList, csvData)
        }
    }

    println("Finished Learning ---- Dumping Parameters")
    for( parameter <- guassianHyperParam){
      println(" Parameter - "+parameter._1 +" , ")
      println("    Mean : "+parameter._2.mu.toArray.mkString(","))
      println("    Covariance : "+parameter._2.sigma.toArray.mkString(","))
    }
  }

  def training_helper(categoryLabelList: List[String], categoryValues: Map[String, String], currentIndex: Int, realLabelIndices: List[String], data:DataFrame) : Unit={
    if(currentIndex == categoryLabelList.size) {
      val jointIdentity = getJointId(categoryValues.map(d => d._2).toList ++ realLabelIndices)

      if(!guassianHyperParam.contains(jointIdentity)) {
        println(categoryValues)

        var rdd_dense: RDD[Row] = data.rdd;
        for (category <- categoryLabelList) {
          val categoryIndex = invertedIndex.get(category).get
          val categoryValue = categoryValues.get(category).get
          rdd_dense = rdd_dense.filter(x =>
            x.getString(categoryIndex) == categoryValue
          )
        }
        val indices = realLabelIndices.map(label=>invertedIndex.get(label)).toList
        if(indices.size > 0) {
          val final_rdd_dense = rdd_dense.map {
            x =>
              val values = collection.mutable.ListBuffer[Double]()
              indices.foreach {
                index =>
                  values += (x.getString(index.get).toDouble)
              }

              Vectors.dense(values.toArray)
          }

          val mat: RowMatrix = new RowMatrix(final_rdd_dense)
          val summary: MultivariateStatisticalSummary = Statistics.colStats(final_rdd_dense)
          val covariance: Matrix = mat.computeCovariance()

          guassianHyperParam.put(jointIdentity, new MultivariateGaussian(summary.mean, covariance))
        }
      }
    }else {
      println("categoryLabelList:"+currentIndex)
      println(categoryLabelList)
      for (categoryValue <- categoricalPossibleValues.get(categoryLabelList(currentIndex)).get) {
        training_helper(categoryLabelList, categoryValues + (categoryLabelList(currentIndex) -> categoryValue.toString), currentIndex + 1, realLabelIndices: List[String], data:DataFrame)
      }
    }
  }

  def test(targetLabel: String, targetValue: String, attr_values: List[String]): Double={
    var totalProb = 1.0
    (0 until allNodes.length).foreach {
      nodeIndex => print(nodeIndex)

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
            numerator = predict((dependentCategoryValues + (allNodes(nodeIndex).label -> nodeValue)).toMap, realLabelList.toList, attr_values)
          } else {
            numerator = predict(dependentCategoryValues.toMap, realLabelList.toList :+ allNodes(nodeIndex).label, attr_values)
          }


          // 2. Calculation for Denominator, e.g. P(B,C)
          val denominator = predict(dependentCategoryValues.toMap, realLabelList.toList, attr_values)

          totalProb *= (numerator / denominator)
        }
    }

    return totalProb
  }

  def predict(categoryValues: Map[String, String], realLabelIndices:List[String], attr_values: List[String]): Double ={
    val jointIdentity = getJointId(categoryValues.map(d => d._2).toList ++ realLabelIndices)

    if(guassianHyperParam.contains(jointIdentity)) {
      val guassian: MultivariateGaussian = guassianHyperParam.get(jointIdentity).get

      val indices = realLabelIndices.map(label => invertedIndex.get(label)).toList
      val values = collection.mutable.ListBuffer[Double]()
      indices.foreach {
        index =>
          values += (attr_values(index.get).toDouble)
      }

      Vectors.dense(values.toArray)

      val prob = guassian.pdf(Vectors.dense(values.toArray))

      println("Predict with Guassian : "+prob +" , P("+categoryValues+","+realLabelIndices+","+attr_values);

      return prob
    }else{

      //      println("Predicted Parameter : "+jointIdentity)
      return 1;
    }
  }
}
