name := "scala2"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.jsuereth" %% "scala-arm" % "2.0", "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.6",
  "com.typesafe" % "config" % "1.3.1",
  "org.scala-lang.modules" %% "scala-java8-compat" % "0.+",
  "com.typesafe.akka" %% "akka-actor" % "2.+",
  "com.typesafe.akka" %% "akka-slf4j" % "2.+",
  "commons-daemon" % "commons-daemon" % "1.0.15",
  "org.slf4j" % "slf4j-api" % "1.7.+",
  "junit" % "junit" % "4.11" % "test"
)
