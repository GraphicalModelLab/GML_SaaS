package org.graphicalmodellab.api

import java.io.{BufferedReader, FileReader, PrintWriter}

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._

import scala.collection.mutable

/**
  * Created by itomao on 2/6/19.
  */
object test {
  def main(args: Array[String]): Unit = {
    val writer = new PrintWriter("/Users/itomao/Documents/GMB/DemoDataSet/TrustInvestment/REINS/marged_file_all_wrangled.csv", "UTF-8");

    val br = new BufferedReader(new FileReader("/Users/itomao/Documents/GMB/DemoDataSet/TrustInvestment/REINS/marged_file_category.csv"))
    var line: String = br.readLine();

    // 1. Commute
    val commuteRegex = s"""徒歩([0-9]+).*"""
    val commutePattern = commuteRegex.r

    // 2. Tanka
    val tankaRegex = s"""([0-9]+).*"""
    val tankaPattern = tankaRegex.r

    // 2. builtyear
    val builtyearRegex = s""".*から ([0-9]+)年"""
    val builtyearPattern = builtyearRegex.r

    while (line!= null) {
      // process the line.
      val body = line.split(",")

      if(body(1) != "△沿線"
        && body(3).matches(commuteRegex)
        && body(5).matches(tankaRegex)
        && body(8).matches(builtyearRegex)
      ) {
        writer.print(body.mkString(","))

        val commutePattern(commuteMinute) = body(3)
        writer.print("," + commuteMinute);

        val tankaPattern(tanka) = body(5)
        writer.print(","+tanka)

        val builtyearPattern(builtyear) = body(8)
        writer.println(","+builtyear)

      }
      line = br.readLine()

    }

    br.close()
    writer.close();
  }
}
