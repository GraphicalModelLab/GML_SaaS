package services

import java.io.File
import java.util

import gml.{edge, node, property, trainingRequest}
import org.apache.spark.SparkConf
import org.apache.spark.mllib.linalg.{Matrix, Vectors}
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.stat.distribution.MultivariateGaussian
import org.apache.spark.mllib.stat.{MultivariateStatisticalSummary, Statistics}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._

import scala.collection.mutable
import scala.io.Source

/**
  * Created by itomao on 6/15/18.
  */
class ModelSimpleCSV(request: trainingRequest) extends Model{

  val dataSource: String = request.datasource
  val allEdges: List[edge] = request.edges
  val allNodes: List[node] = request.nodes
  val allNodesMap: Map[String, node] = allNodes.zipWithIndex.map { case (v, i) => v.label -> v }.toMap

  val invertedIndex = allNodes.zipWithIndex.map { case (v, i) => v.label -> i }.toMap

  val commonProperties: List[property] = request.commonProperties
  val commonDistribution = commonProperties.filter(_.name == "distribution")(0).value
  val distributionMap: mutable.Map[String, String] = mutable.Map[String,String]()
  for(node <- allNodes){
    val distribution = if(node.properties.filter(_.name == "distribution").size > 0) node.properties.filter(_.name == "distribution")(0).value else commonDistribution;
    distributionMap.put(node.label,distribution)
  }

  val categoricalPossibleValues : Map[String, Set[String]] = Map[String,Set[String]]("sex" -> Set[String]("I","F","M"))

  val guassianHyperParam = mutable.Map[String, MultivariateGaussian]()

  def getJointId(nodesLabel: List[String]): String=nodesLabel.sorted.mkString(".")


  val sparkConf: SparkConf = new SparkConf().setAppName("Model").setMaster("local")
  val sparkSession: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()
  /**
    * Calculate hyper parameters for joint probability,
    * i.e. P(all nodes) = P(A |..)P(B |..)...
    *
    */
  def training(datasource: String): Unit ={

//    val csvData = sparkSession.read.csv("/Users/itomao/Documents/GMB/DemoDataSet/abalone_test_answer.csv")
    println("Load "+datasource+" ...")
    val csvData = sparkSession.read.csv(datasource)

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

    println("Test outputs ---- ")
    predict(Map[String,String](
      "sex" -> "M"
    ),List[String]("length"),List[String]("M","0.585"))
    predict(Map[String,String](
      "sex" -> "F"
    ),List[String]("length"),List[String]("F","0.585"))
    predict(Map[String,String](
      "sex" -> "I"
    ),List[String]("length"),List[String]("I","0.585"))
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


  def test(testsource : String): Unit ={

//    val csvData = "/Users/itomao/Documents/GMB/DemoDataSet/abalone_test_answer.csv"
    var csvData = testsource

    println("test "+csvData)

    val target = "sex"
    val targetIndex = invertedIndex.get(target).get

    var count = 0;
    var total = 0;
    for (line <- Source.fromFile(csvData).getLines()) {
      val cols = line.split(",").toList

      var maxLabel = ""
      var maxValue = - Double.MaxValue

      for (targetValue <- categoricalPossibleValues.get(target).get){
        val prob = test(target,targetValue, cols)

        println("  predict "+target+" = "+prob)
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
          var nodeValue = if (allNodes(nodeIndex).label == targetLabel) targetValue else attr_values(nodeIndex)

          var numerator = 0.0
          if (distributionMap.get(allNodes(nodeIndex).label).get == "categorical") {
            numerator = predict((dependentCategoryValues + (allNodes(nodeIndex).label -> nodeValue)).toMap, realLabelList.toList, attr_values)
          } else {
            numerator = predict(dependentCategoryValues.toMap, realLabelList.toList :+ allNodes(nodeIndex).label, attr_values)
          }


          // 2. Calculation for Denominator, e.g. P(B,C)
          var denominator = predict(dependentCategoryValues.toMap, realLabelList.toList, attr_values)


          totalProb *= (numerator / denominator)
        }
    }

    return totalProb
  }

  def predict(categoryValues: Map[String, String], realLabelIndices:List[String], attr_values: List[String]): Double ={
    var jointIdentity = getJointId(categoryValues.map(d => d._2).toList ++ realLabelIndices)

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
