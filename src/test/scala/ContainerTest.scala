// all collections in package scala.collection
// 0) a collection in package scala.collection can be either mutable or immutable
//    the root collections in package scala.collection define the same interface as the immutable collections
//    the mutable collections in package scala.collection.mutable add side-effecting modification operations to the immutable interface
//    ex. scala.collection.mutable.IndexedSeq[T]   extends collection.IndexedSeq[T]
//        scala.collection.immutable.IndexedSeq[T] extends collection.IndexedSeq[T]
// 1) scala.collection.mutable:
//                         -> Seq -> IndexedSeq
//                                -> LinearSeq
// Traversable -> Iterable -> Set -> SortedSet
//                                -> BitSet
//                         -> Map -> SortedMap
//
// 2) scala.collection.immutable:
//                         -> Seq -> IndexedSeq -> "Vector" / Range / "NumericRange" (java "String", scala "Array")
//                                -> LinearSeq  -> "List"   / Stream / Queue / Stack
// Traversable -> Iterable -> Set -> SortedSet  -> TreeSet
//                                -> BitSet
//                                -> "HashSet"
//                                -> "ListSet"
//                         -> Map -> SortedMap  -> "TreeMap"
//                                -> "HashMap"
//                                -> "ListMap"
// 3) scala Array (i.e. Java Array) vs. ArrayBuffer (scala.collection.mutable.ArrayBuffer)
// both are are mutable:
//   you can modify elements at particular indexes: a(i) = e
// ArrayBuffer is resizable, Array isn't
//   if you append an element to an ArrayBuffer, it gets larger
//   if you try to append an element to an Array, you get a new array (so it is better to know the Array's size beforehand)
// 4) Java Array (ex. int[]) vs. Scala Array (mutable, ex. Array[Int])
// 4.1) Java Array is often avoided because of its general incompatibility with generics:
//      Java Array is a co-variant collection, whereas generics is invariant
//      Java Array is mutable which makes its co-variance a danger
//      Java Array accepts primitives where generics DO NOT
//      Java Array has a pretty limited set of methods
// 4.2) Scala Array is invariant
//      Scala accepts AnyVal (the equivalent of Java primitives) as types for its generics
//      All of Seq methods are available to Scala Array
//      Scala Array should perform as fast as a Java int[]
// 5) Scala ArrayBuffer (mutable) vs. Java ArrayList (vs. Java LinkedList)
// 5.1) scala.collection.mutable.ArrayBuffer == Java ArrayList
// 6) Scala List (immutable) vs. Scala Vector (vs. Scala Array: mutable)
// 6.1) scala.collection.immutable.List  : an immutable recursive data structure
//      scala.collection.immutable.Vector: an immutable fast random access structure
//      Scala: List vs. Vector               == Java: LinkedList vs. ArrayList
// 7) scala Seq interface                    == Java List interface
// 8) scala.collection.immutable.List vs. scala.collection.immutable.Vector
//    two implementations of Seq (sequence)
// 8.1) List is a recursive tree structure of Cons cells, accessing an element by index takes O(n) time
//        so List is more suitable for recursive operation of it
//        (traversing a List recursively takes constant time for each visit)
//      Vector is a balanced tree structure, each is a 32-element arrays, accessing an element takes O(log n) time
//        so performing bulk operations, ex. map, fold, filter, on a Vector is more efficient
//        (as consecutive many elements reside on same cache line)
// 9) scala.collection.Map extends MapLike extends PartialFunction
//    therefore, a Map[K, V] is also a Function[K, V], which has andThen(), etc. methods
//    ex. Map(1 -> "one", 2 -> "two")(1)     == "one" (String)
//        Map(1 -> "one", 2 -> "two")(3) throws java.util.NoSuchElementException: key not found
//        Map(1 -> "one", 2 -> "two").get(3) == None  (Option[String], get() is declared in trait MapLike)

import scala.collection.{LinearSeq, mutable}

// IterableLike extends TraversableLike
object ContainerTest {
  def main(args: Array[String]): Unit = {
    // 1) TraversableLike operations:
    //    trait LinearSeq extends & trait LinearSeq extends trait SeqLike
    //    trait SeqLike extends trait TraversableLike
    //    trait IterableLike extends TraversableLike
    // 1.1) ++ & ++:
    //      ++: returns the same collection type as the left side
    //      ++: returns the same collection type as the right side
    println(List(5) ++ Vector(5))  // List(5, 5)
    println(List(5) ++: Vector(5)) // Vector(5, 5)
    // 1.2) exists(p: A => Boolean)
    //      checks if there exists an element in the TraversableLike container that satisfies the predicate p
    println(List("one", "two", "three").exists((element: String) => element.equals("two"))) // true
    // 1.3) partition(p: A => Boolean): (Repr, Repr)
    //      partition the container into two sub-containers based on truth or false of the predicate
    val (evens, odds) = List(1, 2, 3, 4) partition { _ % 2 == 0}
    println(evens) // List(2, 4)
    println(odds)  // List(1, 3)

    //// 2) Seq
    // 2.1) trait IndexedSeq:
    //      fast random-access of elements and a fast length operation, i.e. Vector
    val s1 = IndexedSeq("1", "two", "3")
    println(s1)                          // Vector(1, two, 3)
    println(s1(1))                       // two
    // 2.2) trait LinearSeq:
    //      fast access only to the first element via head, but also has a fast tail operation, i.e. List
    //      default: default implementation of a Seq is a List
    val s2 = LinearSeq("1", "two", "3")
    println(s2)                          // List(1, two, 3)
    println(s2.tail)                     // List(two, 3)
    // 2.3) Seq.newBuilder: returns a mutable.Builder
    val s3: mutable.Builder[String, Seq[String]] = Seq.newBuilder[String]      // mutable.Builder[String, Seq[String]]
    s3 += "1" += "two" += "3"
    println(s3)                          // ListBuffer(1, two, 3)
    println(s3.result())                 // List(1, two, 3)

    // 3) List operations:
    // 3.1) +: or :+   ==> append to list head
    println("a" +: Nil)        // List(a)
    println(Nil :+ "a")        // List(a)
    // 3.2) ++ or :::  ==> concatenate two lists
    println(List("a") ++ Nil)  // List(a)
    println(List("a") ::: Nil) // List(a)
    // 3.3) :: or cons ==>  construct a new List by putting the left-hand side to the head
    println(List("a") :: Nil)  // List(List(a))

    // 4) Map operations:
    // 4.1) map + tuple:
    //      add an element to a Map
    val map1 = Map("one" -> 1, "two" -> 2) + ("three" -> 3)
    println(map1)       // Map(one -> 1, two -> 2, three -> 3)
    // 4.2) map1 ++ map2:
    //      add/override elements of map2 to map1
    val mergedMap1 = Map("one" -> 1, "two" -> 2) ++ Map("two" -> -3, "three" -> 3)
    val mergedMap2 = Map("two" -> -3, "three" -> 3) ++ Map("one" -> 1, "two" -> 2)
    println(mergedMap1) // Map(one -> 1, two -> -3, three -> 3)
    println(mergedMap2) // Map(two -> 2, three -> 3, one -> 1)
    // 4.3) mutable.Map
    val mutableMap = mutable.Map[String, Int]()
    mutableMap.put("one", 1)
    mutableMap.put("two", 2)
    println(mutableMap) // Map(one -> 1, two -> 2)
    // 4.4) map.get([key]): returns an Option (i.e. Some/None)
    Map(1 -> "one", 2 -> "two").get(3) match {
      case Some(x) => println(x)
      case None => println("Key not found")
    } // Key not found
    // 4.5) map.groupBy([Function[Pair, T]])
    Map(1 -> "one", 2 -> "two", 3 -> "three").groupBy( // Map(1 -> Map(1 -> one, 3 -> three), 0 -> Map(2 -> two))
      (pair: (Int, String)) => pair._1 % 2
    ).map(
      (pair: (Int, Map[Int, String])) => pair._1 -> pair._2.values.toList
    ) // Map(1 -> List(one, three), 0 -> List(two))
    // 4.6) map.withDefaultValue(): returns a wrapper of the map with a default value
    //      note: get(), contains(), iterator(), keys(), etc are not affected by `withDefaultValue
    (Map(1 -> "one", 2 -> "two") withDefaultValue "default")(3) // default
    //(Map(1 -> "one", 2 -> "two"))(3)                          // throws java.util.NoSuchElementException: key not found: 3

    // 6) trait Seq[+A] extends PartialFunction[Int, A]
    //    scala.collection.Seq is a PartialFunction: (Int => A) that maps an (index: Int) to elements of type A
    //    scala.collection.immutable.Seq extends scala.collection.Seq
    //    scala.collection.mutable.Seq   extends scala.collection.Seq
    val seq = Seq(1, 2, 3) andThen (_ + 1) // Seq has andThen() method, as it is a Function1
    println(seq(0), seq(1), seq(2))        // (2, 3, 4)

    def func[T](x: T) = x
    println(func(1))
  }
}
