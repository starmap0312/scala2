
object BasicsTest {
  def main(args: Array[String]): Unit = {
    // 0) AnyVal vs. Any vs. AnyRef
    // 0.1) AnyVal: Java primitives
    // 0.2) AnyRef: java.lang.Object
    // 0.3) Any: java.lang.Object and Java primitives
    //      in Scala, all objects are descendant from Any
    //      there is no equivalent in Java because there is no such unification in Java

    // 1) SeqLike operations:
    // 1.1) ::
    //      adds an element at the beginning of this list
    val list: List[String] = List("B")  // B :: Nil
    println("A" :: list)  // List(A, B)
    // the above is equavelient to:
    println(list.::("A")) // List(A, B)
    // 1.2) +:
    //      adds an element at the beginning of this SeqLike
    println("A" +: list)  // List(A, B)
    // similar to ::, but +: can be used in pattern matching but :: cannot
    list match {
      case "B" +: Nil => println("matched")
      case _ => println("no match")
    }                     // matched
    // 1.3) ++
    //      concatenate two lists
    println(List("A") ++ list)  // List(A, B)
    // 1.4) :::
    //      concatenate two lists
    println(List("A") ::: list) // List(A, B)

    // 2) multi-line string:
    //    For every line in this string:
    //    strip a leading prefix consisting of blanks or control characters followed by | from the line
    val multilineStr =
    s"""line1
       |line2
       |line3""".stripMargin // the space| prefix will be removed from each line
    println(multilineStr) // line1 line2 line3

    // 3) unpack an Array
    //    because Array has unapplySeq() defined, so we can treat it as an extractor to unpack a IndexedSeq
    val Array(x1, x2, x3) = Array(1, "two", 3)
    println(x1) // 1
    println(x2) // two
    println(x3) // 3

    // 4) Map[K, V]
    //    map.keys(): returns Iterable[K]
    //    map.values(): returns Iterable[V]
    val map = Map(1 -> "one", 2 -> "two")
    println(map.keys)   // set(1,2)
    println(map.values) // MapLike.DefaultValuesIterable(one, two)
  }
}
