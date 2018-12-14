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
import sbt._;

object Dependencies {

  /* Resolvers */
  val resolvers = DefaultOptions.resolvers(snapshot = true) ++ Seq(
    "scalaz-releases" at "http://dl.bintray.com/scalaz/releases",
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
  )
  // basic libs
  val jettison =  "org.codehaus.jettison" % "jettison" % "1.3.7"
  val scalaj = "org.scalaj" %% "scalaj-http" % "1.1.5"
  val netty = "io.netty" % "netty-transport-native-epoll" % "4.1.3.Final" classifier "linux-x86_64"

  val baseDependencies = Seq(
    jettison,
    scalaj,
    netty
  )

  // Graphical Model Lab API
  val graphicalmodellab_api = "org.graphicalmodellab" %% "gml_api" % "0.1-SNAPSHOT"

  val gmlDependencies = Seq(
    graphicalmodellab_api
  )


}