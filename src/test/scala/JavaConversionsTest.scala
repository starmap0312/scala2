import scala.collection.JavaConverters._

// Scala and Java container conversions:
//   import scala.collection.JavaConversions vs. import scala.collection.JavaConverters
// 1) JavaConversions: (implicit conversion)
//    this provide a series of implicit methods that convert between a Java collection and
//      the closest corresponding Scala collection, and vice versa
//    Scala -> Java: it is done by creating wrappers that implement Scala interface and forward the calls to
//      the underlying Java collection
//    Java -> Scala: it is done by creating wrappers that implement Java interface and forwarding the calls to
//      the underlying Scala collection
// 2) JavaConverters: (explicit conversion)
///   it uses the pimp-my-library pattern to “add” the asScala method to the Java collections and
//    it uses the pimp-my-library pattern to “add” the asJava method to the Scala collections
//      the above two return the appropriate wrappers discussed above
//    it makes the conversion between Scala and Java collection explicit.
//    (the recommended approach)

// import scala.collection.JavaConversions:
//   the following two-way conversions are provided: (scala mutable containers <=> java containers)
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

// import scala.collection.JavaConverters:
//   the following conversions are supported via `asScala` and `asJava`:
// *    scala.collection.Iterable               <=> java.lang.Iterable
// *    scala.collection.Iterator               <=> java.util.Iterator
// *    scala.collection.mutable.Buffer         <=> java.util.List
// *    scala.collection.mutable.Set            <=> java.util.Set
// *    scala.collection.mutable.Map            <=> java.util.Map
// *    scala.collection.mutable.concurrent.Map <=> java.util.concurrent.ConcurrentMap
// the following one-way conversions are provided via `asJava`:
// *    scala.collection.Seq         => java.util.List
// *    scala.collection.mutable.Seq => java.util.List
// *    scala.collection.Set         => java.util.Set
// *    scala.collection.Map         => java.util.Map
// the following conversions are supported via `asScala` and 
//   through specially-named extension methods to convert to Java collections
// *    scala.collection.Iterable    <=> java.util.Collection   (via asJavaCollection)
// *    scala.collection.Iterator    <=> java.util.Enumeration  (via asJavaEnumeration)
// *    scala.collection.mutable.Map <=> java.util.Dictionary   (via asJavaDictionary)
// the following one way conversion is provided via `asScala`:
// *    java.util.Properties => scala.collection.mutable.Map
//
// Note:
// 1) because Java does not distinguish between mutable and immutable collections in their type, so
//    a conversion from, scala.immutable.List will yield a java.util.List,
//    whereas all mutation operations throw an “UnsupportedOperationException”
//
// Scala and Java numeric types conversions
// 1) scala.String


object JavaConversionsTest {
  def main(args: Array[String]): Unit = {
    // JavaConversions: implicit conversion
    // 1) two-way conversions:
    // 1.1) scala.collection.mutable.Buffer <=> java.util.List
    val scalaListBuffer = new scala.collection.mutable.ListBuffer[Int]
    val javaList : java.util.List[Int] = scalaListBuffer.asJava
    val scalaListBuffer2 : scala.collection.mutable.Buffer[Int] = javaList.asScala
    assert(scalaListBuffer eq scalaListBuffer2)
    val javaList2 : java.util.List[Int] = List(1, 2, 3).asJava // throws "type mismatch" if not importing JavaConversions
    println(javaList2) // [1, 2, 3]

    // 1.2) scala.collection.mutable.Map    <=> java.util.Map
    val scalaMap1 = scala.collection.mutable.Map[String, Int]()
    scalaMap1.put("one", 1) // must be mutable.Map to support put() operation
    println(scalaMap1)      // Map(one -> 1)
    val javaMap1 : java.util.Map[String, Int] = scalaMap1.asJava
    javaMap1.put("two", 2)
    println(javaMap1)       // {one=1, two=2}

    // 2) one-way conversions:
    // 2.1) scala.collection.Seq         => java.util.List
    val javaList3 : java.util.List[Int] = Seq(1, 2, 3).asJava // throws "type mismatch" if not importing JavaConversions
    println(javaList3)      // [1, 2, 3]

    // 2.2) scala.collection.Map (immutable)  => java.util.Map
    val javaMap2 : java.util.Map[String, Int] = Map("one" -> 1, "two" -> 2).asJava
    println(javaMap2)       // {one=1, two=2}
    // javaMap2.put("three", 3) // throws UnsupportedOperationException as Map is immutable

    // JavaConverters: explicit conversion
    // 1) two-way conversions:
    // 1.1) scala.collection.mutable.Buffer <=> java.util.List
    val javaList4 : java.util.List[Int] = scalaListBuffer.asJava
    val scalaListBuffer3 : scala.collection.mutable.Buffer[Int] = javaList4.asScala
    assert(scalaListBuffer eq scalaListBuffer3)
    val javaList5 : java.util.List[Int] = List(1, 2, 3).asJava // throws "type mismatch" if not importing JavaConversions
    println(javaList5) // [1, 2, 3]

    // 1.2) scala.collection.mutable.Map    <=> java.util.Map
    val scalaMap2 = scala.collection.mutable.Map[String, Double]()
    scalaMap2.put("one", 1.0) // must be mutable.Map to support put() operation
    println(scalaMap2)      // Map(one -> 1)
    val javaMap3 : java.util.Map[String, Double] = scalaMap2.asJava
    javaMap3.put("two", 2.0)
    println(javaMap3)       // {one=1, two=2}

    // 2) one-way conversions:
    // 2.1) scala.collection.Seq         => java.util.List
    val javaList6 : java.util.List[Int] = Seq(1, 2, 3).asJava // throws "type mismatch" if not importing JavaConversions
    println(javaList6)      // [1, 2, 3]

    // 2.2) scala.collection.Map (immutable)  => java.util.Map
    val javaMap4 : java.util.Map[String, Int] = Map("one" -> 1, "two" -> 2).asJava
    println(javaMap4)       // {one=1, two=2}
    //javaMap4.put("three", 3) // throws UnsupportedOperationException as Map is immutable

    // 3) convert immutable Scala Map[String, Double] to Java mutable java.util.Map[String, java.lang.Double]
    //    note that both the conversion from scala.Double to java.lang.Double & immutable Map to mutable Map are needed
    //    otherwise, UnsupportedOperationException will be thrown when call put() method as Java Map
    val scalaMap3 = Map[String, Double]("one" -> 1.0, "two" -> 2.0)
    val scalaMap4: Map[String, java.lang.Double] = scalaMap3.mapValues(Double.box).toMap
    val mutableMap: scala.collection.mutable.Map[String, java.lang.Double] = scala.collection.mutable.Map(
      scalaMap4.toSeq: _*
    )
    val javaMap5 : java.util.Map[String, java.lang.Double] = mutableMap.asJava
    javaMap5.put("three", 3.0)
    println(javaMap5)       // {one=1.0, three=3.0, two=2.0}

    // 4) Numeric type conversions
    // 4.1) java.lang.Integer extends java.lang.Number, which is java.lang.Object (equivalent to Scala's AnyRef)
    //      java.lang.Double extends java.lang.Number, which is java.lang.Object (equivalent to Scala's AnyRef)
    val javaInteger = java.lang.Integer.valueOf(1)
    val javaDouble = java.lang.Double.valueOf(1.0)
    println(javaInteger) // 1
    println(javaDouble)  // 1.0
    // 4.2) scala.Int extends AnyVal: equivalent to Java's `int` primitive type
    //      scala.Double extends AnyVal: equivalent to Java's `double` primitive type
    val scalaInt = 1: Int
    val scalaDouble = 1.0: Double
    println(scalaInt)    // 1
    println(scalaDouble) // 1.0
    // 4.3) cast Scala Int & Double (primitive types) to java.lang.Integer & java.lang.Double
    //      implicit def int2Integer([Int]) & implicit def double2Double([Double])
    val javaInteger2 = int2Integer(scalaInt)
    val javaDouble2 = double2Double(scalaDouble)
    println(javaInteger2) // 1
    println(javaDouble2)  // 1.0
    // 4.4) or create new java.lang.Integer & java.lang.Double
    val javaInteger3 = java.lang.Integer.valueOf(scalaInt)
    val javaDouble3 = java.lang.Double.valueOf(scalaDouble)
    println(javaInteger3) // 1
    println(javaDouble3)  // 1.0
    // 4.5) convert java.lang.Integer & java.lang.Double to scala.Int & scala.Double
    //      toInt() & toDouble()
    println(javaInteger.toInt)               // 1
    println(javaDouble.toDouble)             // 1.0
    println(javaInteger.intValue.toInt)      // 1   (the casting toInt is redundant)
    println(javaDouble.doubleValue.toDouble) // 1.0 (the casting toDouble is redundant)

    // 5.5) convert java.util.Optional to scala Option
    //      must be done manually
    val javaOptionalInteger: java.util.Optional[Integer] = java.util.Optional.of(3)
    val scalaOptionInt = if (javaOptionalInteger.isPresent) Some(javaOptionalInteger.get) else None
    println(javaOptionalInteger) // Optional[3]
    println(scalaOptionInt)      // Some(3)
  }
}
