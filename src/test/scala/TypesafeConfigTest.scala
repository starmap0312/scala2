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

    // 5) ConfigBeanFactory.create[T]([Config], [Class[T]]):
    //    this creates an instance of a class, initializing its fields from a Config
    val conf2 = config.getConfigList("configuration2.field2").asScala.toList.head
    println(conf2)            // Config(SimpleConfigObject({"list":["list2-1","list2-2"],"type":"type4"}))
    val confList = ConfigBeanFactory.create[MyConfigList](conf2, classOf[MyConfigList])
    println(confList.getType) // type4
    println(confList.getList) // [list2-1, list2-2]
  }
}
