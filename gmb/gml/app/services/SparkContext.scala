package services

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  * Created by itomao on 8/16/18.
  */
object SparkContext {
  val sparkConf: SparkConf = new SparkConf().setAppName("Model").setMaster("local")
  val sparkSession: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
}
