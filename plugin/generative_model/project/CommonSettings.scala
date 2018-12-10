import sbt.Keys._
import sbt._

object CommonSettings {

  val commonSettings = Seq(
    organization := "org.graphicalmodel",
    scalaVersion := "2.11.8",
    resolvers ++= Dependencies.resolvers,
    fork in Test := true,
    parallelExecution in Test := true
  )

  val playVersion = "2.3.6"
}
