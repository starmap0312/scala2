import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

class MapClass2() {
  val num: Int = 0
  val str: String = "default"
  val list: List[String] = List()
}

object ConversionTest {
    def main(args: Array[String]): Unit = {
      // 1) convertValue[T: Manifest](fromValue: Any): T
      //    ex. convertValue[Map[String, Any]]
      //        convert a Java/Scala Object into a Map[String, Any]
      val objectMapper = new ObjectMapper with ScalaObjectMapper
      objectMapper.registerModule(DefaultScalaModule)
      objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      val mapObj1 = new MapClass1
      val mapObj2 = new MapClass2
      val map1 = objectMapper.convertValue[Map[String, Any]](mapObj1)
      val map2 = objectMapper.convertValue[Map[String, Any]](mapObj2)
      println(map1) // Map(num -> 0, str -> default, list -> List())
      println(map2) // Map(num -> 0, str -> default, list -> List())
    }
}
