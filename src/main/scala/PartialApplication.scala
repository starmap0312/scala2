/**
  * Created by kuanyu on 3/20/17.
  */
object PartialApplication {
  def main(args: Array[String]): Unit = {
    def adder(m: Int, n: Int) = m + n   // (Int, Int) => Int
    println(adder(2, 3))                // 5

    // we can obtain a partially applied function object (lambda) as below
    val addTwo = adder(2, _: Int)       // Int => Int
    println(addTwo(3))                  // 5

    // or curry the function as below
    val curriedAdd = (adder _).curried  // Int => (Int => Int)
    val addTwo2 = curriedAdd(2)
    println(addTwo2(3))                 // 5

    // or we can define as below
    def adder2(m: Int)(n: Int) = m + n  // (Int, Int) => Int
    val addTwo3 = adder2(2) _
    println(adder2(2)(3))               // 5
    println(addTwo3(3))                 // 5
  }
}
