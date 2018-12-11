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