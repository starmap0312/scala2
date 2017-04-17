// Partially applied function
//   currying: separate a function of n parameters into n functions of one parameter
//   i.e. func(a, b, c) --> func(a)(b)(c)
object PartiallyAppliedFunction {
  def main(args: Array[String]): Unit = {
    // 1) multiple parameters method & FunctionN:
    def methodAdder(m: Int, n: Int) = m + n                              // (m: Int, n: Int)Int: method with two parameters
    def func2Adder = methodAdder _                                       // (Int, Int) => Int, convert method into Function2
    println(methodAdder(2, 3))                                           // 5
    println(func2Adder(2, 3))                                            // 5

    // 2) curried Function1's:
    def curriedAdder1: Int => Int => Int = (m: Int) => (n: Int) => m + n // Int => (Int => Int): curried Function1 instance
    def curriedAdder2 = func2Adder.curried                               // Int => (Int => Int): convert FunctionN into curried Function1's
    println(curriedAdder1(2)(3))                                         // 5
    println(curriedAdder2(2)(3))                                         // 5

    // 3) curried method
    def curriedMethod(m: Int)(n: Int) = m + n                            // (m: Int)(n: Int)Int: a curried method
    def curriedAdder3 = curriedMethod _                                  // Int => (Int => Int), convert curried method into curried Function1's
    def curriedAdder4 = curriedMethod(_: Int)(_: Int)                    // (Int, Int) => Int, converted into a Function2
    println(curriedMethod(2)(3))                                         // 5
    println(curriedAdder3(2)(3))                                         // 5
    println(curriedAdder4(2, 3))                                         // 5

    // 4) partially applied method: convert method into FunctionN via partial application
    def func1Adder1 = methodAdder(2, _: Int)                             // (Int) => Int: Function1 instance
    def func1Adder2: (Int) => Int = methodAdder(2, _)                    // (Int) => Int: Function1 instance
    def func2Adder2 = methodAdder(_, _)                                  // (Int, Int) => Int: Function2 instance
    println(func2Adder2(2, 3))                                           // 5
    println(func1Adder1(3))                                              // 5
    println(func1Adder2(3))                                              // 5

  }
}
