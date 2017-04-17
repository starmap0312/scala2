/*
abstract class MyList[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: MyList[A]
  final def map[B](f: A => B): MyList[B] = {
      if (isEmpty) {
        MyNil
      } else {
        val h = new ::[B](f(head), Nil)
        var t: ::[B] = h
        var rest = tail
        while (rest ne Nil) {
          val nx = new ::(f(rest.head), Nil)
          t.tl = nx
          t = nx
          rest = rest.tail
        }
        h.asInstanceOf[That]
      }
    else {
      def builder = { // extracted to keep method size under 35 bytes, so that it can be JIT-inlined
      val b = bf(repr)
        b.sizeHint(this)
        b
      }
      val b = builder
      for (x <- this) b += f(x)
      b.result
    }
  }

  final def flatMap[B](f: A => MyList[B]): MyList[B] = {
    if (bf eq List.ReusableCBF) {
      if (this eq Nil) Nil.asInstanceOf[That] else {
        var rest = this
        var found = false
        var h: ::[B] = null
        var t: ::[B] = null
        while (rest ne Nil) {
          f(rest.head).seq.foreach{ b =>
            if (!found) {
              h = new ::(b, Nil)
              t = h
              found = true
            }
            else {
              val nx = new ::(b, Nil)
              t.tl = nx
              t = nx
            }
          }
          rest = rest.tail
        }
        (if (!found) Nil else h).asInstanceOf[That]
      }
    }
    else {
      def builder = bf(repr) // extracted to keep method size under 35 bytes, so that it can be JIT-inlined
      val b = builder
      for (x <- this) b ++= f(x).seq
      b.result
    }
  }
}

case object MyNil extends MyList[Nothing] {
  def isEmpty = true
  def head: Nothing = throw new NoSuchElementException("head of empty list")
  def tail: List[Nothing] = throw new UnsupportedOperationException("tail of empty list")
}
*/
object MyLists {
  def main(args: Array[String]): Unit = {
    //List(1, 2, 3).foldLeft()
    //List(1, 2, 3).reduceLeft()
    println(List(Option(1)).flatten)
    augmentString("123").toInt
  }
}
