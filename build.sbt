name := "scala2"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.jsuereth" %% "scala-arm" % "2.0", "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.6",
  "com.typesafe" % "config" % "1.2.1",
  "junit" % "junit" % "4.11" % "test"
)
