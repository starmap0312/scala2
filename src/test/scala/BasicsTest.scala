
object BasicsTest {
  def main(args: Array[String]): Unit = {
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

  }
}
