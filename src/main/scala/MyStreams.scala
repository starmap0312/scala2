// memoization of the Stream creates a structure much like List
// 1) if something is holding on to the head, the head holds on to the tail, and it continues recursively
//    you can very quickly eat up large amounts of memory
//    (ex. we use val to define the Stream)
// 2) if there is nothing holding on to the head, then once it is no longer being used directly it disappears
//    (ex. we used def to define the Stream)
// 3) some operations, ex. flatMap() or collect() may process a large number of intermediate elements before returning
//    these necessarily hold onto the head, since they are methods on Stream, and a stream holds its own head
//    therefore, for computations where memoization is not desired, use Iterator when possible

object MyStream {
  object Empty extends MyStream[Nothing] {
    override def isEmpty = true
    override def head = throw new NoSuchElementException("head of empty stream")
    override def tail = throw new UnsupportedOperationException("tail of empty stream")
  }

  def empty[A]: MyStream[A] = Empty

  // used to construct a Stream: ex. val stream = 1 #:: 2 #:: 3 #:: Stream.empty
  implicit def consWrapper[A](stream: => MyStream[A]): ConsWrapper[A] = { // implicit conversion method
    new ConsWrapper[A](stream)
  }

  class ConsWrapper[A](tl: => MyStream[A]) {
    def #::(hd: A): MyStream[A] = cons(hd, tl)
  }

  object #:: {  // an extractor, its unapply() is used to extract head and tail if Stream is not empty
    def unapply[A](xs: MyStream[A]): Option[(A, MyStream[A])] =
      if (xs.isEmpty) None
      else Some((xs.head, xs.tail))
  }

  object cons { // apply() is used to construct a Stream
    def apply[A](hd: A, tl: => MyStream[A]) = new Cons(hd, tl) // call-by-name, for lazy evaluation
    def unapply[A](xs: MyStream[A]): Option[(A, MyStream[A])] = #::.unapply(xs)
  }

  final class Cons[+A](hd: A, tl: => MyStream[A]) extends MyStream[A] {
    override def isEmpty = false
    override def head = hd                   // head value is instantiated and stored at the Stream construction
    private[this] var tlVal: MyStream[A] = _ // default value: MyStream[A] = null
    private[this] var tlGen = tl _           // convert expression into a Function0: i.e. () => MyStream[A]
    def tailDefined: Boolean = tlGen eq null // used to verify if the tail Stream has been accessed or not
    override def tail: MyStream[A] = {
      if (!tailDefined) { // the tail's Stream expression gets evaluated only when tail is NOT defined
        tlVal = tlGen()   // call the Function0 to get the expression evaluated
        tlGen = null      // nullify the Function0, as the Stream is already constructed and stored in tlVal
        //println(" (tail Stream is constructed)")
      }
      tlVal
    }
  }
}

abstract class MyStream[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: MyStream[A]

  private def asStream[B](x: AnyRef): MyStream[B] = x.asInstanceOf[MyStream[B]]
  private def append[B >: A](rest: => MyStream[B]): MyStream[B] = {
    if (isEmpty) {
      rest
    } else {
      MyStream.cons(head, tail append rest)
    }
  }

  // returns the n first elements of this Stream as another new Stream
  def take(n: Int): MyStream[A] = {
    if (n <= 0 || isEmpty) MyStream.empty
    else if (n == 1) MyStream.cons(head, MyStream.empty)
    else MyStream.cons(head, tail take n - 1)
  }

  final def foreach[U](f: A => U) {
    if (!this.isEmpty) {
      f(head)
      tail.foreach(f)
    }
  }

  final def map[B](f: A => B): MyStream[B] = {
      if (isEmpty) {
        MyStream.Empty
      } else {
        MyStream.cons(f(head), asStream[B](tail map f))
      }
  }

  final def flatMap[B](f: A => MyStream[B]): MyStream[B] = {
      if (isEmpty) {
        MyStream.Empty
      } else {
        var nonEmptyPrefix = this
        var prefix = f(nonEmptyPrefix.head)
        if (nonEmptyPrefix.isEmpty) {
          MyStream.empty
        } else {
          prefix append asStream[B](nonEmptyPrefix.tail flatMap f)
        }
      }
  }
}

object MyStreams {
  def main(args: Array[String]): Unit = {
    // 1) construct a Stream explicitly
    // 1.1) Stream.cons()
    val stream1: MyStream[Int] = MyStream.cons(1, MyStream.cons(2, MyStream.cons(3, MyStream.empty)))
    // the above constructs a Stream(1, ?), where ? = MyStream.cons(2, MyStream.cons(3, MyStream.empty)) not evaluated yet
    // 1.2) Stream.ConsWrapper.#::() as an constructor
    val stream2 = 1 #:: 2 #:: 3 #:: MyStream.empty
    // note that the line is read backwards by the compiler (because of :)
    // the following implicit conversions are happening behind the scene for the above expression
    val stream3 = MyStream.consWrapper( // head value 1 is stored but inner Stream is not yet evaluated (instantiated)
      MyStream.consWrapper(
        MyStream.consWrapper(
          MyStream.empty
        ).#::(3)
      ).#::(2)
    ).#::(1)
    // note: no tail is accessed when constructing a Stream, so it takes only O(1) space for head and
    //       the tail's Function0
    //       when tail's Function0 is called for the first time, the tail Stream is constructed and
    //       it can be referenced (memorized) via the tlVal field

    // 2) memorization of a Stream
    stream1 foreach  print            // 123 (tail Stream is constructed)
    println
    stream1 foreach  print            // 123
    println

    // 3) Stream.#::() as an extractor
    // 3.1) used in pattern matching
    stream1 match {
      case MyStream.#::(x, MyStream.cons(y, rest)) => {
        println(x, y, rest)           // (1,2,MyStream)
        println(rest.head, rest.tail) // (3,MyStream.Empty)
      }
    }

    // 3) map() and flatMap()
    println(stream1.map(_ * 2) foreach print)                                                // 246()
    println(stream1.flatMap((x: Int) => MyStream.cons(x * 2, MyStream.empty)) foreach print) // 246()

  }
}
