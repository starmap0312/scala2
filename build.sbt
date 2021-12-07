name := "scala2"

import sbt.nio.file.FileTreeView

version := "1.0"
scalaVersion := "2.13.6"

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

// sbt Multi-project builds 
val subProject = (project in file("subproject"))
// by running sbt universal:packageBin, it will create: subproject/target/scala-2.12/subproject_2.12-0.2.jar
val root = (project in file(".")).dependsOn(subProject) // mainProject
  .enablePlugins(ParadoxPlugin) // requires: the sbt-paradox plugin in ./project/plugin.sbt
  .settings(
    name := "Project scala2",
    paradoxTheme := Some(builtinParadoxTheme("generic"))
  )
// it will include subproject.subproject-0.2.jar
// so you can use any classes/objects/packages defined in the subproject in your main project

// SBE: simple binary encoding
val sbe = TaskKey[Seq[File]]("sbe")
Compile / sbe := {
  import uk.co.real_logic.sbe.SbeTool
  val main: File = baseDirectory.value / "src" / "main"
  val files: Seq[String] = (( main/ "sbe") ** "*.xml").get.map(_.getAbsolutePath).toList
  val out: File = (Compile/managedSourceDirectories).value.head
  println(s"Generate sbe java source files to: ${out}") // Sbe output source files: /Users/kuanyu/github/scala2/target/scala-2.13/src_managed/main
  sbt.IO.delete(out)
  out.mkdirs()
  System.setProperty("sbe.output.dir", out.getAbsolutePath)
  System.setProperty("sbe.java.generate.interfaces", "true")
  println(s"Run with ${files.mkString(" ")}") // Run with /Users/kuanyu/github/scala2/src/main/sbe/example-schema.xml
  SbeTool.main(files.toArray)

  FileTreeView.default.list(out.toGlob / ** / "*.java").map(_._1.toFile)
}

Compile / sourceGenerators  += (Compile / sbe).taskValue // required for Compile / sbe to run
// ex. run "sbt compile" to generate the sbe java source files

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

// Run a single unit test:
// sbt "testOnly BasicsTest"