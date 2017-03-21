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
  }
}
