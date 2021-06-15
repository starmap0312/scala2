package design_pattern.creational

// https://github.com/josephguan/scala-design-patterns/tree/master/creational/value-object
// the pattern ensures that two value objects' equality are not based on identity (i.e. the same object) but their values

// value object
//   Scala supports value objects via `case class`
case class Point(x: Int, y: Int)

object ValueObjectApp extends App {
  val point1 = Point(1, 2)
  val point2 = Point(1, 2)
  println(point1) // Point(1,2)
  println(point1 == point2) // true, the two value objects have the same value
}
