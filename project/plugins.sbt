logLevel := Level.Warn

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.10.0-RC1")

libraryDependencies += "uk.co.real-logic" % "sbe-all" % "1.24.0" // required for build.sbt to "import uk.co.real_logic.sbe.SbeTool"