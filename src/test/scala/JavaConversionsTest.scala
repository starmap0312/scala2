import scala.collection.JavaConversions._

// the following two-way conversions are provided: (scala mutable containers <=> java containers)
// *    scala.collection.Iterable       <=> java.lang.Iterable
// *    scala.collection.Iterable       <=> java.util.Collection
// *    scala.collection.Iterator       <=> java.util.{ Iterator, Enumeration }
// *    scala.collection.mutable.Buffer <=> java.util.List
// *    scala.collection.mutable.Set    <=> java.util.Set
// *    scala.collection.mutable.Map    <=> java.util.{ Map, Dictionary }
// *    scala.collection.concurrent.Map <=> java.util.concurrent.ConcurrentMap

// the following one way conversions are provided: (scala immutable containers => java containers)
// *    scala.collection.Seq         => java.util.List
// *    scala.collection.mutable.Seq => java.util.List
// *    scala.collection.Set         => java.util.Set
// *    scala.collection.Map         => java.util.Map
// *    java.util.Properties         => scala.collection.mutable.Map[String, String]

object JavaConversionsTest {
  def main(args: Array[String]): Unit = {
    // 1) two-way conversions:
    // 1.1) scala.collection.mutable.Buffer <=> java.util.List
    val scalaList = new scala.collection.mutable.ListBuffer[Int]
    val javaList : java.util.List[Int] = scalaList
    val scalaList2 : scala.collection.mutable.Buffer[Int] = javaList
    assert(scalaList eq scalaList2)

    val javaList2 : java.util.List[Int] = List(1, 2, 3) // throws "type mismatch" if not importing JavaConversions
    println(javaList2) // [1, 2, 3]

    // 2.1) scala.collection.mutable.Map         => java.util.Map
    val scalaMap1 = scala.collection.mutable.Map[String, Int]()
    scalaMap1.put("one", 1) // must be mutable.Map to support put() operation
    println(scalaMap1)      // Map(one -> 1)
    val javaMap1 : java.util.Map[String, Int] = scalaMap1
    javaMap1.put("two", 2)
    println(javaMap1)       // {one=1, two=2}

    // 2) one-way conversions:
    // 2.1) scala.collection.Seq         => java.util.List
    val javaList3 : java.util.List[Int] = Seq(1, 2, 3) // throws "type mismatch" if not importing JavaConversions
    println(javaList3)      // [1, 2, 3]

    // 2.2) scala.collection.Map (immutable)  => java.util.Map
    val javaMap2 : java.util.Map[String, Int] = Map("one" -> 1, "two" -> 2)
    println(javaMap2)       // {one=1, two=2}
    // javaMap2.put("three", 3) // throws UnsupportedOperationException as Map is immutable
  }
}
