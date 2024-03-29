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

lazy val root = project.in(file("."))
  .settings(commonSettings: _*)
  .dependsOn(yahoo_geo_search_engine)
  .dependsOn(yahoo_geo_scraping_engine)
  .dependsOn(yahoo_geo_crawler_engine)

lazy val yahoo_geo_search_engine = project.in(file("yahoo_geo_search_engine"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= gmlDependencies)
  .settings(libraryDependencies ++= baseDependencies)

lazy val yahoo_geo_scraping_engine = project.in(file("yahoo_geo_scraping_engine"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= gmlDependencies)
  .settings(libraryDependencies ++= baseDependencies)
  .settings(libraryDependencies ++= scalaScrapingDependencies)

lazy val yahoo_geo_crawler_engine = project.in(file("yahoo_geo_crawler_engine"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= gmlDependencies)
  .settings(libraryDependencies ++= baseDependencies)