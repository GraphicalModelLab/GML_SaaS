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
  .dependsOn(multivariate_guassian_algorithm_plugin)
  .dependsOn(kernel_density_algorithm_plugin)

lazy val multivariate_guassian_algorithm_plugin = project.in(file("multivariate_guassian_algorithm_plugin"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= gmlDependencies)
  .settings(libraryDependencies ++= baseDependencies)

lazy val kernel_density_algorithm_plugin = project.in(file("kernel_density_algorithm_plugin"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= gmlDependencies)
  .settings(libraryDependencies ++= baseDependencies)
