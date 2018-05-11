package basics

object ScalaOption extends App {
  def optionMethod(number: Int): Option[Int] = if (number % 2 == 0) Option(number) else None

  // 1) there is an implicit conversion from Some[T] to GenTraversableOnce[T]
  //    so we can List flatMap [function that returns Option] to get another List

  // List: flatMap([function that returns Option]) -> headOption() -> getOrElse()
  val num1 = List(1, 2, 3). // List(1, 2, 3)
    flatMap(optionMethod).  // List(2)
    headOption.    // return Some(first element) if nonEmpty; otherwise, return None
    getOrElse(10)  // return Option's value if nonEmpty; otherwise, return 10
  println(num1) // 2

  val num2 = List(1, 2, 3). // List(1, 2, 3)
    flatMap(_ => None).     // List()
    headOption.
    getOrElse(10)
  println(num2) // 10

}
