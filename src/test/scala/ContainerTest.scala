import scala.collection.LinearSeq
import scala.collection.mutable.ListBuffer

object ContainerTest {
  def main(args: Array[String]): Unit = {
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
    // 2.1) Seq.newBuilder: returns a ListBuffer
    val s3 = Seq.newBuilder[String]      // scala.collection.mutable.ListBuffer
    s3 += "1" += "two" += "3"
    println(s3)                          // ListBuffer(1, two, 3)
    println(s3.result())                 // List(1, two, 3)
  }
}
