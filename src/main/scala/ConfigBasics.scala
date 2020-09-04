import java.util.Map.Entry

import com.typesafe.config.{ConfigFactory, ConfigValue}

import scala.collection.JavaConverters._

object ConfigBasics extends App {
  val config = ConfigFactory.parseResources(getClass.getClassLoader, "application.conf").resolve
  // 1) configObject.keySet()
  //    ConfigObject implements java.util.Map<String, ConfigValue> so you can use it like a regular Java map
  //    Or call unwrapped() to unwrap the map to a map with plain Java values rather than ConfigValue
  val keys = config.getObject("envs.dev.setting").keySet().asScala.toList
  println(keys)         // List(uri, parameters, target)
  val parameters = config.getConfig("envs.dev.setting.parameters")
  println(parameters)   // Config(SimpleConfigObject({"overwrite":true,"permission":774}))
  // 2) config.entrySet() & configValue.unwrapped():
  //  entrySet() returns Set<Map.Entry<String, ConfigValue>>: the set of path-value pairs, excluding any null values
  val params = for {
    entry : Entry[String, ConfigValue] <- parameters.entrySet().asScala
  } yield (entry.getKey, entry.getValue.unwrapped())
  println(params.toMap) // Map(overwrite -> true, permission -> 774)
  // 3) configValue.origin().comments
  val levels = config.getConfig("high-level")
  config.getConfig("high-level").root().entrySet().asScala.foreach(x => println(x.getValue.origin().comments()))
  // [ level2 conmment line1]
  // [ level1 conmment line1,  level1 conmment line2]
  config.getConfig("high-level").root().entrySet().asScala.foreach(x => println(config.getConfig(s"high-level.${x.getKey}").getString("low-level")))
  // low-level value2
  // low-level value1
  config.getConfig("high-level").root().entrySet().asScala.foreach(x => println(config.getConfig(s"high-level.${x.getKey}")))
  config.getConfig("high-level").root().entrySet().asScala.foreach(x => println(x.getValue))
  config.getConfig("high-level").root().entrySet().asScala.foreach(x => println(x.getValue))
}
