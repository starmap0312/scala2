package basics

// good practice: separate into collection class & operation class
trait MyCollection[+A] extends MyCollectionOps[A, MyCollection] { // +A: element type
  def element: A
}
// MyCollection extends MyCollectionOps, so it needs to implement the map operation which returns another MyCollection of a possibly different type B

trait MyCollectionOps[+A, +CC[_]] { // +CC[_]: constructor (collection) type, this specifies that the generic type CC contains a type parameter (like a collection)
  def map[B](f: A => B): CC[B]
}

case class MyCollectionImpl[+A](element: A) extends MyCollection[A] {
  override def map[B](f: A => B): MyCollection[B] = MyCollectionImpl(f(element))
}

// bad practice: a single class containing both the definition of the collection and its operations
trait MyCollectionWithOps[+A, +CC[_]] {
  def element: A
  def map[B](f: A => B): CC[B]
}

// it's not easy to implement the above collection class with operations
//case class MyCollectionImpl2[A](element: A) extends MyCollectionWithOps[A, ({ type L[a] = MyCollectionImpl2[a] })#L] {
//  override def map[B](f: A => B): MyCollectionImpl2[B] = MyCollectionImpl2(f(element))
//}

object CollectionOps extends App {
  //  type is erased when using the copy method of Context and we had a static constructor type
  val col1: MyCollectionImpl[String] = MyCollectionImpl("1")
  println(col1) // MyCollectionImpl(2)
  val col2: MyCollection[Int] = col1.map(e => (e.toInt + 1))
  println(col2) // MyCollectionImpl(2)

}
