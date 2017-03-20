/**
  * Created by kuanyu on 3/20/17.
  */
object PartialApplication {
  def main(args: Array[String]): Unit = {
    def adder(m: Int, n: Int) = m + n   // (m: Int, n: Int)Int: expression not yet evaluated
    println(adder(2, 3))                // 5

    val add = adder(_, _)               // (Int, Int) => Int: Function2
    println(add(2, 3))                  // 5

    // we can obtain a partially applied Function1 object as below
    val addTwo = adder(2, _: Int)       // (Int) => Int: Function1
    println(addTwo(3))                  // 5

    // or curry the function as below
    val curriedAdd = (adder _).curried  // Int => (Int => Int): Function1
    val addTwo2 = curriedAdd(2)         // Int => Int: Function1
    println(addTwo2(3))                 // 5

    // or we can define as below
    def adder2(m: Int)(n: Int) = m + n  // (m: Int)(n: Int)Int: expression not yet evaluated
    val addTwo3 = adder2(2) _           // Int => Int: Function1
    println(adder2(2)(3))               // 5
    println(addTwo3(3))                 // 5
  }
}
