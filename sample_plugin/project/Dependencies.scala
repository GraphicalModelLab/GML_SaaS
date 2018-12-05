import CommonSettings._
import sbt._;

object Dependencies {

  /* Resolvers */
  val resolvers = DefaultOptions.resolvers(snapshot = true) ++ Seq(
    "scalaz-releases" at "http://dl.bintray.com/scalaz/releases",
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
  )
  // Graphical Model Lab API
  val graphicalmodellab_api = "org.graphicalmodellab" %% "gml_api" % "0.1-SNAPSHOT"

  val gmlDependencies = Seq(
    graphicalmodellab_api
  )
}