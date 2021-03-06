/**
  * Copyright (C) 2018 Mao Ito
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

import sbt._
import sbt.Keys.scalaVersion
import CommonSettings._;

object Dependencies {

  /* Resolvers */
  val resolvers = DefaultOptions.resolvers(snapshot = true) ++ Seq(
    "scalaz-releases" at "http://dl.bintray.com/scalaz/releases",
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
  )

  /* Libraries */
  // Logger
  val scalaLoggingSlf4j = "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
  val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.0.9"

  // HttpURLConnection
  val scalaj = "org.scalaj" %% "scalaj-http" % "1.1.5"
  val netty = "io.netty" % "netty-transport-native-epoll" % "4.1.3.Final" classifier "linux-x86_64"


  // Scala Play Test, https://github.com/playframework/scalatestplus-play#releases
  val scalatest = "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  val scalatestplus =  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.1" % "test"
  val mockitoCore = "org.mockito" % "mockito-core" % "2.1.0" % "test"

  // Parcer
  val jon4s = "org.json4s" %% "json4s-native" % "3.2.11"
  //  val playJson = "com.typesafe.play" %% "play-json" % "2.3.6"

  val jacksonCore = "com.fasterxml.jackson.core" % "jackson-core" % "2.7.4"
  val jacksonAnnotation = "com.fasterxml.jackson.core" % "jackson-annotations" % "2.7.4"
  val jacksonDatabind = "com.fasterxml.jackson.core" % "jackson-databind" % "2.7.4"
  val jacksonDataformatYaml = "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.7.4"
  val jacksonModule = "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.7.4"
  val jettison = "org.codehaus.jettison" % "jettison" % "1.3.7"

  // Time Operation
  val jodaTime = "joda-time" % "joda-time" % "2.3"
  val jodaConvert = "org.joda" % "joda-convert" % "1.6"

  // Database
  val cassandraDriver = "com.datastax.cassandra" % "cassandra-driver-core" % "3.0.0"

  // Google API Common Library
  val google_api_client = "com.google.api-client" % "google-api-client" % "1.21.0"
  val google_oauth_client = "com.google.oauth-client" % "google-oauth-client" % "1.21.0"
  val google_http_client = "com.google.http-client" % "google-http-client" % "1.21.0"
  val google_apis = "com.google.apis" % "google-api-services-analytics" % "v3-rev130-1.22.0"

  // PlayFramework
  val jdbc = "com.typesafe.play" %% "play-jdbc" % playVersion
  val cache = "com.typesafe.play" %% "play-cache" % playVersion
  val ws = "com.typesafe.play" %% "play-ws" % playVersion
  val json = "com.typesafe.play" %% "play-json" % playVersion
  val webdriver = "com.typesafe" %% "webdriver" % playVersion
  val jse = "com.typesafe" %% "jse" % playVersion
  val npm = "com.typesafe" %% "npm" % playVersion
  val buildlink = "com.typesafe" %% "npm" % playVersion

  // ND4j
  // Deep Learning 4 j
  // For Deep learning
  val dl4j_core = "org.deeplearning4j" % "deeplearning4j-core" % "0.4-rc3.8"
  val dl4j_nlp = "org.deeplearning4j" % "deeplearning4j-nlp" % "0.4-rc3.8"
  val dl4j_ui = "org.deeplearning4j" % "deeplearning4j-ui" % "0.4-rc3.8"
  val jblas = "org.jblas" % "jblas" % "1.2.4"
  val canova = "org.nd4j" % "canova-nd4j-codec" % "0.0.0.14"
  val nd4j = "org.nd4j" % "nd4j-x86" % "0.4-rc3.8"
  val datavec_api = "org.datavec" % "datavec-api" % "0.9.1"

  // Reflection
  val scala_reflect = "org.scala-lang" % "scala-reflect" % "2.10.2"

  // Spark Mllib
  val de_unkrig_jdisasm = "de.unkrig.jdisasm" % "jdisasm" % "1.0.0"
  val spark_core = "org.apache.spark" %% "spark-core" % "2.2.0"
  val spark_sql = "org.apache.spark" %% "spark-sql" % "2.2.0"
  val spark_mllib = "org.apache.spark" %% "spark-mllib" % "2.2.0"

  // Elastic
  val elasticsearch = "org.elasticsearch" % "elasticsearch" % "6.3.1"
  val elasticsearch_transport = "org.elasticsearch.client" % "transport" % "6.3.1"

  // Scraping
  val scala_scraping_ruippeixotog = "net.ruippeixotog" %% "scala-scraper" % "1.0.0"

  val scala_scraping = Seq(
    scala_scraping_ruippeixotog
  )

  val elasticSearchDependencies = Seq(
    elasticsearch,
    elasticsearch_transport
  )

  val googleApiDependencies = Seq(
    google_api_client,
    google_oauth_client,
    google_http_client,
    google_apis
  )

  val backendDependencies = Seq(
    scalaLoggingSlf4j,
    jon4s,
    scalaj,
    netty,
    jodaTime,
    jodaConvert,
    cassandraDriver,
    jacksonCore,
    jacksonAnnotation,
    jacksonDatabind,
    jacksonDataformatYaml,
    jacksonModule,
    jettison,
    scala_reflect,
    scalatest,
    scalatestplus,
    mockitoCore,
    scala_scraping_ruippeixotog
  )

  val dl4jDependencies = Seq(
    //    dl4j_core,
    //    dl4j_nlp,
    //    dl4j_ui,
    jblas,
    //    canova,
    nd4j,
    datavec_api
  )

  val playDependencies = Seq(
    json,
    ws
  )

  val sparkDependencies = Seq(
    spark_core,
    spark_mllib,
    spark_sql,
    de_unkrig_jdisasm
  )
}