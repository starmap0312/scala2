package design_pattern.structural

// https://github.com/josephguan/scala-design-patterns/tree/master/structural/magnet
// the pattern is an alternative approach to "method overloading"
// the pattern is similar to the type-classes pattern in that:
//   there is a conversion happening on the actual parameter when the client receives the actual parameter
//   it differs in that the implicit classes are defined in the target class companion instead of the type class (magnet class) companion

// magnet interface
//   it declares a magnet interface and an abstract type for result
trait DoubleMagnet {

  type Result // undefined
  def apply(): Result // unimplemented
}

// concrete magnets (implicit classes)
//   it implements the magnet interface and declares it as implicit class
object DoubleMagnet {

  // implicit conversions
  implicit class fromInt(x: Int) extends DoubleMagnet {

    override type Result = Int
    // note that you need to defined the return Result type here; otherwise, a compile error will be raised
    override def apply(): Result = x * 2
  }

  implicit class fromListInt(ls: List[Int]) extends DoubleMagnet {

    override type Result = List[Int]
    override def apply(): Result = ls.map(_ * 2)
  }

  implicit class fromListString(ls: List[String]) extends DoubleMagnet {

    override type Result = List[String]
    override def apply(): Result = ls ++ ls
  }

  // overloading with different number of parameters: Tuple2[String, Int]
  implicit class fromStringIntTuple(tuple: (String, Int)) extends DoubleMagnet {

    override type Result = String
    override def apply(): String = tuple._1 * tuple._2
  }
}

// client
//   it defines a function which take a magnet object as argument and return the type of magnet.Result
class Doubling {

  def double(magnet: DoubleMagnet): magnet.Result = magnet() // i.e. magnet.apply(): the magnet implements the apply() method
  // note that Doubling takes a DoubleMagnet instead of the actual parameters: Int, List[Int], List[String], (String, Int), etc.
  //   the actual parameters will be automatically wrapped in a DoubleMagnet class as the implicit classes (conversions) are defined in the DoubleMagnet companion object
}

class DoublingThatDoesNotWork {

  def double(x: Int): Int = x * 2
  def double(ls: List[Int]): List[Int] = ls.map(_ * 2)
  def double(tuple: (String, Int)): String = tuple._1 * tuple._2

  // overloading collisions caused by type erasure, as type parameter Int is erased by the compiler:
  //   the following definition results in a compile error: double(scala.List) is already defined in the scope
  // def double(ls: List[String]): List[String] = ls ++ ls

}

object MagnetApp extends App {
  val doubling = new Doubling()
  // objective:
  //   Overload double function so that it can process Int, List[Int], List[String] and Tuple2[String, Int]
  println(doubling.double(2)) // 4
  println(doubling.double(List(1, 2, 3))) // List(2, 4, 6)
  println(doubling.double(List("a", "b", "c"))) // List(a, b, c, a, b, c)
  println(doubling.double("a", 5)) // aaaaa

  val doubling2 = new DoublingThatDoesNotWork()
  println(doubling2.double(2)) // 4
  println(doubling2.double(List(1, 2, 3))) // List(2, 4, 6)
  //println(doubling2.double(List("a", "b", "c"))) // not implemented due to overloading collisions
  println(doubling2.double("a", 5)) // aaaaa
}
