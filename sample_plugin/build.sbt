import CommonSettings._
import Dependencies._

lazy val test_algorithm_plugin = project.in(file("test_algorithm_plugin"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= gmlDependencies)