import scala.util.Random

trait Superclass {
  def abstract_func(): String                        // abstract method: all subclasses need to implement it; otherwise, compile error
  def func() = println("Superclass's func()")
}

class Subclass extends Superclass {
  def abstract_func() = "Subclass's abstract_func"   // does not need the "override" keyword
  override def func() = println("Subclass's func()") // needs the "override" keyword (); otherwise, compile error
}

object BasicsTest {
  def main(args: Array[String]): Unit = {
    // 0) AnyVal vs. Any vs. AnyRef
    // 0.1) AnyVal: Java primitives
    // 0.2) AnyRef: java.lang.Object
    // 0.3) Any: java.lang.Object and Java primitives
    //      in Scala, all objects are descendant from Any
    //      there is no equivalent in Java because there is no such unification in Java

    // 1) SeqLike operations:
    // 1.1) ::
    //      adds an element at the beginning of this list
    val list: List[String] = List("B")  // B :: Nil
    println("A" :: list)  // List(A, B)
    // the above is equavelient to:
    println(list.::("A")) // List(A, B)
    // 1.2) +:
    //      adds an element at the beginning of this SeqLike
    println("A" +: list)  // List(A, B)
    // similar to ::, but +: can be used in pattern matching but :: cannot
    list match {
      case "B" +: Nil => println("matched")
      case _ => println("no match")
    }                     // matched
    // 1.3) ++
    //      concatenate two lists
    println(List("A") ++ list)  // List(A, B)
    // 1.4) :::
    //      concatenate two lists
    println(List("A") ::: list) // List(A, B)

    // 1.5) find([fn]): find first element that satisfies [fn], it returns Some(value) or None if not found
    println(Seq(1, 2, 3, 4).find(num => num % 2 == 0))   // Some(2)
    println(Seq(1, 2, 3, 4).filter(num => num % 2 == 0)) // List(2, 4)
    println(Seq(1, 2, 3, 4).find(num => num == 5))       // None

    // 1.6) contains([value]): returns if the Seq contains the value, true or false
    println(Seq(1, 2, 3, 4).contains(3)) // true
    println(Seq(1, 2, 3, 4).contains(5)) // false

    // 2) multi-line string:
    //    For every line in this string:
    //    strip a leading prefix consisting of blanks or control characters followed by | from the line
    val multilineStr =
    s"""line1
       |line2
       |line3""".stripMargin // the space| prefix will be removed from each line
    println(multilineStr) // line1 line2 line3

    // 3) unpack an Array
    //    because Array has unapplySeq() defined, so we can treat it as an extractor to unpack a IndexedSeq
    val Array(x1, x2, x3) = Array(1, "two", 3)
    println(x1) // 1
    println(x2) // two
    println(x3) // 3

    // 4) Map[K, V]
    //    map.keys(): returns Iterable[K]
    //    map.values(): returns Iterable[V]
    val map = Map(1 -> "one", 2 -> "two")
    println(map.keys)   // set(1,2)
    println(map.values) // MapLike.DefaultValuesIterable(one, two)

    // 4.1) map.flatMap(entry => Option[T]): returns a collection of List/Map
    val map2 = Map("one"-> 1, "two" -> 2, "three" -> 3).flatMap(
      entry => if (entry._2 % 2 == 1) Some(entry._2) else None
    )
    println(map2) // List(1, 3)
    val map3 = Map("one"-> 1, "two" -> 2, "three" -> 3).flatMap(
      entry => if (entry._2 % 2 == 1) Some(entry) else None
    )
    println(map3) // Map(one -> 1, three -> 3)

    // 5) virtual function:
    //    in Java/Scala, all non-static methods are by default virtual functions (dynamic binding to subclass implementations)
    //    methods marked with the keyword final, which cannot be overridden, are non-virtual
    //    methods marked with private are not inherited, are non-virtual
    val instance: Superclass = new Subclass
    println(instance.abstract_func()) // Subclass's abstract_func
    instance.func()                   // Subclass's func()

    // 6) overriding methods in Scala
    // 6.1) methods cannot be overloaded
    //     i.e. two methods cannot be bound to the same name
    // ex.
    def foo(x: Int) = x
    //def foo(x: String) = println(x)// compile error: foo is already defined in scope

    // 6.2) class methods can be overloaded
    // ex.
    class Bar {
      def foo(x: Int) = println(x)
      def foo(x: String) = println(x)
    }
    (new Bar).foo(5)      // 5
    (new Bar).foo("five") // five

    // 7) read Stdin
    // 7.1) scala.io.StdIn.readLine()
    println("Enter your input:")
    val input = scala.io.StdIn.readLine()
    println(input)
    // 7.2) System.console().readLine() & System.console().readPassword()
    //val input = System.console().readLine()
    // note: System.console() returns null in an IDE, so use scala.io.StdIn.readLine() instead

    // 8) copy() method of case class: functional programming way of creating a new instance from an existing (immutable) one
    case class Person(first: String, last: String)
    val peter = Person("Peter", "Chen")  // val is immutable
    val john1 = peter.copy(first="John") // create a new Person by overriding the frist name but using Person("Peter", "Chen")'s last name
    val john2 = Person(first="John", last=peter.last) // or alternatively, we use constructor directly
    println(peter) // Person(Peter,Chen)
    println(john1) // Person(John,Chen)
    println(john2) // Person(John,Chen)

    // 9) Iterator.fill([size])([element]): returns an Iterator of elements with size
    val rand = new Random
    val iterator = Iterator.fill(5)(rand.nextInt() % 10)
    while (iterator.hasNext) {
      println(iterator.next()) // print 5 random Int
    }

    // 10) Seq.newBuilder[String]: returns a new mutable.ListBuffer & StringBuilder.newBuilder
    val seqBuilder = Seq.newBuilder[String]
    seqBuilder += "string1"
    seqBuilder += "string2"
    println(seqBuilder.result()) // List(string1, string2)

    val stringBuilder = StringBuilder.newBuilder
    stringBuilder.append("head")
    stringBuilder.append("|")
    stringBuilder.append("tail")
    println(stringBuilder.result()) // head|tail
  }
}
