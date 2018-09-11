import CommonSettings._
import Dependencies._

lazy val multivariateguassian = project.in(file("multivariateguassian"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= playDependencies)
  .settings(libraryDependencies ++= gmlDependencies)
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
