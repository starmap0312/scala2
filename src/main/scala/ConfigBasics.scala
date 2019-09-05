import java.util.Map.Entry

import com.typesafe.config.{ConfigFactory, ConfigValue}

import scala.collection.JavaConverters._

object ConfigBasics extends App {
  val config = ConfigFactory.parseResources(getClass.getClassLoader, "application.conf").resolve
  // 1) configObject.keySet()
  val keys = config.getObject("envs.dev.setting").keySet().asScala.toList
  println(keys)         // List(uri, parameters, target)
  val parameters = config.getConfig("envs.dev.setting.parameters")
  println(parameters)   // Config(SimpleConfigObject({"overwrite":true,"permission":774}))
  // 2) config.entrySet() & configValue.unwrapped():
  val params = for {
    entry : Entry[String, ConfigValue] <- parameters.entrySet().asScala
  } yield (entry.getKey, entry.getValue.unwrapped())
  println(params.toMap) // Map(overwrite -> true, permission -> 774)
  // 3) configValue.origin().comments
  val levels = config.getConfig("high-level")
  config.getConfig("high-level").root().entrySet().asScala.foreach(x => println(x.getValue.origin().comments()))
}
