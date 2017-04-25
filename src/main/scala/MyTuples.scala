final case class MyTuple1[+T1](_1: T1)
final case class MyTuple2[+T1, +T2](_1: T1, _2: T2)

object MyTuples {
  def main(args: Array[String]): Unit = {
    // 1) syntactic sugars for constructing Tuples
    val tuple1 = MyTuple2(1, "two") // (1, two)
    val tuple2 = (1, "two")         // (1, two)
    val tuple3 = 1 -> "two"         // (1, two)
    assert(tuple1._1 == tuple2._1)
    assert(tuple2._2 == tuple3._2)

    // 2) Tuples used in pattern matching
    tuple2 match {
      case (x, y) => println("x = " + x + ", y = " + y)         // x = 1, y = two
      // the above is syntactic sugar of the following
      //case Tuple2(x, y) => println("x = " + x + ", y = " + y)
    }
  }
}
