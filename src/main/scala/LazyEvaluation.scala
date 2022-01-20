import scala.annotation.tailrec

// implement lazy list in Scala

abstract class Stream[A] {
  def head: A
  def tail: Stream[A]
  def isEmpty: Boolean

  def exists(p: A => Boolean): Boolean = exists(p, this)

  @tailrec
  private def exists(p: A => Boolean, s: Stream[A]): Boolean = if (s.isEmpty) false else (if (p(s.head)) true else exists(p, s.tail))

  def drop(n: Int): Stream[A] = drop(n, this)

  @tailrec
  private def drop(n: Int, acc: Stream[A]): Stream[A] = if (n <= 0) acc else drop(n - 1, acc.tail)
}

object Stream {
  val empty = new Empty()

  class Empty[A] extends Stream[A] {
    override def isEmpty: Boolean = true
    lazy val head: A = throw new IllegalStateException("head called on empty")
    lazy val tail: Stream[A] = throw new IllegalStateException("tail called on empty")
  }

  class Cons[A](hd: => A, tl: => Stream[A]) extends Stream[A] { // lazy evaluation: call-by-name
    override def isEmpty: Boolean = false
    lazy val head: A = hd
    lazy val tail: Stream[A] = tl
  }

  def cons[A](hd: => A, tl: => Stream[A]): Stream[A] = new Cons(hd, tl)

  def from(i: Int): Stream[Int] = cons(i, from(i + 1))
}

object LazyEvaluation extends App {

  def isLarge(num: Int) = {
    println(s"isLarge: $num")
    if (num == 10) true else false
  }

  val stream = Stream.from(1) // infinite stream
  // lazy evaluation
  println(stream.head) // 1
  println(stream.tail.head) // 2

  val hasLarge = stream.exists(isLarge)
  println(hasLarge) // isLarge: 1, ..., isLarge: 10, true
}
