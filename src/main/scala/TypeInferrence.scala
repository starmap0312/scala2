
object TypeInferrence {
  def main(args: Array[String]): Unit = {

    def func1(x: Int) = if (x % 2 == 0) 0 else 1     // (x: Int)Int
    def func2(x: Int) = if (x % 2 == 0) 0 else "one" // (x: Int)Any
    println(func1(1))                                // 1
    println(func2(1))                                // one

  }
}
