package services.caulculationmodel
import java.nio.file.{Files, Paths}

import gml.sparkJobContextRequest
import org.apache.commons.lang3.StringEscapeUtils
import play.api.libs.json._
import org.graphicalmodellab.api.graph_api._
import org.apache.spark.SparkConf
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.linalg.{Matrix, Vectors}
import org.apache.spark.mllib.stat.distribution.MultivariateGaussian
import org.apache.spark.mllib.stat.{MultivariateStatisticalSummary, Statistics}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.codehaus.jettison.json.{JSONArray, JSONObject}
import org.graphicalmodellab.api.Model
import play.api.libs.ws._

import scala.collection.mutable
import scala.concurrent.Future
import scala.io.Source
import scalaj.http
import scalaj.http.{Base64, Http, MultiPart}
import play.api.Logger

/**
  * Created by itomao on 6/15/18.
  */

class ModelMultivariateGuassianCSV extends Model{
  // Three Parameters for spark-job-server
  val contextName = "multi"
  val appNameSparkJob = "multi"
  val appJar = "/Users/itomao/git/GML_SaaS/model/sample_model/multivariateguassian/target/scala-2.11/multivariateguassian-assembly-0.1-SNAPSHOT.jar";
  val classPath = "org.graphicalmodellab.model.TestByCrossValidation"

  // Parameters for TestSimple & Training Methods
  var invertedIndex: Map[String, Int] = null;
  var commonDistribution: String = null;
  var distributionMap: mutable.Map[String, String] = null;
  var categoricalPossibleValues : collection.mutable.Map[String, Set[String]] = null;
  var guassianHyperParam : mutable.Map[String, MultivariateGaussian] = null;

  def getModelName: String = "Freq_and_Multi"

  def getSupportedEvaluationMethod: List[String] = List[String] (
//    Model.EVALUATION_METHOD_SIMPLE,
    Model.EVALUATION_METHOD_CROSS_VALIDATION
  )

  def getModelParameterInfo: List[String] = List[String](
    "distribution"
  )

  def init(): Unit ={
    // 1. Generate Context
    val existingSparkContext = Json.fromJson[sparkJobContextRequest](Json.parse(Http("http://localhost:8090/contexts").timeout(connTimeoutMs = 4000, readTimeoutMs = 9000 ).asString.body))

    if(!existingSparkContext.get.context.contains(contextName)) {
      val response1 = Http("http://localhost:8090/contexts/"+contextName+"?num-cpu-cores=1&memory-per-node=512m&spark.executor.instances=1&context-factory=spark.jobserver.context.SessionContextFactory")
        .timeout(connTimeoutMs = 4000, readTimeoutMs = 90000 ).postData("").asString

      println(response1)
    }

    // 2. Upload Jar
    val bytes: Array[Byte] = Files.readAllBytes(Paths.get(appJar))
    val response2 = Http("http://localhost:8090/jars/"+appNameSparkJob)
                    .timeout(connTimeoutMs = 4000, readTimeoutMs = 9000 )
                    .header("Content-Type", "application/java-archive")
                    .postData(bytes)
                    .asString
    println(response2)

  }

  def testByCrossValidation(graph:graph, datasource: String,targetLabel: String, numOfSplit: Int): Double={
    val jsonString = Json.stringify(Json.toJson(graph))

    val requestString =
      "{"+ "\"datasource\":\""+datasource+"\","+"\"targetLabel\":\""+targetLabel+"\","+"\"numOfSplit\":"+numOfSplit+",\"graph\":"+ jsonString+"}"
    val base64Encoded = Base64.encodeString(requestString)

    val responseJson = new JSONObject(Http("http://localhost:8090/jobs?appName="+appNameSparkJob+"&context="+contextName+"&classPath="+classPath)
                    .timeout(connTimeoutMs = 4000, readTimeoutMs = 9000 )
                    .postData("input.string = \""+base64Encoded+"\"")
                    .asString.body)

    Logger.logger.info("First response");
    Logger.logger.info(responseJson.toString());
    if(responseJson.get("status") == "ERROR") return -1;

    val jobId = responseJson.get("jobId")

    // Sometimes, if we try to get job status just right after registering, you get Error, "No such job ID...". Thus, put a sleep before getting status
    Thread.sleep(5000);
    while(true){
      val statusResponse = new JSONObject(Http("http://localhost:8090/jobs/"+jobId)
        .asString.body)

      statusResponse.get("status") match {
        case "ERROR" => return -1
        case "FINISHED" => return statusResponse.getJSONObject("result").getDouble("accurarcy")
        case _ =>
      }

      Thread.sleep(1000);
    }

    return -1;
  }

  /**
    * Calculate hyper parameters for joint probability,
    * i.e. P(all nodes) = P(A |..)P(B |..)...
    *
    */
  def training(graph:graph, datasource: String): Unit ={

  }

  def testSimple(graph:graph, testsource : String, targetLabel: String): Double ={
    var csvData = testsource

    val target = targetLabel;
    val targetIndex = invertedIndex.get(target).get

    var count = 0;
    var total = 0;
    for (line <- Source.fromFile(csvData).getLines()) {
      val cols = line.split(",").toList

      var maxLabel = ""
      var maxValue = - Double.MaxValue

      for (targetValue <- categoricalPossibleValues.get(target).get){
        val prob = test(graph, target,targetValue, cols)

        if(prob > maxValue){
          maxLabel = targetValue;
          maxValue = prob;
        }
      }

      println("  selected "+maxLabel+" , "+maxValue)
      if(cols(targetIndex) == maxLabel){
        count += 1
      }
      total += 1
    }

    println("accuracy ("+count+"/"+total+") = "+(count.toDouble/total.toDouble))

    return (count.toDouble/total.toDouble)
  }

  def test(graph:graph, targetLabel: String, targetValue: String, attr_values: List[String]): Double={
    var totalProb = 1.0
    (0 until graph.nodes.length).foreach {
      nodeIndex => print(nodeIndex)

        if(!graph.nodes(nodeIndex).disable) {

          val categoryLabelList = collection.mutable.ListBuffer[String]();
          val realLabelList = collection.mutable.ListBuffer[String]();

          val dependentIndices = collection.mutable.ListBuffer[Int]();
          (0 until graph.edges.length).foreach {
            edgeIndex =>

              if (graph.edges(edgeIndex).label2 == graph.nodes(nodeIndex).label) {
                distributionMap.get(graph.edges(edgeIndex).label1).get match {
                  case "categorical" =>
                    categoryLabelList += (graph.edges(edgeIndex).label1)
                  case "real" =>
                    realLabelList += (graph.edges(edgeIndex).label1)
                }
              }
          }

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
          val nodeValue = if (graph.nodes(nodeIndex).label == targetLabel) targetValue else attr_values(nodeIndex)

          var numerator = 0.0
          if (distributionMap.get(graph.nodes(nodeIndex).label).get == "categorical") {
            numerator = predict((dependentCategoryValues + (graph.nodes(nodeIndex).label -> nodeValue)).toMap, realLabelList.toList, attr_values)
          } else {
            numerator = predict(dependentCategoryValues.toMap, realLabelList.toList :+ graph.nodes(nodeIndex).label, attr_values)
          }


          // 2. Calculation for Denominator, e.g. P(B,C)
          var denominator = predict(dependentCategoryValues.toMap, realLabelList.toList, attr_values)

          totalProb *= (numerator / denominator)
        }
    }

    return totalProb
  }

  def getJointId(nodesLabel: List[String]): String=nodesLabel.sorted.mkString(".")

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

  var exploreCount = 0;
  var exploreAccuracy = -1;

  override def exploreStructure(graphInfo: graph,targetLabel: String, datasource: String): (graph, Double) = {

    val edges = mutable.ListBuffer[edge]();

    (0 until graphInfo.edges.length-2).foreach{
      index =>
        edges += graphInfo.edges(index)
    }

    val newGraph = graph(
      graphInfo.modelid,
      graphInfo.modelname,
      graphInfo.modeltag,
      graphInfo.modeldescription,
      graphInfo.userid,
      graphInfo.algorithm,
      graphInfo.nodes,
      edges.toList,
      graphInfo.commonProperties
    )

    val accuracy = testByCrossValidation(newGraph,datasource,targetLabel,10)

    return (newGraph, accuracy)
  }
}
