// 1) Implicit Conversion:
//    ex. obj.m(), where obj is of class C
//        if class C does not support method m()
//        then Scala will look for an implicit conversion from C to something that does support m()
//    ex. "abc".map(_.toInt)
//        String does not support the method map(), but StringOps does
//        so there’s an implicit conversion from String to StringOps
// 2) Implicit Parameters:
//    if a method call has implicit parameters, then the compiler tries to fill them in automatically
//    If the compiler can’t find one, then it will complain
// 3) Implicit conversions as implicit parameters
//    an implicit is both an implicit conversion and an implicit parameter
// 4) Context Bounds
//    implicit parameters, used in type class pattern
//    the pattern enables the provision of common interfaces to classes which did not declare them
//    it can both serve as a bridge pattern (gaining separation of concerns) and as an adapter pattern
// 5) search for implicits rules
// 5.1) First look in current scope
//      "Implicits defined in current scope"
//      Explicit imports
//      wildcard imports
// 5.2) Next look at associated types in
//      "Companion objects of a type"
//      "Implicit scope of an argument’s type" (2.9.1)
//      Implicit scope of type arguments (2.8.0)
//      Outer objects for nested types
import scala.collection.immutable.StringOps

object Implicits {
  def main(args: Array[String]): Unit = {
    // 1) Implicit Conversion:
    //    String does not have map() method, but we can still write the following
    println("abc".map(_.toInt))                   // Vector(97, 98, 99)
    //    this is because there is an implicit method defined for the conversion of String to StringOps (in Predef.scala)
    //    implicit def augmentString(x: String): StringOps = new StringOps(x)
    //    the above is converted to the following automatically
    println(augmentString("abc").map(_.toInt))    // Vector(97, 98, 99)
    println((new StringOps("abc")).map(_.toInt))  // Vector(97, 98, 99)

    // 2) Implicit Parameters:
    // ex1.
    implicit val n: Int = 5                       // Implicits Defined in Current Scope
    def add(x: Int)(implicit y: Int) = x + y
    println(add(5))                               // takes n from the current scope, res: Int = 10
    // ex2.
    def foo[T](t: T)(implicit integral: Integral[T]): Unit = {
      println(integral)
    }
    foo(1)                                        // scala.math.Numeric$IntIsIntegral$@1cd072a9

    // 3) Implicit conversions as implicit parameters
    def getIndex[T, CC](seq: CC, value: T)(implicit conv: CC => Seq[T]) = {
      seq.indexOf(value)
      // because CC does not have indexOf() method, so the above is converted by compiler as the following:
      //conv(seq).indexOf(value)
    }
    println(getIndex("abc", 'b'))                 // 1

    // 4) Context Bounds
    // ex1.
    def sum1[T](list: List[T])(implicit integral: Integral[T]): T = {
      import integral._                           // Wildcard Imports: get the implicits in question into scope
      list.foldLeft(integral.zero)(_ + _)
    }
    // the following is a syntactic sugar of the above, i.e. context bound
    def sum2[T: Integral](list: List[T]): T = {
      val integral = implicitly[Integral[T]]
      import integral._                           // get the implicits in question into scope
      list.foldLeft(integral.zero)(_ + _)
    }
    // Because Integral[T] was implicitly passed to sum2, it can then pass it implicitly to foldLeft
    println(sum1(List(1, 2, 3)))                  // 6
    println(sum2(List(1, 2, 3)))                  // 6
    // ex2.
    def reverseSort[T : Ordering](seq: Seq[T]) = {
      seq.reverse.sorted
    }
    // Because Ordering[T] was implicitly passed to reverseSort, it can then pass it implicitly to sorted
    println(reverseSort(List(3, 2, 1)))           // List(1, 2, 3)

    // 5.1) Companion Objects of a Type
    // Why can we write this
    for {
      x <- List(1, 2, 3)
      y <- Some('x')
    } yield (x, y)
    // the above is a syntactic sugar of the following
    println(List(1, 2, 3).flatMap(x => Some('x').map(y => (x, y)))) // List((1,x), (2,x), (3,x))
    // note: List.flatMap expects a TraversableOnce, which Option is not
    //   def flatMap[B, That](f: A => GenTraversableOnce[B])
    // so the compiler looks inside Option’s object companion and finds the conversion to Iterable (a TraversableOnce)
    //   object Option {
    //     implicit def option2Iterable[A](xo: Option[A]): Iterable[A] = xo.toList
    //   }
    // i.e. the above is converted into the following by compiler
    println(List(1, 2, 3).flatMap(x => Option.option2Iterable(Some('x')).map(y => (x, y)))) // List((1,x), (2,x), (3,x))
    // List(
    //   option2Iterable(Option((1,x))),
    //   option2Iterable(Option((2,x))),
    //   option2Iterable(Option((3,x))),
    // ).flatten
    // = List(
    //   List((1,x)),
    //   List((2,x)),
    //   List(3,x)),
    // ).flatten
    // = List((1,x), (2,x), (3,x))

    // 5.2) Implicit scope of an argument’s type
    class A(val n: Int) {
      def +(other: A) = new A(n + other.n)
    }
    object A {
      implicit def fromInt(n: Int) = new A(n)
    }
    // Why can we write this
    1 + new A(1)
    // because it is converted into this:
    A.fromInt(1) + new A(1)

  }
}
