package services.caulculationmodel
import java.nio.file.{Files, Paths}

import com.typesafe.config.ConfigFactory
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
  val config = ConfigFactory.load("model_multivariate_guassian.conf")

  // Three Parameters for spark-job-server
  val contextName = "model_multivariate_guassian_context"
  val appNameSparkJob = "model_multivariate_guassian_app"
  val appJar = config.getString("app.jar")
  val classPath = "org.graphicalmodellab.model.TestByCrossValidation"
  val sparkJobServerHost = config.getString("spark.job.server.host")

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

  override def init(): Unit ={
    // 1. Generate Context
    val existingSparkContext = Json.fromJson[sparkJobContextRequest](Json.parse(Http("http://"+sparkJobServerHost+":8090/contexts").timeout(connTimeoutMs = 4000, readTimeoutMs = 9000 ).asString.body))

    if(!existingSparkContext.get.context.contains(contextName)) {
      val response1 = Http("http://"+sparkJobServerHost+":8090/contexts/"+contextName+"?num-cpu-cores=1&memory-per-node=512m&spark.executor.instances=1&context-factory=spark.jobserver.context.SessionContextFactory")
        .timeout(connTimeoutMs = 4000, readTimeoutMs = 90000 ).postData("").asString

      println(response1)
    }

    // 2. Upload Jar
    val bytes: Array[Byte] = Files.readAllBytes(Paths.get(appJar))
    val response2 = Http("http://"+sparkJobServerHost+":8090/jars/"+appNameSparkJob)
                    .timeout(connTimeoutMs = 4000, readTimeoutMs = 9000 )
                    .header("Content-Type", "application/java-archive")
                    .postData(bytes)
                    .asString
    println(response2)

  }

  override def testByCrossValidation(graph:graph, datasource: String,targetLabel: String, numOfSplit: Int): Double={
    val jsonString = Json.stringify(Json.toJson(graph))

    val requestString =
      "{"+ "\"datasource\":\""+datasource+"\","+"\"targetLabel\":\""+targetLabel+"\","+"\"numOfSplit\":"+numOfSplit+",\"graph\":"+ jsonString+"}"
    val base64Encoded = Base64.encodeString(requestString)

    val responseJson = new JSONObject(Http("http://"+sparkJobServerHost+":8090/jobs?appName="+appNameSparkJob+"&context="+contextName+"&classPath="+classPath)
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
      val statusResponse = new JSONObject(Http("http://"+sparkJobServerHost+":8090/jobs/"+jobId)
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
  override def training(graph:graph, datasource: String): Unit ={

  }

  override def testSimple(graph:graph, testsource : String, targetLabel: String): Double ={

    return 0.0
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
