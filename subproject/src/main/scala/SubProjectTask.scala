package subproject

import com.typesafe.config.ConfigFactory
//import scala.collection.JavaConverters._

object SubProjectTask extends App {
  println("i am a task in the sub-project")
  val config = ConfigFactory.defaultApplication() // application.conf
    .withFallback(ConfigFactory.defaultReference()) // reference.conf
    .resolve()
  println(ConfigFactory.defaultApplication().origin())
  // application.conf @ jar:file:./target/bg-jobs/sbt_225a8eb3/job-1/target/7d5b6ad4/367a8d3c/scala2_2.13-1.0.jar!/application.conf
  println(ConfigFactory.defaultReference().origin())
  // merge of system properties,
  // reference.conf @ jar:file:./target/bg-jobs/sbt_225a8eb3/target/2f61b134/2b4db539/subproject_2.13-0.1.0-SNAPSHOT.jar!/reference.conf: 1,
  // reference.conf @ jar:file:./target/bg-jobs/sbt_225a8eb3/target/b01ab46b/cf83d284/akka-actor_2.13-2.6.8.jar!/reference.conf: 1,
  // reference.conf @ jar:file:./target/bg-jobs/sbt_225a8eb3/target/9e345434/df259a03/akka-stream_2.13-2.6.8.jar!/reference.conf: 1,
  // reference.conf @ jar:file:./target/bg-jobs/sbt_225a8eb3/target/fd9091a4/f0167b47/akka-http_2.13-10.2.1.jar!/reference.conf: 1,
  // reference.conf @ jar:file:./bg-jobs/sbt_225a8eb3/target/e18acedb/02992f91/ssl-config-core_2.13-0.4.1.jar!/reference.conf: 1,
  // reference.conf @ jar:file:./target/bg-jobs/sbt_225a8eb3/target/de2d4bf0/59863fcd/akka-http-core_2.13-10.2.1.jar!/reference.conf: 1


  println(config.getString("sub.conf")) // i am defined in subproject reference.conf
  println(config.getString("sub.replacement")) // i am a replacement defined in main project application.conf
  // note that, the replacement defined in main project application.conf is NOT used in sub.resolved!!!
  println(config.getString("sub.resolved")) // after resolved, replacement=i am a replacement defined in subproject reference.conf
}
// run subproject:
//   sbt "project subProject" run
// run a specific class in subproject:
//   sbt "runMain subproject.SubProjectTask"