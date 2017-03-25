// trait Function1[-T1, +R]:
// 1) used to instantiate a function that takes 1 parameter
// 1) a Function1 is a callable object, as the apply() method is defined in its class
trait MyFunction1[-T1, +R] {
  // apply the body of this function to the argument
  def apply(v1: T1): R

  // composes two instances of Function1 in a new Function1, with this function applied last
  def compose[A](g: A => T1): A => R = {
    x => apply(g(x))
  }

  // composes two instances of Function1 in a new Function1, with this function applied first
  def andThen[A](g: R => A): T1 => A = {
    x => g(apply(x))
  }
}

trait MyFunction2[-T1, -T2, R] {

  def apply(v1: T1, v2: T2): R

  // creates a tupled version of this function
  // instead of 2 arguments, it accepts a single Tuple2 argument.
  def curried: T1 => (T2 => R) = {          // i.e. type: T1 => (T2 => R)
    (x: T1) => (
      (y: T2) => apply(x, y)
    )
  }
}

object MyFunctions {
  def main(args: Array[String]): Unit = {
    // example: Function1
    def function1 = new MyFunction1[Int, Int] { // i.e. type: (Int) => Int
      // create a Function1 object
      def apply(x: Int): Int = {
        x + 1
      }
    }
    def function2: ((Int) => Int) = {           // i.e. type: (Int) => Int
      (x: Int) => x + 1
    }
    def function3 = {                           // i.e. type: (Int) => Int
      // omitting return type, as it can be inferred by the compiler
      (x: Int) => x + 1
    }
    def function4: ((Int) => Int) = {           // i.e. type: (Int) => Int
      // syntactic sugar for writing Function1
      _ + 1 // type of _ can be inferred from the return type
    }
    println(function1.apply(1)) // 2
    println(function1(1))       // 2, a syntactic sugar of omitting apply
    println(function2(1))       // 2
    println(function3(1))       // 2
    println(function4(1))       // 2

    // compose() and andThen
    println((function1 compose function2)(1)) // 3
    println((function1 andThen function2)(1)) // 3

    // example: Function2
    def function5 = new MyFunction2[Int, Int, Int] { // i.e. type: (Int, Int) => Int
      def apply(x: Int, y: Int): Int = {
        x + y
      }
    }
    def function6: ((Int, Int) => Int) = {           // i.e. type: (Int, Int) => Int
      (x: Int, y: Int) => x + y
    }
    def function7: ((Int, Int) => Int) = {           // i.e. type: (Int, Int) => Int
      _ + _
    }
    // curried functions
    def curried = function7.curried                  // i.e. type: Int => (Int => Int)
    println(function5(2, 2))    // 4
    println(function6(2, 2))    // 4
    println(function7(2, 2))    // 4
    println(curried(2)(2))      // 4

    def method(x: Int)(y: Int) = x + y               // i.e. type: (x: Int)(y: Int)Int
    def function8 = method(_)                        // i.e. type: Int => (Int => Int)
    def function9 = method(3)(_)                     // i.e. type: (Int) => Int
    def function10 = method(_: Int)(_: Int)          // i.e. type: (Int, Int) => Int
    println(method(3)(3))       // 6
    println(function8(3)(3))    // 6
    println(function9(3))       // 6
    println(function10(3, 3))   // 6
  }
}
