import com.fasterxml.jackson.databind.util.TypeKey

import scala.collection.SortedSet
import scala.reflect.{ClassTag, classTag}
// a ClassTag[T] stores the erased class of a given type T, accessible via its runtimeClass field
//   ex. when instantiating Arrays whose element types are unknown at compile time
// ClassTag vs. TypeTag
//   ClassTag: it wraps only the runtime class of a given type
//             it is constructed from knowing only the top-level class of a type,
//             without necessarily knowing all of its argument types (enough for runtime Array creation)
//   TypeTag:  it contains all static type information
//
// ClassTag[T] serves as an extractor that matches only objects of type T
//   The compiler tries to turn unchecked type tests in pattern matches into checked ones
//     by wrapping a (_: T) type pattern as ctag(_: T), where ctag is a ClassTag[T] instance
//   i.e. SomeExtractor(...) is turned into ctag(SomeExtractor(...)) if T in SomeExtractor.unapply(x: T)
//        is uncheckable, but we have an instance of ClassTag[T]

object MyClassTag {
  // Class tags corresponding to primitive types and constructor/extractor for ClassTags

  // note: type Class[T] = java.lang.Class[T] is defined in Predef.scala
  def apply[T](runtimeClass1: Class[T]): MyClassTag[T] = {
    new MyClassTag[T] {
      def runtimeClass = runtimeClass1
    }
  }

  //def unapply[T](ctag: MyClassTag[T]): Option[Class[_]] = {
  //  Some(ctag.runtimeClass)
  //}
}

trait MyClassTag[T] {

  // a class representing the type U to which T would be erased (there is no subtyping relationship between T and U)
  def runtimeClass: Class[_]

  // a ClassTag[T] can serve as an extractor that matches only objects of type T
  def unapply(obj: Any): Option[T] = {  // it checks if the type of obj is equal to the wrapped type, and returns Some(obj) if yes
    if (runtimeClass.isInstance(obj)) { // so when you try to val ctag(x) = obj, you get x if x's type is the same as ctag's wrapping and None if not
      Some(obj.asInstanceOf[T])
    } else {
      None
    }
  }
}

case class MyItem(name: String, value: Int)

object MyClassTags {
  def main(args: Array[String]): Unit = {
    // example 1
    val num: Int = 1
    val numCasted: Number = 1.asInstanceOf[Number]
    println(numCasted)      // 1
    // it is a runtime operation: let the compiler believe that the instance is of type Number
    // asInstanceOf[Number] does not do any casting, but simply telling compiler to treat the instance as a Number
    // it may result in a runtime ClassCastException when the instance is evaluated to be something other than a Number
    //println("a".asInstanceOf[Number])  // ClassCastException: java.lang.String cannot be cast to java.lang.Number
    val numClass: Class[Number] = classOf[Number] // type Class[T]      = java.lang.Class[T]
    val classCasted: Number = numClass.cast(1)
    println(classCasted)     // 1
    //println(classOf[Number].cast("a")) // ClassCastException: Cannot cast java.lang.String to java.lang.Number
    // note: classOf[Number] returns a type Class[Number] instance, which has a cast() method

    // ClassTag unapply
    println(s"""classTag[String].unapply("abc")=${classTag[String].unapply("abc")}""") // classTag[String].unapply("abc")=Some(abc)
    println(s"""classTag[Int].unapply("abc")=${classTag[Int].unapply("abc")}""") // classTag[Int].unapply("abc")=None
    val strTag: ClassTag[String] = classTag[String]
    val intTag: ClassTag[Int] = classTag[Int]
    val strTag(x) = "abc"
    //val strTag(x) = 123 // throw scala.MatchError Exception
    val intTag(y) = 123
    println(s"x=$x, y=$y") // x=abc, y=123

    val strClass: Class[String] = classOf[String]
    val ctag: MyClassTag[String] = MyClassTag.apply[String](strClass)
    val str: String = "123"
    val value1: String = ctag.unapply(str) match {
      case Some(x) => x
      case None    => throw new scala.MatchError()
    }
    // the following is a syntactic sugar of the above
    val ctag(value2) = "123"

    println(value1)                  // 123
    println(value2)                  // 123

    // implicit tags
    implicit val tag: MyClassTag[String] = MyClassTag.apply(classOf[String]) // compiler creates an implicit ClassTag instance for you
//    implicit val intTag: MyClassTag[Int] = MyClassTag.apply(classOf[Int])    // compiler creates an implicit ClassTag instance for you
    // or you can use the following
    //implicit val tag = MyClassTag.apply("abc".getClass)    // compiler creates an implicit ClassTag instance for you

    def matchFunc[T](obj: Any)(implicit ctag: MyClassTag[T]) = obj match {
      case ctag(x: T)  => println(s"type T matched: $x") // use the extractor instance to check the type at runtime
      case x           => println(s"not matched: $x")
    }

    matchFunc[String]("abc")                                                  // type T matched: 123
    matchFunc[String](123)                                                    // not matched: 123

    // alernatively, you can write as context bound:
    def matchContexBound[T: MyClassTag](obj: Any) = {
      val tag = implicitly[MyClassTag[T]]
      obj match {
        case tag(x: T) => println(s"type T matched: $x")
        case x => println(s"not matched: $x")
      }
    }
    matchContexBound[String]("abc")                                          // type T matched: 123
    matchContexBound[String](123)                                            // not matched: 123

    // use ClassTag to unwrap an object to a certain type at runtime
    def convert[T](obj: T)(implicit ctag: ClassTag[T]) = {
        // use the implicit MyClassTag to unwrap the obj to type T at runtime
        val ctag(typedObj) = obj // alternatively, val typedObj: T = ctag.unapply(obj).get
        println(s"ctag.unapply(obj): ${ctag.unapply(obj)}, obj: ${obj}, typedObj: ${typedObj}, typedObj.getClass: ${typedObj.getClass}")
        typedObj
    }
    val s: String = convert("abc") // typedObj: abc, typedObj.getClass: class java.lang.String
    val n: Int = convert(123)      // typedObj: 123, typedObj.getClass: class java.lang.Integer
    val d: Double = convert(1.0)   // typedObj: 1.0, typedObj.getClass: class java.lang.Double
    val q1: Seq[Int] = convert(Seq(1, 2))     // typedObj: List(1, 2), typedObj.getClass: class scala.collection.immutable.$colon$colon
    val q2: Seq[Any] = convert(Seq(1, "two")) // typedObj: List(1, two), typedObj.getClass: class scala.collection.immutable.$colon$colon
    val m1: Map[String, Int] = convert(Map("one" -> 1, "two" -> 2))     // typedObj: Map(one -> 1, two -> 2), typedObj.getClass: class scala.collection.immutable.Map$Map2
    val m2: Map[String, Any] = convert(Map("one" -> 1, "two" -> "two")) // typedObj: Map(one -> 1, two -> two), typedObj.getClass: class scala.collection.immutable.Map$Map2
    val item: MyItem = convert(MyItem("item1", 123)) // typedObj: MyItem(item1,123), typedObj.getClass: class MyItem
    println(s, n, d, q1, q2, m1, m2, item) // (abc,123,1.0,List(1, 2),List(1, two),Map(one -> 1, two -> 2),Map(one -> 1, two -> two),MyItem(item1,123))

    // example2: ClassTag is required when creating an array
    def createArray[A : ClassTag](n: Int) = new Array[A](n)
    val intArr: Array[Int] = createArray[Int](10)
    val strArr: Array[String] = createArray[String](10)
    println(intArr.mkString(" "))
    println(strArr.mkString(" "))

    // example 2
    val mp: Map[String, Any] = Map("1" -> 1, "2" -> "two")
    val one: Int = mp("1").asInstanceOf[Int] // runtime casting (may throw java.lang.ClassCastException at runtime)
    println(one) // 1

    // w/o ClassTag
    def getValue[T](key: String, mp: Map[String, Any]): Option[T] = {
      mp.get(key) match {
        case Some(value: T) => Some(value) // type is erased at run-time, so basically we are matching Some(value: Any), which will always be matched
        case _ => None
      }
    }

    val v1: Option[Int] = getValue[Int]("1", mp)
    val v2: Option[String] = getValue[String]("2", mp)
    val v3: Option[Int] = getValue[Int]("2", mp)  // Option("two"), which is mistakenly assigned as Option[Int]

    println(v1) // Some(1)
    println(v2) // Some(two)
    println(v3) // Some(two)
    // v3.map(_ + 1) // we will get java.lang.ClassCastException at runtime!!

    // w/ ClassTag
    def getValue2[T: ClassTag](key: String, mp: Map[String, Any]): Option[T] = {
      mp.get(key) match {
        case Some(value: T) => Some(value) // the implicit ClassTag helps the type check at run-time, so this will NOT be matched if type is different
        case _ => None
      }
    }

    val u1: Option[Int] = getValue2[Int]("1", mp)
    val u2: Option[String] = getValue2[String]("2", mp)
    val u3: Option[Int] = getValue2[Int]("2", mp) // None

    println(u1) // Some(1)
    println(u2) // Some(two)
    println(u3) // None
    u3.map(_ + 1) // ok, as u3 is None


    // alternatively, use an implicit parameter instead of a context bound
    def getValue3[T](key: String, mp: Map[String, Any])(implicit ctag: ClassTag[T]): Option[T] = {
      mp.get(key) match {
        case Some(value: T) => Some(value)                                           // the implicit ClassTag helps the type check at run-time, so this will NOT be matched if type is different
        // case Some(value: T) if ctag.unapply(value).nonEmpty => Some(value)        // alternatively, the implicit ClassTag can be used as such
        // case Some(value: T) if ctag.runtimeClass.isInstance(value) => Some(value) // alternatively, the implicit ClassTag can be used as such
        case _ => None
      }
    }

    val w1: Option[Int] = getValue3[Int]("1", mp) // Some(1)
    val w2: Option[String] = getValue3[String]("2", mp) // Some(two)
    val w3: Option[Int] = getValue3[Int]("2", mp) // None

    println(w1) // Some(1)
    println(w2) // Some(two)
    println(w3) // None
    w3.map(_ + 1) // ok, as w3 is None

    // example 3: type Ordering is an example of type class
    val intOrd: Ordering[Int] = scala.math.Ordering.apply[Int]
//    val intOrd: Ordering[Int] = Ordering[Int]
    val reverseIntOrd: Ordering[Int] = scala.math.Ordering.apply[Int].reverse
//    val reverseIntOrd: Ordering[Int] = Ordering[Int].reverse
    val seqOrd: Ordering[Seq[Int]] = scala.math.Ordering.Implicits.seqOrdering[Seq, Int]
    val reverseSeqOrd: Ordering[Seq[Int]] = scala.math.Ordering.Implicits.seqOrdering[Seq, Int].reverse

    val setOrd: Ordering[SortedSet[Int]] = scala.math.Ordering.Implicits.sortedSetOrdering[SortedSet, Int]
    val reverseSetOrd: Ordering[SortedSet[Int]] = scala.math.Ordering.Implicits.sortedSetOrdering[SortedSet, Int].reverse

    println(Seq(3, 1, 2).sorted(intOrd)) // List(1, 2, 3)
    println(Seq(3, 1, 2).sorted(reverseIntOrd)) // List(3, 2, 1)

    println(Seq(Seq(3, 1, 2), Seq(1, 2, 3)).sorted(seqOrd)) // List(List(1, 2, 3), List(3, 1, 2))
    println(Seq(Seq(3, 1, 2), Seq(1, 2, 3)).sorted(reverseSeqOrd)) // List(List(3, 1, 2), List(1, 2, 3))

    println(Seq(SortedSet(3, 1, 4), SortedSet(1, 2, 3)).sorted(setOrd)) // List(TreeSet(1, 2, 3), TreeSet(1, 3, 4))
    println(Seq(SortedSet(3, 1, 4), SortedSet(1, 2, 3)).sorted(reverseSetOrd)) // List(TreeSet(1, 3, 4), TreeSet(1, 2, 3))

    // example 4
    // extract a specific type
    def extractString(list: List[Any]) = list.flatMap {
      case element: String => Some(element)
      case _ => None
    }
    println(extractString(List(1, "two", List(1, 2), "three", 4))) // List(two, three)

    // extract a generic type (not working, as type is erased at run-time)
    def extract[T](list: List[Any]) = list.flatMap {
      case element: T => Some(element)
      case _ => None
    }
    println(extract[String](List(1, "two", List(1, 2), "three", 4))) // List(1, two, List(1, 2), three, 4)

    def extractWithContextBound[T: ClassTag](list: List[Any]) = list.flatMap {
      case element: T => Some(element)
      case _ => None
    }
    println(extractWithContextBound[String](List(1, "two", List(1, 2), "three", 4))) // List(two, three)

    // the above is translated as the following:
    def extractWithImplicitTag[T](list: List[Any])(implicit ct: ClassTag[T]) = list.flatMap {
      case element: T => Some(element) // i.e. case (element @ ct(_: T)) => Some(element)
      case _ => None
    }
    println(extractWithImplicitTag[String](List(1, "two", List(1, 2), "three", 4))) // List(two, three)

    // example 5
    def extractObjWithTag[T](target: Any)(implicit ct: ClassTag[T]): T = {
      val ct(obj: T) = target // try to unwrap target, which may throw an Exception
      obj
    }
    println("extractObjWithTag: " + extractObjWithTag[String]("abc")) // abc
    // println(extractObjWithTag[String](123))       // throws scala.MatchError Exception!

    def extractObjWithContextBound[T: ClassTag](target: Any): T = {
      val ct = implicitly[ClassTag[T]]
      val ct(obj: T) = target // try to unwrap target, which may throw an Exception
      obj
    }
    println("extractObjWithContextBound: " + extractObjWithContextBound[String]("abc")) // abc
    // println(extractObjWithContextBound[String](123))       // throws scala.MatchError Exception!

    def matchObjWithoutContextBound[T](target: Any): Option[T] = {
      target match {
        case obj: T => Some(obj)
        case _ => None
      }
    }
    println("matchObjWithoutContextBound: " + matchObjWithoutContextBound[String]("abc")) // Some(abc)
    println("matchObjWithoutContextBound: " + matchObjWithoutContextBound[String](123)) // Some(123) (incorrect!! should be None, see the following)

    def matchObjWithContextBound[T: ClassTag](target: Any): Option[T] = {
      target match {
        case obj: T => Some(obj)
        case _ => None
      }
    }
    println("matchObjWithContextBound: " + matchObjWithContextBound[String]("abc")) // Some(abc)
    println("matchObjWithContextBound: " + matchObjWithContextBound[String](123)) // None

    println

    // example 6
    // bad example: a generic method w/o ClassTag
    val values: Map[String, Any] = Map("StringKey" -> "abc", "TupleKey" -> (1 -> 2))

    // we want to write a generic method that works for various type T, but as T is erased at runt-ime the pattern matching does not work as expected
    def getVal1[T](key: String): Option[T] = {
      values.get(key) match {
        case Some(x: T) => Some(x) // it's matching Any type as T is erased at run-time
        case _ => None
      }
    }
    // note that type T cannot be referenced, so it's Nothing
    val strVal1 = getVal1("StringKey") // Option[Nothing]
    val numVal1 = getVal1("IntKey") // Option[Nothing]
    val tupleVal1 = getVal1("TupleKey") // Option[Nothing]
    println(strVal1) // Some(abc), cannot call map as it's Option[Nothing]
    println(numVal1) // None
    println(tupleVal1) // Some((1,2))

    println

    // we can specify the type T manually
    val strVal2 = getVal1[String]("StringKey")        // Option[String]
    val numVal2 = getVal1[Int]("IntKey")              // Option[Int]
    val tupleVal2 = getVal1[Tuple2[_, _]]("TupleKey") // Option[Tuple2[_, _]]
    println(strVal2.map(_ + "de")) // Some(abcde)
    println(numVal2.map(_ + 1)) // None
    println(tupleVal2.map(x => (x._1.toString, x._2.toString + "34"))) //Some((1,234))

    // run-time error case: we specify the wrong type and it's still got matched
    val wrongCastValue = getVal1[Int]("StringKey")    // Option[Int], but it should be Option[String]
    // println(wrongValue.map(_ + 1)) // this throws ClassCastException at run-time!

    println

    // good example: a generic method w/ ClassTag
    // although T is erased at runt-ime, the pattern matching will work as a ClassTag is used implicitly in the pattern matching
    def getVal2[T: ClassTag](key: String): Option[T] = {
      values.get(key) match {
        case Some(x: T) => Some(x) // T's runtimeClass is carried over by the implicit ClassTag at run-time
        case _ => None
      }
    }
    val wrongCastValue2 = getVal2[Int]("StringKey")    // None, as it should be getVal2[String]
    println(wrongCastValue2.map(_ + 1)) // None, this prevents ClassCastException at run-time!

    // good example2: a generic class w/ type class
    trait TypeKey[+X]
    object StringKey extends TypeKey[String] // concrete type classes
    object IntKey extends TypeKey[Int]
    object TupleKey extends TypeKey[(_, _)]

    val typeValues: Map[TypeKey[_], Any] = Map(StringKey -> "abc", TupleKey -> (1 -> 2))

    def getVal[T](key: TypeKey[T]): Option[T] = {
      typeValues.get(key) match {
        case Some(x: T) => Some(x)
        case _ => None
      }
    }
    // type is referenced, you don't need to specify manually
    val strVal = getVal(StringKey)  // Option[String]
    val numVal = getVal(IntKey)     // Option[Int]
    val tupleVal = getVal(TupleKey) // Option[Tuple2[_, _]]
    println(strVal.map(_ + "de")) // Some(abcde)
    println(numVal.map(_ + 1)) // None
    println(tupleVal.map(x => (x._1.toString, x._2.toString + "34"))) // Some((1,234))

    println

    // example 7: match a generic type collection
    // bad example: not working as type is erased at run-time
    def matchSeq[T](seq: Seq[T]) = seq match {
      case x: Seq[Int] => println("case x: Seq[Int]")
      case x: Seq[String] => println("case x: Seq[String]")
      case _ => println("not matched")
    }
    matchSeq(Seq(1, 2, 3))       // case x: Seq[Int]
    matchSeq(Seq("a", "b", "c")) // case x: Seq[Int], as Seq[String] is not matched due to type erasure

    // good example: use classTag for run-time generic type check
    def matchSeqWithContextBound[T: ClassTag](seq: Seq[T]) = seq match {
      case x: Seq[T] if classTag[T].runtimeClass == classOf[String] => println("classTag[T].runtimeClass == classOf[String]")
      case x: Seq[T] if classTag[T].runtimeClass == classOf[Int] =>    println("classTag[T].runtimeClass == classOf[Int]")
      case _ => println("not matched")
    }
    matchSeqWithContextBound(Seq(1, 2, 3))       // classTag[T].runtimeClass == classOf[Int]
    matchSeqWithContextBound(Seq("a", "b", "c")) // classTag[T].runtimeClass == classOf[String]

  }
}
