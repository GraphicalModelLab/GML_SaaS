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

import CommonSettings._
import Dependencies._
import com.typesafe.sbt.packager.Keys.scriptClasspath
import com.typesafe.sbt.packager.Keys.bashScriptDefines

lazy val common = project.in(file("common"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= backendDependencies)
  .settings(libraryDependencies ++= googleApiDependencies)
  .settings(libraryDependencies ++= elasticSearchDependencies)
  .settings(libraryDependencies += guice)

lazy val auth = project.in(file("auth"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= playDependencies)
  .settings(libraryDependencies ++= backendDependencies)
  .settings(libraryDependencies += guice)
  .settings(assemblyMergeStrategy in assembly := {
        case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
        case PathList(ps @ _*) if ps.last endsWith ".properties" => MergeStrategy.first
        case PathList(ps @ _*) if ps.last endsWith ".xml" => MergeStrategy.first
        case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
        case PathList(ps @ _*) if ps.last endsWith ".js" => MergeStrategy.first
        case PathList(ps @ _*) if ps.last endsWith ".tooling" => MergeStrategy.first
        case PathList(ps @ _*) if ps.last endsWith ".types" => MergeStrategy.first
        case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.first
        case PathList(ps @ _*) if ps.last endsWith ".yaml" => MergeStrategy.first
        case PathList(ps @ _*) if ps.last endsWith ".txt" => MergeStrategy.first
        case PathList(ps @ _*) if ps.last endsWith ".java" => MergeStrategy.first
        case PathList(ps @ _*) if ps.last endsWith ".so" => MergeStrategy.first
        case PathList(ps @ _*) if ps.last endsWith "module-version" => MergeStrategy.first
        case "application.conf"                            => MergeStrategy.concat
        case "unwanted.txt"                                => MergeStrategy.discard
        case x =>
              val oldStrategy = (assemblyMergeStrategy in assembly).value
              oldStrategy(x)
  })
  .enablePlugins(PlayScala)
  .dependsOn(common)


// For Develop
lazy val dataex_plugin_crawler = RootProject(file("../dataex_plugin/simpledatacrawler_plugin/"))
lazy val dataex_plugin_extractor = RootProject(file("../dataex_plugin/etl_plugin/"))


lazy val gml = project.in(file("gml"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= playDependencies)
  .settings(libraryDependencies ++= backendDependencies)
//  .settings(libraryDependencies ++= dl4jDependencies)
  .settings(libraryDependencies ++= sparkDependencies)
  .settings(libraryDependencies += guice)
  .settings(Seq(
      // This is for making start up script to include this folder in the classpath as well
      scriptClasspath ~= (cp => "../extra_lib/*" +:cp)
    ))
  .settings(assemblyMergeStrategy in assembly := {
          case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
          case PathList(ps @ _*) if ps.last endsWith ".properties" => MergeStrategy.first
          case PathList(ps @ _*) if ps.last endsWith ".xml" => MergeStrategy.first
          case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
          case PathList(ps @ _*) if ps.last endsWith ".js" => MergeStrategy.first
          case PathList(ps @ _*) if ps.last endsWith ".tooling" => MergeStrategy.first
          case PathList(ps @ _*) if ps.last endsWith ".types" => MergeStrategy.first
          case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.first
          case PathList(ps @ _*) if ps.last endsWith ".yaml" => MergeStrategy.first
          case PathList(ps @ _*) if ps.last endsWith ".txt" => MergeStrategy.first
          case PathList(ps @ _*) if ps.last endsWith ".java" => MergeStrategy.first
          case PathList(ps @ _*) if ps.last endsWith ".so" => MergeStrategy.first
          case PathList(ps @ _*) if ps.last endsWith "module-version" => MergeStrategy.first
          case "application.conf"                            => MergeStrategy.concat
          case "unwanted.txt"                                => MergeStrategy.discard
          case x =>
                  val oldStrategy = (assemblyMergeStrategy in assembly).value
                  oldStrategy(x)
  })
  .enablePlugins(PlayScala)
  .dependsOn(common)
  .dependsOn(gml_api)
//  .dependsOn(dataex_plugin_crawler) // Add this dependency when developing simpledatacrawler_plugin
//  .dependsOn(dataex_plugin_extractor) // Add this dependency when developing etl_plugin

lazy val gml_api = project.in(file("gml_api"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= playDependencies)
  .dependsOn(common)
