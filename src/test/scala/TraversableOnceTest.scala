// 1) trait TraversableOnce
//    it exists primarily to eliminate code duplication between Iterator and Traversable
//    it implements some of their common methods, ex. reduce(Left), fold(Left), reduceRight, foldRight, etc.
// 2) trait Iterator vs. trait Traversable
//    an Iterator can only be traversed once, whereas a Traversable can be traversed many times
// 2.1) trait Iterator extends TraversableOnce
//      trait Iterator declares hasNext(), next() methods
// 2.2) trait Traversable extends TraversableOnce
//      trait Traversable declares
object TraversableOnceTest {
  def main(args: Array[String]): Unit = {
    // 1) reduce((value1, value2) => merged_value)
    //    reduceLeft((value1, value2) => merged_value)
    //    reduceRight((value1, value2) => merged_value)
    // ex. Array is an TraversableOnce
    println(Array(1, 2, 3).reduce((x, y) => x + y))   // 6
    Array(1, 2, 3).reduceLeft(
      (x, y) => {
        println("(%d, %d)".format(x, y))
        x + y
      }
    )                                                 // (1, 2) (3, 3)
    Array(1, 2, 3).reduceRight(
      (x, y) => {
        println("(%d, %d)".format(x, y))
        x + y
      }
    )                                                 // (2, 3), (1, 5)
    // note: in spark, we have rdd.reduce(f: (T, T) => T): T
    //   ex. RDD[1, 2, 3].reduce((value1, value2) => value1 + value2) == RDD[6]
    // note: in spark, we have rdd.reduceByKey(func: (V, V) => V): RDD[(K, V)]
    //       a Traversable of tuple (key, value) can be reduceByKey as:
    //   ex. RDD[(1, 100), (2, 100), (1, 100)].reduceByKey((value1, value2) => value1 + value2) = RDD[(1, 200), (2, 100)]
    // note: in spark, we have rdd.groupByKey(): RDD[(K, V)]
    //       a Traversable of tuple (key, value) can be groupByKey as:
    //   ex. RDD[(1, 100), (2, 100), (1, 100)].groupByKey() = RDD[(1, [100 200]), (2, [100])]
    // note: in spark, we have rdd.groupBy(func: element => key): RDD[(K, V)]
    //       a Traversable of tuple (key, value) can be groupBy as:
    //   ex. RDD[(1, 100), (2, 100), (1, 100)].groupBy(e => e._1 % 2) = RDD[(0, [100]), (1, [100, 200])]

    // 2) fold([initial value])((value1, value2) => merged_value))
    //    foldLeft([initial value])((value1, value2) => merged_value))
    //    foldRight([initial value])((value1, value2) => merged_value))
    println(Array(1, 2, 3).fold(-6)((x, y) => x + y)) // 0
    Array(1, 2, 3).foldLeft(0)(
      (x, y) => {
        println("(%d, %d)".format(x, y))
        x + y
      }
    )                                                 // (0, 1) (1, 2) (3, 3)
    Array(1, 2, 3).foldRight(0)(
      (x, y) => {
        println("(%d, %d)".format(x, y))
        x + y
      }
    )                                                 // (3, 0) (2, 3), (1, 5)
    println(Set(1 -> 2, 2 -> 3).toMap) // Map(1 -> 2, 2 -> 3)
  }
}