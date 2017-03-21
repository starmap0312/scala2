// Partially applied function
//   currying: separate a function of n parameters into n functions of one parameter
//   i.e. func(a, b, c) --> func(a)(b)(c)
object PartiallyAppliedFunction {
  def main(args: Array[String]): Unit = {
    def adder(m: Int, n: Int) = m + n       // (m: Int, n: Int)Int: expression not yet evaluated (method)
    println(adder(2, 3))                    // 5

    val add = adder(_, _)                   // (Int, Int) => Int: Function2 instance
    println(add(2, 3))                      // 5

    // we can obtain a partially applied Function1 object as below
    val addTwo1 = adder(2, _: Int)          // (Int) => Int: Function1 instance
    val addTwo2: (Int) => Int = adder(2, _) // (Int) => Int: Function1 instance
    println(addTwo1(3))                     // 5
    println(addTwo2(3))                     // 5

    // or curry the function as below
    val curriedAdder1: Int => Int => Int = (m: Int) => (n: Int) => m + n // Int => (Int => Int): Function1 instance
    val curriedAdder2 = (adder _).curried                                // Int => (Int => Int): Function1 instance
    println(curriedAdder1(2)(3))            // 5
    println(curriedAdder2(2)(3))            // 5

    // or we can define method as below
    def curriedAdder3(m: Int)(n: Int) = m + n  // (m: Int)(n: Int)Int: expression not yet evaluated (method)
    val addTwo4 = curriedAdder3(2) _        // (Int) => Int: Function1 instance
    println(curriedAdder3(2)(3))            // 5
    println(addTwo4(3))                     // 5
  }
}
