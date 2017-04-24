object MyStream {
  object Empty extends MyStream[Nothing] {
    override def isEmpty = true
    override def head = throw new NoSuchElementException("head of empty stream")
    override def tail = throw new UnsupportedOperationException("tail of empty stream")
  }

  object #:: {
    def unapply[A](xs: MyStream[A]): Option[(A, MyStream[A])] =
      if (xs.isEmpty) None
      else Some((xs.head, xs.tail))
  }

  object cons {
    def apply[A](hd: A, tl: => MyStream[A]) = new Cons(hd, tl) // call-by-name, lazy evaluation
    def unapply[A](xs: MyStream[A]): Option[(A, MyStream[A])] = #::.unapply(xs)
  }

  final class Cons[+A](hd: A, tl: => MyStream[A]) extends MyStream[A] {
    override def isEmpty = false
    override def head = hd
    private[this] var tlVal: MyStream[A] = _
    private[this] var tlGen = tl _
    def tailDefined: Boolean = tlGen eq null
    override def tail: MyStream[A] = {
      if (!tailDefined)
        synchronized {
          if (!tailDefined) {
            tlVal = tlGen()
            tlGen = null
          }
        }
      tlVal
    }
  }
}

abstract class MyStream[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: MyStream[A]
  //def map[B](f: A => B): MyStream[B]
  //def flatMap[B](f: A => MyStream[B]): MyStream[B]
  //def foreach[U](f: A => U): Unit

}

object MyStreams {
  def main(args: Array[String]): Unit = {

  }
}
