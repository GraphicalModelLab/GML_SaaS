package services

import java.io._

import org.apache.spark.SparkConf
import org.apache.spark.mllib.feature.StandardScaler
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._
/**
  * Created by itomao on 7/16/18.
  */
object NormalizeData {


  def addLabel(): Unit ={
  print("Add Label");
    val br = new BufferedReader(new FileReader("/Users/itomao/Documents/GMB/DemoDataSet/stock/cleansed2.csv"))
      val sb = new StringBuilder();
      var line = br.readLine();

      var lastLine:String = null;

      var output = ""

      while (line != null) {

        if(lastLine != null){
          sb.append(line);

          val currentVal = line.split(",")(3).toDouble;
          val lastVal = lastLine.split(",")(3).toDouble;
          if( currentVal > lastVal){
            sb.append(","+"up"+System.lineSeparator());
          }else{
            sb.append(","+"down"+System.lineSeparator());
          }
        }

        lastLine = line;

        line = br.readLine();
      }


      println(sb.toString())
    val file = new File("/Users/itomao/Documents/GMB/DemoDataSet/stock/cleansed2_with_Label.csv")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(sb.toString())
    bw.close()


  }

  def main(args: Array[String]): Unit = {
    addLabel()
  }

  def standarlize(args: Array[String]) {
    val sparkConf: SparkConf = new SparkConf().setAppName("Model").setMaster("local")
    val sparkSession: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()

//    var csvData = sparkSession.read.csv("/Users/itomao/Documents/GMB/DemoDataSet/abalone_test.csv")
//    var csvData = sparkSession.read.csv("/Users/itomao/Documents/GMB/DemoDataSet/bank/bank-full.csv")
    var csvData = sparkSession.read.csv("/Users/itomao/Documents/GMB/DemoDataSet/wine/winequality-red-white-no-category.csv")
//    var numOfColumns = 13
    val indices = (0 to 11).toList

    val final_rdd_dense = csvData.rdd.map {
      x =>
        val values = collection.mutable.ListBuffer[Double]()
        indices.foreach {
          index =>
            values += (x.getString(index).toDouble)
        }

        Vectors.dense(values.toArray)
    }



//      //    // Creating a Scaler model that standardizes with both mean and SD
    val scaler = new StandardScaler(withMean = true, withStd = true).fit(final_rdd_dense)
    // Scale features using the scaler model
    val scaledFeatures = scaler.transform(final_rdd_dense)


    val outIndices = (0 to 11).toList

    val csvOutput = scaledFeatures.map {
      x =>

        var string = x.toDense.values(0).toString

        val values = collection.mutable.ListBuffer[Double]()
        println(x.toArray)
        outIndices.foreach {
          index =>
            string += ","+x.toDense.values(index).toString
        }

        string
    }

//    csvOutput.saveAsTextFile("/Users/itomao/Documents/GMB/DemoDataSet/bank/bank-normalized-full.csv")
    csvOutput.saveAsTextFile("/Users/itomao/Documents/GMB/DemoDataSet/wine/winequality-normalized-red-white-no-category.csv")
  }
}
