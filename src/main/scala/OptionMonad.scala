// trait Option[T] {
//     def isDefined: Boolean
//     def get: T
//     def getOrElse(t: T): T
// }
object OptionMonad {
  def main(args: Array[String]): Unit = {
    // example: Map.get uses Option for its return typ
    val numbers = Map("one" -> 1, "two" -> 2)
    // 1) get() method:
    println(numbers.get("two").get)            // 2
    //println(numbers.get("three").get)        // runtime NoSuchElementException
    // 2) getOrElse([T]) method:
    println(numbers.get("three").getOrElse(3)) // 3
    // 3) isDefined() method:
    val result = numbers.get("three")
    val value = if (result.isDefined) {
      result.get * 2
    } else {
      3
    }
    println(value)                             // 3
    // 4) flatMap[B](f: A => Option[B]): Option[B] = if (isEmpty) None else f(this.get)
    println(                                   // Some(4)
      Option(3).flatMap(
        {(x: Int) => Option(x + 1)}
      )
    )
    println(                                   // None
      None.flatMap(
        {(x: Int) => Option(x + 1)}
      )
    )    // 5) def map[B](f: A => B): Option[B] = if (isEmpty) None else Some(f(this.get))
    println(                                   // Some(4)
      Option(3).map(
        {(x: Int) => (x + 1)}
      )
    )
    println(                                   // None
      None.map(
        {(x: Int) => (x + 1)}
      )
    )
    // 6) def filter(p: A => Boolean): Option[A] = if (isEmpty || p(this.get)) this else None
    println(                                   // Some(4)
      Option(4).filter(
        _ % 2 == 0
      )
    )
    println(                                   // None
      Option(4).filter(
        _ % 2 != 0
      )
    )
    // 7) def orElse[B >: A](alternative: => Option[B]): Option[B] = if (isEmpty) alternative else this
    println(                                   // Some(4)
      None.orElse(
        Option(4)
      )
    )
  }
}
