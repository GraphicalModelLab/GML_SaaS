package services

import org.apache.spark.SparkConf
import org.apache.spark.mllib.linalg.{Matrix, Vectors}
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.stat.{MultivariateStatisticalSummary, Statistics}
import org.apache.spark.sql.SparkSession
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
  * Created by itomao on 7/14/18.
  */
object TestModelSimpleCSV {
  val logger = LoggerFactory.getLogger("test");

  val categoricalPossibleValues : Map[String, Set[String]] = Map[String,Set[String]]("sex" -> Set[String]("I","F","M"),"type" -> Set[String]("strong","week"))
  def main(args: Array[String]){
    logger.info("Now start test model simple csv ...");

    val appJar = "/Users/itomao/multivariateguassian_2.11.jar";
    val jars = List[String]()
    val SPARK_HOME = "/usr/local/Cellar/apache-spark/2.3.1/"
    val env = Map[String,String](
      "SPARK_PRINT_LAUNCH_COMMAND" -> "1"
    )
    val master = "local[2]";
    val mainClass = "org.graphicalmodellab.model.TestByCrossValidation";
    val appName = "Multivariate Guassian Model";
    var userid : String = "test";

    val sparkProcessManager: SparkProcessManager = new SparkProcessManager();
    val uuid = sparkProcessManager.createSparkProcess(
      userid,
      SPARK_HOME,
      master,
      mainClass,
      appName,
      appJar,
      jars,
      env,
      List[String](
        //        Json.prettyPrint(Json.toJson(graph))
      ));
////    training_helper(List[String]("sex", "type"), Map[String,String](), 0);
//
//    val sparkConf = new SparkConf().setAppName("Model").setMaster("local")
//    //  val sparkContext = new SparkContext(sparkConf)
//    val sparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
////    val csvData = sparkSession.read.csv("/Users/itomao/Documents/GMB/DemoDataSet/abalone_test.csv")
//    val csvData = sparkSession.read.csv("/Users/itomao/Documents/GMB/DemoDataSet/bank/bank-full.csv")
//
//    val final_rdd_dense = csvData.rdd.map {
//      x =>
//        //                          val values = collection.mutable.ListBuffer[Double]()
//        //                          values += x.getDouble(1)
//        //                          indices.foreach {
//        //                            index =>
//        //                              values += x.getDouble(1)
//        //                            //                              values += 19.2
//        //                          }
//        //                          val values : double[] = new double[]{12,22}
//        Vectors.dense(Array[Double](11.1, 22.2))
//      //              Vectors.dense(values.toArray)
//      //          Vectors.dense(values.toArray)
//    }
//    //          val final_rdd_dense: RDD[Vector] = rdd_dense.map {
//    //            x =>
//    //              val values = collection.mutable.ListBuffer[Double]()
//    //              values += x.getDouble(1)
//    //              indices.foreach {
//    //                index =>
//    //                  values += x.getDouble(1)
//    //                //                              values += 19.2
//    //              }
//    //              Vectors.dense(Array[Double](12,2,3,3))
//    ////              Vectors.dense(values.toArray)
//    //            //          Vectors.dense(values.toArray)
//    //          }
//
//    val mat: RowMatrix = new RowMatrix(final_rdd_dense)
//    val summary: MultivariateStatisticalSummary = Statistics.colStats(final_rdd_dense)
//    val covariance: Matrix = mat.computeCovariance()
//
//    println("Found : ")
//    println(summary.mean)
  }

  def training_helper(categoryList: List[String], categoryValues: Map[String, String], currentIndex: Int) : Unit={
    if(currentIndex == categoryList.size) {
      println(categoryValues)
    }else {
      println(categoricalPossibleValues.get(categoryList(currentIndex)))
      for (categoryValue <- categoricalPossibleValues.get(categoryList(currentIndex)).get) {
        training_helper(categoryList, categoryValues + (categoryList(currentIndex) -> categoryValue.toString), currentIndex + 1)
      }
    }
  }
}
