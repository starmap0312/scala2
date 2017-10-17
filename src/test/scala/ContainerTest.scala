import scala.collection.LinearSeq
import scala.collection.mutable.ListBuffer

object ContainerTest {
  def main(args: Array[String]): Unit = {
    // 1) TraversableLike operations:
    //    trait LinearSeq extends & trait LinearSeq extends trait SeqLike
    //    traitSeqLike extends trait TraversableLike
    // 1.1) ++ & ++:
    //      ++: returns the same collection type as the left side
    //      ++: returns the same collection type as the right side
    println(List(5) ++ Vector(5))  // List(5, 5)
    println(List(5) ++: Vector(5)) // Vector(5, 5)

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
    // 2.3) Seq.newBuilder: returns a ListBuffer
    val s3 = Seq.newBuilder[String]      // scala.collection.mutable.ListBuffer
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
    println(map1) // Map(one -> 1, two -> 2, three -> 3)
  }
}
