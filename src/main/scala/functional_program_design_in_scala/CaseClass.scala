package functional_program_design_in_scala

trait Json
case class  JsonList(elements: List[Json])       extends Json
case class  JsonMap(mappings: Map[String, Json]) extends Json
case class  JsonNumber(number: Int)              extends Json
case class  JsonString(str: String)              extends Json
case object JsonNull                             extends Json

object Week1 extends App {
  // 1) case class:
  // example: representing the following Json object:
  // { "firstname": "john",
  //   "lastname" : "nash",
  //   "address"  : {
  //     "street": "Xin Rd",
  //     "post"  : 500
  //   },
  //   "phone": [
  //     5551234,
  //     1234567
  //   ]
  // }
  val json = JsonMap(
    Map(
      "firstname" -> JsonString("john"),
      "lastname"  -> JsonString("nash"),
      "address"   -> JsonMap(
        Map(
          "street" -> JsonString("Xin Rd"),
          "post"   -> JsonNumber(500)
        )
      ),
      "phone" -> JsonList(
        List(
          JsonNumber(5551234),
          JsonNumber(1234567)
        )
      )
    )
  )
  // 2) pattern matching: handle all subtypes of Json in one place
  def show(json: Json): String = json match {
    case JsonList(elements) =>
      "[" +
        elements.map( // map: e => show(e)
          show(_)
        ).mkString(", ") +
      "]"
    case JsonMap(mappings) =>
      "{" +
      mappings.map(   // map: (key, value) => "key": show(value)
        {
          case (key, value) => s""""${key}" : ${show(value)}"""
        }
      ).mkString(", ") +
      "}"
    case JsonNumber(number) => s""""${number.toString}""""
    case JsonString(str) => s""""${str}""""
    case JsonNull => "null"
  }
  println(show(json))
  //{"firstname" : "john", "lastname" : "nash", "address" : {"street" : "Xin Rd", "post" : "500"}, "phone" : ["5551234", "1234567"]}

  // 2) trait Function1[-T, +R]:
  // 2.1) trait PartialFunction[-T, +R] extends (T => R)
  val tf: Function1[Int, String] = { case 1 => "one" case 2 => "two"}
  println(tf(2))   // two
  //println(tf(3)) // throw scala.MatchError exception
  val pf: PartialFunction[Int, String] = { case 1 => "one" case 2 => "two"}
  if (pf.isDefinedAt(3)) {
    println(pf(3)) // unreachable
  }
  // 2.1) Map[K, V] extends (K => V)
  //      scala Map is a subtype of Function1[K, V]
  println(Map(1 -> "one", 2 -> "two")(2)) // two
  // 2.2) Seq[T] extends (Int => T):
  //      scala Seq is a subtype of Function1[Int, T]
  println(Seq(2, 4, 6)(1)) // 4
}
