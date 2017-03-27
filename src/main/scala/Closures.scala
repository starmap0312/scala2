
object Closures {
  def main(args: Array[String]): Unit = {
    var free_var = 0
    def f(bounded_var: Int) = {
      free_var += 1
      bounded_var + free_var
    }
    def g(bounded_var: Int) = {
      free_var += 1
      bounded_var + free_var
    }
    // free_var is free variable with respect to function f and g
    //   because the functions have no clue what value k is bound to (and how)

    println(free_var) // 0
    f(1)
    println(free_var) // 1
    g(1)
    println(free_var) // 2

  }
}
