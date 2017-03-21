// 1) total function:
//    it works for every argument of the defined type
//    ex. a function defined as (Int) => String takes any Int and returns a String
// 2) partial function:
//    it is only defined for certain values of the defined type
//    ex. a Partial Function (Int) => String might not accept every Int
// PartialFunction[-A, +B] extends (A => B)
//   it is a subclass of Function1
// isDefinedAt() method:
//   used to determine if the PartialFunction will accept a given argument
// orElse([function]) method:
object PartialFunctions {
  def main(args: Array[String]): Unit = {
    val one  : PartialFunction[Int, String] = { case 1 => "one" }   // Function1 instance
    val two  : PartialFunction[Int, String] = { case 2 => "two" }   // Function1 instance
    val three: PartialFunction[Int, String] = { case 3 => "three" } // Function1 instance
    val other: PartialFunction[Int, String] = { case _ => "other" } // Function1 instance
    val numbers1 = one orElse two orElse three orElse other         // composition of Function1 instances
    // the above is equivalent to the following
    val numbers2: PartialFunction[Int, String] = {
      case 1 => "one"
      case 2 => "two"
      case 3 => "three"
      case _ => "other"
    }
    //println(one(2))                                               // runtime scala.MatchError
    println(numbers1(1))                                            // one
    println(numbers1(2))                                            // two
    println(numbers1(3))                                            // three
    println(numbers1(4))                                            // other
    println(numbers2(1))                                            // one
    println(numbers2(2))                                            // two
    println(numbers2(3))                                            // three
    println(numbers2(4))                                            // other
  }
}
