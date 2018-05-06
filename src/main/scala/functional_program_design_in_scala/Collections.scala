package functional_program_design_in_scala

object Collections extends App {
  // 2) for-expression: used to simplify flatMap(), map(), and withFilter()
  def isEven(n: Int) = (n % 2 == 0)
  val n = 3
  val m = 5
  val evenSums = for {
    i <- (1 until n)
    j <- (1 until m)
    if isEven(i + j)
  } yield (i, j)
  println(evenSums) // Vector((1,1), (1,3), (2,2), (2,4))
}
