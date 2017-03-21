// flatMap[B](f: A => Container[B]): Container[B]

object FunctionalCombinators {
  def main(args: Array[String]): Unit = {
    // 1) List.map([function]):
    //    evaluates a function over each element in the list, returning a list with the same number of elements
    println(List(1, 2, 3).map((x: Int) => x * 2))                 // List(2, 4, 6)
    println(List(1, 2, 3).map(_ * 2))                             // List(2, 4, 6)

    // 2) List.foreach([function]):
    //    it is like map but returns nothing (Unit)
    println(List(1, 2, 3).foreach((x: Int) => print(x * 2)))      // 246()

    // 3) List.filter([boolean function]):
    //    removes any elements where the function you pass in evaluates to false
    println(List(1, 2, 3).filter(_ % 2 == 0))                     // List(2)

    // 4) List.flatten:
    //    collapses one level of nested structure
    println(List(List(1, 2), List(3)).flatten)                    // List(1, 2, 3)

    // 5) List.flatMap([function]):
    //    it combines mapping and flattening
    //    it takes a function that may return nested lists and then concatenates the flattened results back
    println(List(1, 2, 3).flatMap((x: Int) => List(x * 2)))       // List(2, 4, 6)
    // the above is a combination of map() and flatten() as below
    println(List(1, 2, 3).map((x: Int) => List(x * 2)))           // List(List(2), List(4), List(6))
    println((List(1, 2, 3).map((x: Int) => List(x * 2))).flatten) // List(List(2), List(4), List(6))

  }
}
