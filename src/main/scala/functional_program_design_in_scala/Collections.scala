package functional_program_design_in_scala

object Collections extends App {
  // 1) for-expression: used to simplify flatMap(), map(), and withFilter()
  def isEven(n: Int) = (n % 2 == 0)
  val n = 3
  val m = 5
  val evenSums1 = for {
    i <- (1 until n)
    j <- (1 until m)
    if isEven(i + j)
  } yield (i, j)
  println(evenSums1) // Vector((1,1), (1,3), (2,2), (2,4))

  // the above is translated as the following:
  val evenSums2 = (1 until n) flatMap {
    i => (1 until m) withFilter {
      j => isEven(i + j)
    } map {
      j => (i, j)
    }
  }
  println(evenSums2) // Vector((1,1), (1,3), (2,2), (2,4))
}
