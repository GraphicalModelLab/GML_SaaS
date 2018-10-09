package services.caulculationmodel
import java.nio.file.{Files, Paths}

import gml.sparkJobContextRequest
import org.apache.commons.lang3.StringEscapeUtils
import play.api.libs.json._
import org.graphicalmodellab.api.graph_api.{graph, _}
import org.apache.spark.SparkConf
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.linalg.{Matrix, Vectors}
import org.apache.spark.mllib.stat.distribution.MultivariateGaussian
import org.apache.spark.mllib.stat.{MultivariateStatisticalSummary, Statistics}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.codehaus.jettison.json.{JSONArray, JSONObject}
import org.graphicalmodellab.api.Model
import play.Play
import play.api.libs.ws._

import scala.collection.mutable
import scala.concurrent.Future
import scala.io.Source
import scalaj.http
import scalaj.http.{Base64, Http, MultiPart}
import java.io.File
import com.typesafe.config.{ Config, ConfigFactory }

/**
  * Created by itomao on 9/13/18.
  */
class ModelKernelDensityCSV extends Model{
  val config = ConfigFactory.load("model_kernel_density.conf")

  // Three Parameters for spark-job-server
  val contextName = "model_kernel_density_context"
  val appNameSparkJob = "model_kernel_density_app"
  val appJar = config.getString("app.jar")
  val classPath = "org.graphicalmodellab.model.TestByCrossValidation"
  val sparkJobServerHost = config.getString("spark.job.server.host")

  override def getModelName: String = "KernelDensity"

  override def getModelParameterInfo: List[String] = List[String](
    "distribution",
    "bandwidth"
  )

  override def getSupportedEvaluationMethod: List[String] = List[String] (
    //    Model.EVALUATION_METHOD_SIMPLE,
    Model.EVALUATION_METHOD_CROSS_VALIDATION
  )

  override def init(): Unit ={
    // 1. Generate Context
    val existingSparkContext = Json.fromJson[sparkJobContextRequest](Json.parse(Http("http://"+sparkJobServerHost+":8090/contexts").timeout(connTimeoutMs = 4000, readTimeoutMs = 9000 ).asString.body))

    if(!existingSparkContext.get.context.contains(contextName)) {
      val response1 = Http("http://"+sparkJobServerHost+":8090/contexts/"+contextName+"?num-cpu-cores=1&memory-per-node=512m&spark.executor.instances=1&context-factory=spark.jobserver.context.SessionContextFactory&timeout=200")
        .timeout(connTimeoutMs = 40000, readTimeoutMs = 90000 ).postData("").asString

      println(response1)
    }

    // 2. Upload Jar
    val bytes: Array[Byte] = Files.readAllBytes(Paths.get(appJar))
    val response2 = Http("http://"+sparkJobServerHost+":8090/jars/"+appNameSparkJob)
      .timeout(connTimeoutMs = 40000, readTimeoutMs = 90000 )
      .header("Content-Type", "application/java-archive")
      .postData(bytes)
      .asString
    println(response2)

  }

  override def training(graph: graph, datasource: String): Unit = {

  }

  override def testSimple(graph: graph, testsource: String, targetLabel: String): Double = {

    return -1
  }

  override def testByCrossValidation(graph: graph, datasource: String, targetLabel: String, numOfSplit: Int): Double ={
    val jsonString = Json.stringify(Json.toJson(graph))

    val requestString =
      "{"+ "\"datasource\":\""+datasource+"\","+"\"targetLabel\":\""+targetLabel+"\","+"\"numOfSplit\":"+numOfSplit+",\"graph\":"+ jsonString+"}"
    val base64Encoded = Base64.encodeString(requestString)

    val responseJson = new JSONObject(Http("http://"+sparkJobServerHost+":8090/jobs?appName="+appNameSparkJob+"&context="+contextName+"&classPath="+classPath)
      .timeout(connTimeoutMs = 4000, readTimeoutMs = 9000 )
      .postData("input.string = \""+base64Encoded+"\"")
      .asString.body)

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

  override def exploreStructure(graph: graph,targetLabel: String, datasource: String): (graph, Double) = {
    return (graph, -1)
  }
}
