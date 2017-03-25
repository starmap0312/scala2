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

object MyFunctions {
  def main(args: Array[String]): Unit = {
    val function1 = new MyFunction1[Int, Int] { // create a Function1 object
      def apply(x: Int): Int = {
        x + 1
      }
    }
    val function2: (Int) => Int = {
      (x: Int) => x + 1
    }
    val function3 = {                           // omitting return type, as it can be inferred by the compiler
      (x: Int) => x + 1
    }
    val function4: (Int) => Int = {             // syntactic sugar for writing Function1
      _ + 1                                     // type of _ can be inferred from the return type
    }
    println(function1.apply(1))                 // 2
    println(function1(1))                       // 2, a syntactic sugar of omitting apply
    println(function2(1))                       // 2
    println(function3(1))                       // 2
    println(function4(1))                       // 2
  }
}
