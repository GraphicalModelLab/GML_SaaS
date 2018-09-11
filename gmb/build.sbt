import CommonSettings._
import Dependencies._

lazy val common = project.in(file("common"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= backendDependencies)
  .settings(libraryDependencies ++= googleApiDependencies)
  .settings(libraryDependencies ++= elasticSearchDependencies)

lazy val auth = project.in(file("auth"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= playDependencies)
  .settings(libraryDependencies ++= backendDependencies)
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


lazy val gml = project.in(file("gml"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= playDependencies)
  .settings(libraryDependencies ++= backendDependencies)
//  .settings(libraryDependencies ++= dl4jDependencies)
  .settings(libraryDependencies ++= sparkDependencies)
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


lazy val gml_api = project.in(file("gml_api"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= playDependencies)
