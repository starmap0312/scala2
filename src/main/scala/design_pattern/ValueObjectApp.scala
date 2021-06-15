package design_pattern

// https://github.com/josephguan/scala-design-patterns/tree/master/creational/value-object
// the pattern provides objects which follow value semantics rather than reference semantics
//   i.e. value objects' equality are not based on identity (the same object)
//        two value objects are equal when they have the same value

// value object
//   Scala supports value objects via `case class`
case class Point(x: Int, y: Int)

object ValueObjectApp extends App {
  val point1 = Point(1, 2)
  val point2 = Point(1, 2)
  println(point1)
  println(point1 == point2)
}
