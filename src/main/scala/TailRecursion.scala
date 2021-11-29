import scala.annotation.tailrec
// ref: https://www.scala-exercises.org/scala_tutorial/tail_recursion

object TailRecursion extends App {

  // 1) traditional recursion:
  // @tailrec
  def factorial(n: Int): Int= {
    if (n == 0) 1 else n * factorial(n - 1)
  }
  // note: if we annotate the recursive method with @tailrec, we will get compiler error:
  //   Error: could not optimize @tailrec annotated method, as it contains a recursive call not in tail position

  println(factorial(4)) // 24
  // 4 * factorial(3)
  // 4 * (3 * factorial(2))
  // 4 * (3 * (2 * factorial(1)))
  // 4 * (3 * (2 * (1 * factorial(0)))
  // 4 * (3 * (2 * (1 * 1)))

  // 2) tail recursion:
  //    the return value of any given recursive step is the same as the return value of the next recursive call
  //    so you do not need the current stack frame any more, which allows for some compiler optimization
  def factorialWithTailRecursion(n: Int): Int = {
    @tailrec
    def factorial(m: Int, result: Int): Int = {
      if (m == 1) result else factorial(m - 1, m * result)
    }
    factorial(n, 1)
  }
  println(factorialWithTailRecursion(4)) // 24
  // factorial(4, 1)
  // factorial(3, 4 * 1)
  // factorial(2, 3 * 4 * 1)
  // factorial(1, 2 * 3 * 4 * 1)
  // (2 * 3 * 4 * 1)

}
