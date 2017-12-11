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
//                         -> Seq -> IndexedSeq -> String / Vector / Range / NumericRange
//                                -> LinearSeq  -> List   / Stream / Queue / Stack
// Traversable -> Iterable -> Set -> SortedSet  -> TreeSet
//                                -> BitSet
//                                -> HashSet
//                                -> ListSet
//                         -> Map -> SortedMap  -> TreeMap
//                                -> HashMap
//                                -> ListMap
// 3) scala Array (i.e. Java Array) vs. ArrayBuffer (scala.collection.mutable.ArrayBuffer)
// both are are mutable:
//   you can modify elements at particular indexes: a(i) = e
// ArrayBuffer is resizable, Array isn't
//   if you append an element to an ArrayBuffer, it gets larger
//   if you try to append an element to an Array, you get a new array (so it is better to know the Array's size beforehand)


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
    // 4.1) +
    val map1 = Map("one" -> 1, "two" -> 2) + ("three" -> 3)
    println(map1)       // Map(one -> 1, two -> 2, three -> 3)
    // 4.2) mutable.Map
    val mutableMap = mutable.Map[String, Int]()
    mutableMap.put("one", 1)
    mutableMap.put("two", 2)
    println(mutableMap) // Map(one -> 1, two -> 2)
  }
}
