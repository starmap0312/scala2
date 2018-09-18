import com.fasterxml.jackson.databind.{DeserializationFeature, JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

import scala.collection.mutable

class MapClass2() {
  val num: Int = 0
  val str: String = "default"
  val list: List[String] = List()
}

object JacksonConvertObjectToMapTest {
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

      // 2) Convert between Mutable and Immutable
      // 2.1) from mutable to immutable, use toXXX series methods:
      //      ex. mList.toList, mMap.toMap
      val mMap1 = mutable.Map("one" -> 1, "two" -> 2)
      mMap1.put("three", 3)
      println(mMap1) // Map(one -> 1, three -> 3, two -> 2)
      val imMap = mMap1.toMap
      //imMap.put("four", 4) // unsupported operation
      // 2.2) from immutable to mutable: use mutable.Map.apply() method
      val mMap2 = mutable.Map(imMap.toSeq: _*)
      mMap2.put("four", 4)
      println(mMap2) // Map(one -> 1, three -> 3, four -> 4, two -> 2)

      // 3) ObjectMapper.readValue([json string]): Convert Json string into Map[String, Any]
      val jsonString: String =
        """
          |{
          |  "key1": {
          |    "key21": "value21",
          |    "key22": 22,
          |    "key23": {
          |      "key31": {
          |        "key41": 41,
          |        "key42": "2018-09-17T03:31:02Z"
          |      }
          |    }
          |  }
          |}
        """.stripMargin
      val mMap3: Map[String, Any] = objectMapper.readValue[Map[String, Any]](jsonString)
      println(mMap3) // Map(key1 -> Map(key21 -> value21, key22 -> 22, key23 -> Map(key31 -> Map(key41 -> 41, key42 -> 2018-09-17T03:31:02Z))))
      println(mMap3.get("key1").get.asInstanceOf[Map[String, Any]].get("key21").get.asInstanceOf[String]) // value21

      // 4) ObjectMapper.readTree([json tree]): Convert Json tree into JsonNode
      val jsonNode: JsonNode = objectMapper.readTree(jsonString)
      println(jsonNode) // {"key1":{"key21":"value21","key22":22,"key23":{"key31":{"key41":41,"key42":"2018-09-17T03:31:02Z"}}}}
      println(jsonNode.get("key1").get("key21")) // value21

    }
}
