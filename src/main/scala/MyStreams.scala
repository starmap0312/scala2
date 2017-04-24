

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
    override def head = hd
    private[this] var tlVal: MyStream[A] = _ // default value: MyStream[A] = null
    private[this] var tlGen = tl _           // a value must has a type
                                             // so we convert the expression into a Function0: i.e. () => MyStream[A]
    override def tail: MyStream[A] = {
      tlVal = tlGen()                        // call the Function0 to get the expression evaluated when tail is accessed
      tlVal
    }
  }
}

abstract class MyStream[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: MyStream[A]

  private def asStream[B](x: AnyRef): MyStream[B] = x.asInstanceOf[MyStream[B]]
  def append[B >: A](rest: => MyStream[B]): MyStream[B] = {
    if (isEmpty) {
      rest
    } else {
      MyStream.cons(head, tail append rest)
    }
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
  //def foreach[U](f: A => U): Unit

}

object MyStreams {
  def main(args: Array[String]): Unit = {
    val stream1: MyStream[Int] = MyStream.cons(1, MyStream.cons(2, MyStream.cons(3, MyStream.empty)))
    val stream2 = 1 #:: 2 #:: 3 #:: MyStream.empty
    // for the above, implicit conversions are happening behind the scene
    // note that the line is read backwards by the compiler (because of :)
    val stream3 = MyStream.consWrapper(
      MyStream.consWrapper(
        MyStream.consWrapper(MyStream.empty).#::(3)
      ).#::(2)
    ).#::(1)
    stream1 match {
      case MyStream.#::(x, MyStream.cons(y, rest)) => {
        println(x, y, rest)           // (1,2,MyStream)
        println(rest.head, rest.tail) // (3,MyStream.Empty)
      }
    }
    println(stream2.map(_ * 2) foreach print)                                                // 246()
    println(stream3.flatMap((x: Int) => MyStream.cons(x * 2, MyStream.empty)) foreach print) // 246()
  }
}
