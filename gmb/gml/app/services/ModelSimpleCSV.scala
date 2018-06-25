package services

import java.io.File

import gml.{edge, trainingRequest}
import org.apache.spark.mllib.linalg.DenseVector
import org.apache.spark.mllib.linalg.{Matrix, Vector, Vectors}
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.stat.distribution.MultivariateGaussian
import org.apache.spark.mllib.stat.{MultivariateStatisticalSummary, Statistics}
import org.apache.spark.sql._
import org.apache.spark.{SparkConf, SparkContext}
import org.datavec.api.records.reader.impl.csv.CSVRecordReader
import org.datavec.api.split.FileSplit
import org.datavec.api.util.ClassPathResource

import scala.collection.mutable

/**
  * Created by itomao on 6/15/18.
  */
class ModelSimpleCSV(request: trainingRequest) extends Model{

  val dataSource: String = request.datasource
  val allEdges: List[edge] = request.edges
  val allNodes: List[String] = request.nodes
  val invertedIndex = allNodes.zipWithIndex.map { case (v, i) => v -> i }.toMap

  val sparkConf = new SparkConf().setAppName("Model").setMaster("local")
  val sparkContext = new SparkContext(sparkConf)
  val sparkSession = SparkSession.builder().config(sparkConf).getOrCreate()

  val guassianHyperParam = mutable.Map[String, MultivariateGaussian]()

  def getJointId(nodesIndex: List[Int]): String=nodesIndex.sortBy(a => -a).mkString(".")

  /**
    * Calculate hyper parameters for joint probability,
    * i.e. P(all nodes) = P(A |..)P(B |..)...
    *
    */
  def training(): Unit ={
    val csvData = sparkSession.read.csv(dataSource)


    (0 until allNodes.length).foreach {
      nodeIndex => print(nodeIndex)

        val dependentIndices = collection.mutable.ListBuffer[Int]();
        (0 until allEdges.length).foreach {
          edgeIndex =>
            if (allEdges(edgeIndex).label2 == allNodes(nodeIndex)) {
              dependentIndices += invertedIndex(allEdges(edgeIndex).label1)
            }
        }

        // 1. Calculation for Numerator
        training(allNodes(nodeIndex),nodeIndex, dependentIndices.toList,csvData)

        // 2. Calculation for Denominator
//        if(dependentIndices.size == 0){
//          training(dependentIndices.toList, csvData)
//        }
    }
  }

  def training(categoryLabel: String, categoryIndex: Int, jointIndices: List[Int], data:DataFrame) : Unit={
    var jointIdentity = getJointId(jointIndices.toList)

    if(!guassianHyperParam.contains(jointIdentity)) {
      val rdd_dense = data.rdd.filter(x=> x.getString(categoryIndex) == categoryLabel).map {
        x =>
          val values = collection.mutable.ListBuffer[Double]()
          jointIndices.foreach {
            index =>
              values += x.getDouble(index)
          }

          Vectors.dense(values.toArray)
      }

      val mat: RowMatrix = new RowMatrix(rdd_dense)
      val summary: MultivariateStatisticalSummary = Statistics.colStats(rdd_dense)
      val covariance: Matrix = mat.computeCovariance()

      guassianHyperParam.put(jointIdentity, new MultivariateGaussian(summary.mean, covariance))
    }
  }
  /**
    * return the joint probability over nodes
    *
    * @param nodesIndex
    * @return P( nodes )
    */
  def jointProbabilityByGuassian(nodesIndex: List[Int], predictedValues: DenseVector) : Double = {
    return guassianHyperParam(getJointId(nodesIndex)).pdf(predictedValues)
  }


//  def conditionalProbabilityByGuassian(targetNodeId: Int, dependingNodes: List[Int], predictedValues: DenseVector): Double = {
//    return jointProbabilityByGuassian(targetNodeId :: dependingNodes) / jointProbabilityByGuassian(dependingNodes);
////    return 0.0.toDouble;
//  }
}
