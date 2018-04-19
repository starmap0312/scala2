name := "scala2"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.jsuereth" %% "scala-arm" % "2.0", "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.6",
  "com.typesafe" % "config" % "1.3.1",
  "org.scala-lang.modules" %% "scala-java8-compat" % "0.+",
  "com.typesafe.akka" %% "akka-actor" % "2.+",
  "com.typesafe.akka" %% "akka-slf4j" % "2.+",
  "com.typesafe.akka" %% "akka-http"   % "10.1.1",
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.1", // akka JSON marshaller Support
  "commons-daemon" % "commons-daemon" % "1.0.15",
  "org.slf4j" % "slf4j-api" % "1.7.+",
  "junit" % "junit" % "4.11" % "test"
)

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