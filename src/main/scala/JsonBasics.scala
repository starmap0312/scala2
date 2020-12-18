import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.core.{JsonFactory, JsonParser, JsonToken}
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

object JsonBasics extends App {
  val jsonFactory = new JsonFactory()
  val mapper = new ObjectMapper with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
  mapper.setSerializationInclusion(Include.NON_ABSENT)

  val jsonString =
    """
      |{
      |  "key1": "value1",
      |  "key2": ["arrValue1", "arrValue2"],
      |  "key3": {
      |    "key3_1": "objValue1",
      |    "key3_2": "objValue2"
      |  }
      |}
    """.stripMargin

  val jsonParser: JsonParser = jsonFactory.createParser(jsonString.getBytes)
  println(jsonParser.currentToken()) // null

  println(jsonParser.nextToken()) // JsonToken.START_OBJECT: JsonToken(token = "{", id = 1)
    println(jsonParser.nextToken()) // JsonToken.FIELD_NAME: JsonToken(token = null, id = 5)
      println(jsonParser.nextToken()) // JsonToken.VALUE_STRING: JsonToken(token = null, id = 6)
    println(jsonParser.nextToken()) // JsonToken.FIELD_NAME: JsonToken(token = null, id = 5)
      println(jsonParser.nextToken()) // JsonToken.START_ARRAY: JsonToken(token = "[", id = 3)
        println(jsonParser.nextToken()) // JsonToken.VALUE_STRING: JsonToken(token = null, id = 6)
        println(jsonParser.nextToken()) // JsonToken.VALUE_STRING: JsonToken(token = null, id = 6)
      println(jsonParser.nextToken()) // JsonToken.END_ARRAY: JsonToken(token = null, id = 5)
    println(jsonParser.nextToken()) // JsonToken.FIELD_NAME: JsonToken(token = null, id = 5)
      println(jsonParser.nextToken()) // JsonToken.START_OBJECT: JsonToken(token = "{", id = 1)
        println(jsonParser.nextToken()) // JsonToken.FIELD_NAME: JsonToken(token = null, id = 5)
          println(jsonParser.nextToken()) // JsonToken.VALUE_STRING: JsonToken(token = null, id = 6)
        println(jsonParser.nextToken()) // JsonToken.FIELD_NAME: JsonToken(token = null, id = 5)
          println(jsonParser.nextToken()) // JsonToken.VALUE_STRING: JsonToken(token = null, id = 6)
      println(jsonParser.nextToken()) // JsonToken.END_OBJECT: JsonToken(token = "}", id = 2)
  println(jsonParser.nextToken()) // JsonToken.END_OBJECT: JsonToken(token = "}", id = 2)

  println(jsonParser.nextToken()) // null
  jsonParser.close()


  def parse[X](arr: Array[Byte])(fn: JsonParser => X) = {
    val jsonParser: JsonParser = jsonFactory.createParser(arr)
    try {
      fn(jsonParser)
    } finally {
      jsonParser.close()
    }
  }


  val skip: PartialFunction[(String, JsonParser), Unit] = {
    case (_, p) =>
      if (p.currentToken() == JsonToken.FIELD_NAME) p.nextValue()
      p.skipChildren()
  }

  def field[X](jsonParser: JsonParser)(fn: PartialFunction[(String, JsonParser), Unit]) = {
    var fieldName = jsonParser.nextFieldName()
    while (fieldName != null) {
      (fn orElse skip)(fieldName, jsonParser)
      fieldName = jsonParser.nextFieldName()
    }
  }

  def obj[X](jsonParser: JsonParser)(fn: PartialFunction[(String, JsonParser), Unit]) = {
    if (jsonParser.currentToken() != JsonToken.START_OBJECT) while (jsonParser.nextToken() != JsonToken.START_OBJECT && jsonParser.currentToken() != null) {}
    field(jsonParser)(fn)
    while (jsonParser.currentToken() != JsonToken.END_OBJECT && jsonParser.currentToken() != null && jsonParser.nextToken() != JsonToken.END_OBJECT) {}
  }

  def array[X: Manifest](jsonParser: JsonParser)(fn: JsonParser => X) = {
    var arrayBuilder = Array.newBuilder[X]
    while (jsonParser.nextToken() != JsonToken.START_ARRAY && jsonParser.currentToken() != null) {}
    while (jsonParser.nextToken() != JsonToken.END_ARRAY && jsonParser.currentToken() != null) {
      arrayBuilder.+=(fn(jsonParser))
    }
    arrayBuilder.result()
  }
}
