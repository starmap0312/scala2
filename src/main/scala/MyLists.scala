// List comes with two implementing case classes:
// 1) scala.Nil
// 2) scala.:: that implement the abstract members isEmpty, head and tail
object MyList {
  def empty[A]: MyList[A] = MyNil
  def apply[A](xs: A*): MyList[A] = {
    if (xs.isEmpty) { // my own implementation, the original delegates the construction to other objects
      MyNil
    } else {
      val h = new ::[A](xs.head, MyNil)
      var t: ::[A] = h
      var rest = xs.tail
      while (rest.nonEmpty) {
        val nx = new ::(rest.head, MyNil)
        t.tl = nx
        t = nx
        rest = rest.tail
      }
      h
    }
  }
}

abstract class MyList[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: MyList[A]
  final def map[B](f: A => B): MyList[B] = {
    if (this eq MyNil) {
      MyNil
    } else {
      val h = new ::[B](f(head), MyNil)
      var t: ::[B] = h
      var rest = tail
      while (rest ne MyNil) {
        val nx = new ::(f(rest.head), MyNil)
        t.tl = nx
        t = nx
        rest = rest.tail
      }
      h
    }
  }

  def foreach[U](f: A => U) = {
    var these = this
    while (!these.isEmpty) {
      f(these.head)
      these = these.tail
    }
  }

  final def flatMap[B](f: A => MyList[B]): MyList[B] = {
    if (this eq MyNil) {
      MyNil
    } else {
      var rest = this
      var found = false // found head or not
      var h: ::[B] = null
      var t: ::[B] = null
      while (rest ne MyNil) {
        f(rest.head) foreach { b =>
          if (!found) { // reads the head of list
            h = new ::(b, MyNil)
            t = h
            found = true
          } else {
            val nx = new ::(b, MyNil)
            t.tl = nx
            t = nx
          }
        }
        rest = rest.tail
      }
      (if (!found) MyNil else h)
    }
  }
}

case object MyNil extends MyList[Nothing] {
  def isEmpty = true
  def head: Nothing = throw new NoSuchElementException("head of empty list")
  def tail: MyList[Nothing] = throw new UnsupportedOperationException("tail of empty list")
  override def equals(that: Any) = that match {
    case x: scala.collection.GenSeq[_] => x.isEmpty
    case _ => false
  }
}

final case class ::[B](override val head: B, var tl: MyList[B]) extends MyList[B] {
  override def isEmpty: Boolean = false
  override def tail : MyList[B] = tl
}

object MyLists {
  def main(args: Array[String]): Unit = {
    MyList(1, 2, 3).map(_ * 2) foreach print                     // 246
    println
    MyList(1, 2, 3).flatMap(x => MyList(x, x + 1)) foreach print // 122334
  }
}
