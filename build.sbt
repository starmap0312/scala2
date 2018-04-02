name := "scala2"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.jsuereth" %% "scala-arm" % "2.0", "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.6",
  "com.typesafe" % "config" % "1.3.1",
  "junit" % "junit" % "4.11" % "test",
  "org.scala-lang.modules" %% "scala-java8-compat" % "0.+",
  "com.typesafe.akka" %% "akka-actor" % "2.+"
)
