name := "scala2"

version := "1.0"
scalaVersion := "2.13.1"

scalacOptions ++= Seq("-language:postfixOps")

val akkaVersion = "2.6.8"
val akkaHttpVersion = "10.2.1"

libraryDependencies ++= Seq(
//  "com.jsuereth" %% "scala-arm" % "2.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.10.1",
  "com.typesafe" % "config" % "1.3.1",
  "org.scala-lang.modules" %% "scala-java8-compat" % "0.+",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion, // akka JSON marshaller Support
  "commons-daemon" % "commons-daemon" % "1.0.15",
  "org.apache.httpcomponents" % "httpasyncclient" % "4.1.3",
  "com.github.blemale" %% "scaffeine" % "3.1.0",
  "org.jsoup" % "jsoup" % "1.8.3",
  "uk.co.real-logic" % "sbe-all" % "1.24.0", // Simple Binary Encoding (SBE)
  "org.slf4j" % "slf4j-api" % "1.7.+",
  "junit" % "junit" % "4.11" % Test
)

lazy val root = (project in file("."))

// Plugins
// 1) sbt native packaging
//    https://www.scala-sbt.org/sbt-native-packager/archetypes/java_app/index.html
//    Application packaging focuses on:
//      how your application is launched (via a bash)
//      how dependencies are managed
//      how configuration and other auxiliary files are included in the final distributable
//    The JavaAppPackaging archetype provides a default application structure and executable scripts to launch your application
// 1.2) Java Server Application Archetype
//      https://www.scala-sbt.org/sbt-native-packager/archetypes/java_server/index.html
//      Java Server Application Archetype
//        it provides platform-specific functionality for installing your application in server environments
//        the server archetype adds additional features you may need when running your application as a service on a server
//        SBT Native Packager ships with a set of predefined install and uninstall scripts for various platforms and service managers