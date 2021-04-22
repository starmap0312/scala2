import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.core.{JsonFactory, JsonParser, JsonToken}
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

import scala.reflect.ClassTag

object JsonBasics extends App {
  val jsonFactory = new JsonFactory()
  val mapper = new ObjectMapper with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
  mapper.setSerializationInclusion(Include.NON_ABSENT)

  val jsonString =
    """
      |{
      |  "field_name1": "value_string1",
      |  "field_name2": ["value_string2_1", "value_string2_2"],
      |  "field_name3": {
      |    "field_name3_1": "value_string3_1",
      |    "field_name3_2": "value_string3_2"
      |  },
      |  "no_parse_field_name": "no_parse_value"
      |}
    """.stripMargin

  // 1) JsonFactory.createParser([input string]):
  //    create a parser for an input json string
  val jsonParser: JsonParser = jsonFactory.createParser(jsonString.getBytes)
  println(jsonParser.currentToken()) // null
  // JsonParser.nextToken()
  println(jsonParser.nextToken()) // JsonToken.START_OBJECT: JsonToken(token = "{", id = 1)
    println(jsonParser.nextFieldName()) // field_name1
      println(jsonParser.nextTextValue()) // value_string1
    println(jsonParser.nextToken()) // JsonToken.FIELD_NAME: JsonToken(token = null, id = 5)
      println(jsonParser.nextToken()) // JsonToken.START_ARRAY: JsonToken(token = "[", id = 3)
        println(jsonParser.nextTextValue()) // value_string2_1
        println(jsonParser.nextTextValue()) // value_string2_2
      println(jsonParser.nextToken()) // JsonToken.END_ARRAY: JsonToken(token = null, id = 5)
    println(jsonParser.nextFieldName()) // field_name3
      println(jsonParser.nextToken()) // JsonToken.START_OBJECT: JsonToken(token = "{", id = 1)
        println(jsonParser.nextFieldName()) // field_name3_1
          println(jsonParser.nextTextValue()) // value_string3_1
        println(jsonParser.nextFieldName()) // field_name3_2
          println(jsonParser.nextTextValue()) // value_string3_2
      println(jsonParser.nextToken()) // JsonToken.END_OBJECT: JsonToken(token = "}", id = 2)
    println(jsonParser.nextToken()) // JsonToken.FIELD_NAME: JsonToken(token = null, id = 5)
      println(jsonParser.nextToken()) // JsonToken.VALUE_STRING
  println(jsonParser.nextToken()) // JsonToken.END_OBJECT
  println(jsonParser.nextToken()) // null
  jsonParser.close()

  println

  // jackson wrapper for stream-like processing of json payloads

  def parse[T: ClassTag](arr: Array[Byte])(fn: JsonParser => T) = {
    val jsonParser: JsonParser = jsonFactory.createParser(arr)
    try {
      fn(jsonParser)
    } finally {
      jsonParser.close()
    }
  }

  def skip: PartialFunction[(String, JsonParser), Unit] = {
    case (_, p) =>
      if (p.currentToken() == JsonToken.FIELD_NAME) p.nextValue() // consume nextValue
      p.skipChildren()
  }

  def obj(jsonParser: JsonParser)(fn: PartialFunction[(String, JsonParser), Unit]) = {
    if (jsonParser.currentToken() != JsonToken.START_OBJECT) while (jsonParser.nextToken() != JsonToken.START_OBJECT && jsonParser.currentToken() != null) {}
    var fieldName = jsonParser.nextFieldName()
    while (fieldName != null) { // continue to read a fieldName if not null
      (fn orElse skip)(fieldName, jsonParser) // apply fn to the parser
      fieldName = jsonParser.nextFieldName()
    }
    while (jsonParser.currentToken() != JsonToken.END_OBJECT && jsonParser.currentToken() != null && jsonParser.nextToken() != JsonToken.END_OBJECT) {}
  }

  def array[T: ClassTag](jsonParser: JsonParser)(fn: JsonParser => T) = {
    var arrayBuilder = Array.newBuilder[T]
    while (jsonParser.nextToken() != JsonToken.START_ARRAY && jsonParser.currentToken() != null) {}
    while (jsonParser.nextToken() != JsonToken.END_ARRAY && jsonParser.currentToken() != null) {
      arrayBuilder.+=(fn(jsonParser))
    }
    arrayBuilder.result()
  }

  parse(jsonString.getBytes()) { parser =>
    obj(parser) {
      case ("field_name1", p) =>
        println(p.nextTextValue()) // value_string1
      case ("field_name2", p) =>
        val arr: Array[String] = array(p) {
          _.getValueAsString
        }
        println(arr.mkString(",")) // value_string2_1, value_string2_2
      case ("field_name3", p) =>
        obj(p) {
          case ("field_name3_1", x) =>
            println(x.nextTextValue) // value_string3_1
          case ("field_name3_2", x) =>
            println(x.nextTextValue) // value_string3_2
        }
      case ("unknown_field_name", p) =>
        println(p.nextTextValue()) // skipped
    }
  }

  println

  parse(jsonString.getBytes()) {
    obj(_) {
      case ("field_name1", p) =>
        println(p.nextTextValue()) // value_string1
      case ("field_name2", p) =>
        p.nextValue()
        println(mapper.readValue[Seq[String]](p)) // List(value_string2_1, value_string2_2)
      case ("field_name3", p) =>
        p.nextValue()
        println(mapper.readValue[Map[String, String]](p)) // Map(field_name3_1 -> value_string3_1, field_name3_2 -> value_string3_2)
      case ("unknown_field_name", p) =>
        println(p.nextTextValue()) // skipped
    }
  }

  println

  val tree = mapper.readTree(jsonString)
  println(tree.findValue("field_name1").asInt()) // 0
  println(tree.findValue("field_name1").asText()) // value_string1
  println(tree.findValue("unknown_field_name")) // null

}
