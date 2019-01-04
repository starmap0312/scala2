import com.typesafe.config.{Config, ConfigFactory, ConfigObject, ConfigBeanFactory}
import scala.collection.JavaConverters._

class MyConfigList {
  var typ: String = _
  var list: java.util.List[String] = _

  def getType = this.typ
  def setType(t: String) = this.typ = t

  def getList = this.list
  def setList(l: java.util.List[String]) = this.list = l
}

case class MyConfigClass(val name: String)

object TypesafeConfigTest {

  def main(args: Array[String]): Unit = {

    // 1) ConfigFactory.parseResources([ClassLoader], [filepath: String]):
    //    load a TypeSafe config from a source, i.e. parse a resource file to a Config instance
    val config: Config = ConfigFactory.parseResources("typesafe.conf").resolve()
    // Config.resolve():
    //   it returns an immutable object with substitutions resolved
    //   i.e. reuse a config value inside of other config values

    // 2) Config.getConfig([path: String]):
    //    get the nested Config instance by the requested path
    val conf1: Config = config.getConfig("configuration1")
    println(conf1)
    // Config(
    //   SimpleConfigObject(
    //     {
    //       "field1":[
    //         {"text":"text1","type":"type1"},
    //         {"list":["list1-1","list1-2"],"type":"type2"}
    //       ],
    //       "field2":[
    //         {"text":"text2","type":"type3"}
    //       ]
    //     }
    //   )
    // )


    // 3) Config.getConfigList([path: String]): returns a java.util.List[Config]
    //    Config.getString([path: String]): returns a String
    println(config.getConfigList("configuration1.field2").asScala.toList.head.getString("text")) // text2

    // 4) Config.root():
    //    get the ConfigObject
    val configObject: ConfigObject = conf1.root()
    println(configObject.keySet()) // [field1, field2]
    configObject.keySet().asScala.map(
      (field: String) => {
        val configList: List[Config] = conf1.getConfigList(field).asScala.toList
        println(configList)
      }
    ) // List(Config(SimpleConfigObject({"text":"text1","type":"type1"})), Config(SimpleConfigObject({"list":["list1-1","list1-2"],"type":"type2"})))
      // List(Config(SimpleConfigObject({"text":"text2","type":"type3"})))

    // 4.2) config reuse & override
    println(config.getConfig("configuration1")) // Config(SimpleConfigObject({"field1":[{"text":"text1","type":"type1"},{"list":["list1-1","list1-2"],"type":"type2"}],"field2":[{"text":"text2","type":"type3"}]}))
    println(config.getConfig("configuration2")) // Config(SimpleConfigObject({"field1":[{"text":"text1","type":"type1"},{"list":["list1-1","list1-2"],"type":"type2"}],"field2":[{"text":"text2","type":"type1"}]}))
    println(config.getConfig("configuration3")) // Config(SimpleConfigObject({"field1":[{"text":"text1","type":"type1"},{"list":["list1-1","list1-2"],"type":"type2"}],"field2":[{"text":"override","type":"type3"}]}))
    println(config.getConfig("configuration4")) // Config(SimpleConfigObject({}))

    // 5) this.getClass.getClassLoader.loadClass([class name]): returns a runtime Class instance, which can be used to instantiate at runtime
    val class_conf: String = config.getString("class_configuration.class")
    val runtimeClass: Class[_] = this.getClass.getClassLoader.loadClass(class_conf)
    println(runtimeClass)  // class MyConfigClass
    val instance: MyConfigClass = runtimeClass.getConstructor(classOf[String]).newInstance("my class name").asInstanceOf[MyConfigClass]
    println(instance.name) // my class name


  }
}
