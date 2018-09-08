package services

import org.apache.spark.mllib.linalg.Vectors

/**
  * Created by itomao on 8/15/18.
  */
object ReflectionTest {
  def main(args: Array[String]): Unit = {
    val t1 = Vectors.dense(List[Double](120,202,22).toArray)
    val t2 = Vectors.dense(List[Double](11,1,10).toArray)


    val vectors = Vectors.dense((t1.toArray, t2.toArray).zipped.map(_ + _))

    println(vectors.toArray.mkString(","));
//    println(List[Double](0.5,0.2,1).toA)
//    var k = List[Double](
//        1,1,1,1,1,1,1,1,1,1,
//      2,2,2,2,2,2,2,2,
//      3,3,3,3,3,3,
//      4,4,4,4,
//      6,6,
//      8
//    )
//    var bandwidth = 2
//    println(densityEstimate(bandwidth,1,k))
//    println(densityEstimate(bandwidth,2,k))
//    println(densityEstimate(bandwidth,3,k))
//    println(densityEstimate(bandwidth,4,k))
//    println(densityEstimate(bandwidth,5,k))
//    println(densityEstimate(bandwidth,6,k))
//    println(densityEstimate(bandwidth,8,k))

  }

  def densityEstimate(bandwidth: Double, input: Double, data: List[Double]) : Double = {
    var output = 0;

    data.map(f => GuassianKernel((f - input)/bandwidth)).sum/(data.length*bandwidth)
  }

  def GuassianKernel(data: Double): Double = {
    return Math.exp(-data*data/2)/Math.sqrt(2*Math.PI);
  }
}
