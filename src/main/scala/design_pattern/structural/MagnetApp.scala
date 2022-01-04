package design_pattern.structural

// https://github.com/josephguan/scala-design-patterns/tree/master/structural/magnet
// the pattern is an alternative approach to "method overloading"
// the pattern is similar to the type-classes pattern in that:
//   there is a conversion happening on the actual parameter when the client receives the actual parameter
//   it differs in that the implicit classes are defined in type class (magnet class) companion, instead of the target class companion

// 1) original: magnet interface with undefined type
// magnet interface (type class)
//   it declares a magnet interface and an abstract type for result
trait DoubleMagnet {

  type R // undefined type, an alternative of defining trait DoubleMagnet[R]
  def apply(): R // unimplemented
}

// magnet class companion (implicit classes)
//   it implements the magnet interface and declares it as implicit class
object DoubleMagnet {

  // implicit conversions
  implicit class fromInt(x: Int) extends DoubleMagnet {

    override type R = Int
    // note that you need to defined the return R type here; otherwise, a compile error will be raised
    override def apply(): R = x * 2
  }

  implicit class fromListInt(ls: List[Int]) extends DoubleMagnet {

    override type R = List[Int]
    override def apply(): R = ls.map(_ * 2)
  }

  implicit class fromListString(ls: List[String]) extends DoubleMagnet {

    override type R = List[String]
    override def apply(): R = ls ++ ls
  }

  // overloading with different number of parameters: Tuple2[String, Int]
  implicit class fromStringIntTuple(tuple: (String, Int)) extends DoubleMagnet {

    override type R = String
    override def apply(): String = tuple._1 * tuple._2
  }
}

// client
//   it defines a function which take a magnet object as argument and return the type of magnet.R
class Doubling {

  // 1) original
  def double(magnet: DoubleMagnet): magnet.R = magnet() // i.e. magnet.apply(): the magnet implements the apply() method
  // note that Doubling takes a DoubleMagnet instead of the actual parameters: Int, List[Int], List[String], (String, Int), etc.
  //   the actual parameters will be automatically wrapped in a DoubleMagnet class as the implicit classes (conversions) are defined in the DoubleMagnet companion object

  // 2) alternative
  def doubleGeneric[R](magnet: DoubleGeneric[R]): R = magnet() // i.e. magnet.apply(): the magnet implements the apply() method
}

// 2) alternative: magnet interface with generic type
trait DoubleGeneric[R] {

  def apply(): R // unimplemented
}

// concrete magnets (implicit classes)
//   it implements the magnet interface and declares it as implicit class
object DoubleGeneric {

  // implicit conversions
  implicit class fromInt(x: Int) extends DoubleGeneric[Int] {

    // note that you need to defined the return R type here; otherwise, a compile error will be raised
    override def apply(): Int = x * 2
  }

  implicit class fromListInt(ls: List[Int]) extends DoubleGeneric[List[Int]] {

    override def apply(): List[Int] = ls.map(_ * 2)
  }

  implicit class fromListString(ls: List[String]) extends DoubleGeneric[List[String]] {

    override def apply(): List[String] = ls ++ ls
  }

  // overloading with different number of parameters: Tuple2[String, Int]
  implicit class fromStringIntTuple(tuple: (String, Int)) extends DoubleGeneric[String] {

    override def apply(): String = tuple._1 * tuple._2
  }
}

// 3) alternative: method overloading (it has limitation as there may be overloading collisions)
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
  // 1) objective: magnet pattern with undefined type
  //   Overload double function so that it can process Int, List[Int], List[String] and Tuple2[String, Int]
  println(doubling.double(2)) // 4
  println(doubling.double(List(1, 2, 3))) // List(2, 4, 6)
  println(doubling.double(List("a", "b", "c"))) // List(a, b, c, a, b, c)
  println(doubling.double("a", 5)) // aaaaa
  println

  // 2) alternative: magnet pattern with generic type
  val doubling3 = new Doubling()
  println(doubling3.doubleGeneric(2)) // 4
  println(doubling3.doubleGeneric(List(1, 2, 3))) // List(2, 4, 6)
  println(doubling3.doubleGeneric(List("a", "b", "c"))) // List(a, b, c, a, b, c)
  println(doubling3.doubleGeneric("a", 5)) // aaaaa
  println

  // 3) alternative: method overloading
  val doubling2 = new DoublingThatDoesNotWork()
  println(doubling2.double(2)) // 4
  println(doubling2.double(List(1, 2, 3))) // List(2, 4, 6)
  //println(doubling2.double(List("a", "b", "c"))) // not implemented due to overloading collisions
  println(doubling2.double("a", 5)) // aaaaa

}
