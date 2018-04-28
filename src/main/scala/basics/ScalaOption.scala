package basics

object ScalaOption extends App {
  def optionMethod(number: Int): Option[Int] = if (number % 2 == 0) Option(number) else None

  // List: flatMap([function that returns Option]) -> headOption() -> getOrElse()
  val num1 = List(1, 2, 3). // List(1, 2, 3)
    flatMap(optionMethod).  // List(2)
    headOption.
    getOrElse(10)
  val num2 = List(1, 2, 3). // List(1, 2, 3)
    flatMap(_ => None).     // List()
    headOption.
    getOrElse(10)
  println(num1) // 2
  println(num2) // 10
}
