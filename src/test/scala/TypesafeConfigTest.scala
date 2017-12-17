import com.typesafe.config.{Config, ConfigFactory, ConfigObject}
import scala.collection.JavaConverters._

object TypesafeConfigTest {

  def main(args: Array[String]): Unit = {

    // 1) ConfigFactory.parseResources([ClassLoader], [filepath: String]):
    //    parse a resource file to a Config instance
    val config: Config = ConfigFactory.parseResources("typesafe.conf").resolve()
    // it is best to resolve an entire stack of fallbacks (such as all your config
    // files combined) rather than resolving each one individually

    // 2) Config.getConfig([path: String]):
    //    get the nested Config instance by the requested path
    val conf1: Config = config.getConfig("configuration1")
    println(conf1)
    // Config(SimpleConfigObject(
    //   {
    //     "field1":[{"text":"text1","type":"type1"},{"text":"text2","type":"type2"}],
    //     "field2":[{"text":"text3","type":"type3"}]
    //   }
    // )

    // 3) Config.root():
    //    get the ConfigObject
    val configObject: ConfigObject = conf1.root()
    println(configObject.keySet()) // [field1, field2]

    // 4) Config.getConfigList([path: String]):
    //    get a java.util.List of Config instances
    configObject.keySet().asScala.map(
      (field: String) => {
        val configList: List[Config] = conf1.getConfigList(field).asScala.toList
        println(configList)        // List(Config(SimpleConfigObject({"text":"text3","type":"type3"})))
      }
    )

    // 1) configBeanFactory.create[T]([Config], [Class[T]])
    //    this creates an instance of Class[T] with its fields initialed as the Config fields
    //ConfigBeanFactory.create[X](c, clazz)
  }
}
