package services

import gml.{edge, node, property}
import org.apache.spark.SparkConf
import org.apache.spark.mllib.linalg.{Matrix, Vectors}
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.stat.{MultivariateStatisticalSummary, Statistics}
import org.apache.spark.mllib.stat.distribution.MultivariateGaussian
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, Row, _}

import scala.collection.mutable
import scala.io.Source

/**
  * Created by itomao on 8/15/18.
  */
object Kernel{
  def GuassianKernel(data: Double): Double = Math.exp(-data*data/2)/Math.sqrt(2*Math.PI);
}

class ModelKernelDensity extends Model{


  var allEdges: List[edge] = null;
  var allNodes: List[node] = null;
  var allNodesMap: Map[String, node] = null;

  var invertedIndex: Map[String, Int] = null;

  var commonDistribution: String = null;
  var distributionMap: mutable.Map[String, String] = null;

  var commonBandwidth: Double = 10.0;
  var bandwidthMap: mutable.Map[String, Double] = null;

  var categoricalPossibleValues : collection.mutable.Map[String, Set[String]] = null;

  def getJointId(nodesLabel: List[String]): String=nodesLabel.sorted.mkString(".")

  var trainingData: Dataset[Row] = null;
  var sparkConf: SparkConf = null;
  var sparkSession: SparkSession = null;


  def getModelName: String = "Freq_and_Kernel_Density"

  def getModelParameterInfo: List[String] = List[String](
    "distribution",
    "bandwidth"
  )

  def setup(_sparkConf: SparkConf, _sparkSession: SparkSession, edges:List[edge], nodes: List[node], commonProperties: List[property]): Unit ={
    sparkConf = _sparkConf
    sparkSession = _sparkSession

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

  def testByCrossValidation(datasource: String,targetLabel: String, numOfSplit: Int): Double={
    val csvData = sparkSession.read.csv(datasource)

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
            val prob = test(bandwidthMap.get(target).get,target,targetValue, cols, trainingDF)

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

        println("acurracy("+index+")="+(count.toDouble/total.toDouble))

        averagedAccuracy += (count.toDouble/total.toDouble)
    }


    (0 until numOfSplit).foreach {
      index =>
        val testDF = splittedDf(index);

        println("testDF(" + index + ") size : " + testDF.collect().size)

    }
    return averagedAccuracy/numOfSplit
  }

  /**
    * Kernel Density Estimation does not need to calculate any parameters
    *
    * @param csvData
    */
  def training(csvData: DataFrame): Unit ={
    trainingData = csvData

    initCategoryMap(csvData)
  }

  /**
    * Calculate hyper parameters for joint probability,
    * i.e. P(all nodes) = P(A |..)P(B |..)...
    *
    */
  def training(datasource: String): Unit ={
    //    val csvData = sparkSession.read.csv("/Users/itomao/Documents/GMB/DemoDataSet/abalone_test_answer.csv")
    println("Load "+datasource+" ...")
    val csvData = sparkSession.read.csv(datasource)

    training(csvData)
  }


  def testSimple(testsource : String, targetLabel: String): Double ={
    val csvData = testsource

    val target = targetLabel;
    val targetIndex = invertedIndex.get(target).get

    var count = 0;
    var total = 0;
    for (line <- Source.fromFile(csvData).getLines()) {
      val cols = line.split(",").toList

      var maxLabel = ""
      var maxValue = - Double.MaxValue

      for (targetValue <- categoricalPossibleValues.get(target).get){
        val prob = test(bandwidthMap.get(target).get,target,targetValue, cols,trainingData)

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

    return (count.toDouble/total.toDouble)
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

  def predictByGuassianKernel(numOfRows:Long,bandwidth: Double, value: Double, index:Int, restrictedData: RDD[Row]): Double={
    val sum = restrictedData.map {
      x =>(Kernel.GuassianKernel((value - x.getString(index).toDouble)/bandwidth))
    }.reduce{
      (a, b) =>
        if(a == 0) println("Fined 000000!");
        a+b
    }

    return sum/(numOfRows*bandwidth)
  }

  def predict(bandwidth: Double, categoryValues: Map[String, String], realLabelIndices:List[String], attr_values: List[String],trainingData: Dataset[Row]): Double ={
     if(realLabelIndices.size == 0){
       println("zero array")
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
     println("numOfRows : "+numOfRows)
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

     println("kernelized values");
     println(kernelizedValues)
     // 3. Then simply multiply each kernel value to get the final prediction value
     var predict = 1.0

     kernelizedValues.toArray.foreach( kernel => predict *= kernel)

     return predict;
  }
}
