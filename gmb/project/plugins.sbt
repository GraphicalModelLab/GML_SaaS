resolvers ++= DefaultOptions.resolvers(snapshot = true)
resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.6")

//Idea plugin
addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.2")

// Setup sbteclipse
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.3")


