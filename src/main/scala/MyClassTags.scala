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
  def apply[T](runtimeClass1: Class[_]): MyClassTag[T] = {
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

object MyClassTags {
  def main(args: Array[String]): Unit = {
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

    def matchFunc[T](obj: Any)(implicit ctag: MyClassTag[T]) = obj match {
    // or use context bound as below
    //def matchFunc[T: MyClassTag](value: Any) = value match {
      //case x: T      => { println("type T matched: " + x) }
      // the above is a syntactic sugar of the following
      //case elem@ctag(x) => { println("type T matched: " + x) } // use the extractor instance to check the type at runtime
      case ctag(x: T)  => { println("type T matched: " + x) } // use the extractor instance to check the type at runtime
      // ctag.unapply(value) match {
      //   case Some(x) => x
      //   case None => throw new scala.MatchError
      // }
      case _           => { println("not matched")    }
    }

    implicit val tag: MyClassTag[_] = MyClassTag.apply(classOf[String])    // compiler creates an implicit ClassTag instance for you
    // or you can use the following
    //implicit val tag = MyClassTag.apply("abc".getClass)    // compiler creates an implicit ClassTag instance for you
    matchFunc("abc")                                          // type T matched: 123
    matchFunc(123)                                            // not matched
  }
}
