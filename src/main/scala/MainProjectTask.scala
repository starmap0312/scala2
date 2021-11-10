package mainproject

import com.typesafe.config.ConfigFactory

object MainProjectTask extends App {
  println("I am a task in the main-project")
  val appConf = ConfigFactory.defaultApplication()
  println(appConf.origin()) // load application.conf file of the main project
  // application.conf @ jar:file:./target/bg-jobs/sbt_ebe7d265/job-1/target/7d5b6ad4/1230d1c5/scala2_2.13-1.0.jar!/application.conf
  //   i.e./target/scala-2.13/scala2_2.13-1.0.jar!/application.conf

  val refConf = ConfigFactory.defaultReference()

  println(refConf.origin()) // load all the reference.conf files in all dependencies & of the sub project
  // merge of:
  // system properties,
  // reference.conf @ jar:file:./target/bg-jobs/sbt_ebe7d265/target/2f61b134/f86ea5fa/subproject_2.13-0.1.0-SNAPSHOT.jar!/reference.conf: 1,
  //   i.e. ./subproject/target/scala-2.13/subproject_2.13-0.1.0-SNAPSHOT.jar
  // reference.conf @ jar:file:./target/bg-jobs/sbt_ebe7d265/target/b01ab46b/cf83d284/akka-actor_2.13-2.6.8.jar!/reference.conf: 1,
  // reference.conf @ jar:file:./target/bg-jobs/sbt_ebe7d265/target/9e345434/df259a03/akka-stream_2.13-2.6.8.jar!/reference.conf: 1,
  // reference.conf @ jar:file:./target/bg-jobs/sbt_ebe7d265/target/fd9091a4/f0167b47/akka-http_2.13-10.2.1.jar!/reference.conf: 1,
  // reference.conf @ jar:file:./target/bg-jobs/sbt_ebe7d265/target/e18acedb/02992f91/ssl-config-core_2.13-0.4.1.jar!/reference.conf: 1,
  // reference.conf @ jar:file:./target/bg-jobs/sbt_ebe7d265/target/de2d4bf0/59863fcd/akka-http-core_2.13-10.2.1.jar!/reference.conf: 1

  val config = appConf.withFallback(refConf)
  println(config.origin()) // load all the reference.conf files in all dependencies & subproject
  // merge of :
  // application.conf @ jar:file:./target/bg-jobs/sbt_f045a735/job-1/target/7d5b6ad4/61c7a7e1/scala2_2.13-1.0.jar!/application.conf: 1,
  // system properties,
  // reference.conf @ jar:file:./target/bg-jobs/sbt_f045a735/target/2f61b134/f86ea5fa/subproject_2.13-0.1.0-SNAPSHOT.jar!/reference.conf: 1,
  // reference.conf @ jar:file:./target/bg-jobs/sbt_f045a735/target/b01ab46b/cf83d284/akka-actor_2.13-2.6.8.jar!/reference.conf: 1,
  // reference.conf @ jar:file:./target/bg-jobs/sbt_f045a735/target/9e345434/df259a03/akka-stream_2.13-2.6.8.jar!/reference.conf: 1,
  // reference.conf @ jar:file:./target/bg-jobs/sbt_f045a735/target/fd9091a4/f0167b47/akka-http_2.13-10.2.1.jar!/reference.conf: 1,
  // reference.conf @ jar:file:./target/bg-jobs/sbt_f045a735/target/e18acedb/02992f91/ssl-config-core_2.13-0.4.1.jar!/reference.conf: 1,
  // reference.conf @ jar:file:./target/bg-jobs/sbt_f045a735/target/de2d4bf0/59863fcd/akka-http-core_2.13-10.2.1.jar!/reference.conf: 1

  println(config.getString("sub.conf")) // i am defined in subproject reference.conf
  println(config.getString("sub.replacement")) // i am a replacement defined in main project application.conf
  println(config.getString("sub.resolved")) // after resolved, replacement=i am a replacement defined in subproject reference.conf
  // note that, the replacement defined in main project application.conf is NOT used in sub.resolved!!!
  // this means that the resolve() in each reference.conf happens within themselves
  //   i.e. one can only override the subproject's config, and not to expect the unresolved config to be partially replaced
}
// run main project:
//   sbt run
// run a specific class in main project:
//   sbt "runMain mainproject.MainProjectTask"
