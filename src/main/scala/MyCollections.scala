import scala.collection.{Iterable, IterableFactoryDefaults, IterableOnce, IterableOps, View, mutable}
// Architecture of Scala Collections:
//   https://docs.scala-lang.org/overviews/core/architecture-of-scala-213-collections.html
// Implementing Custom Collections:
//   https://docs.scala-lang.org/overviews/core/custom-collections.html

// 1) first version
// Collection transformation operations
trait BadIterable[A] {

  def map[B](f: A => B): BadIterable[B]
  // returns the same concrete collection type with a different type of elements, ex. map, flatMap, collect
  //   ex. map called on a List[Int] may return a List[String]
  // we need to abstract over the resulting `collection type constructor` (i.e. CC[_], ex. List[_])

  def filter(p: A => Boolean): BadIterable[A]
  // returns the same concrete collection type with the same type of elements, ex. filter, take, drop
  //   ex. called on a List[Int] returns a List[Int]
  // we need to abstract over the resulting `collection type` (i.e. C, ex. List[Int])
}

// 2) second version: abstracting over collection types
trait MyIterableOps[+A, +CC[_], +C] {
  // A: element type of the iterable
  // CC: collection type constructor
  // C: collection type

  def map[B](f: A => B): CC[B] // ex. List[B]
  // use iterableFactory for operations implementation, i.e. dedicate the implementation to a factory
  //  non-strict: = iterableFactory.from
  //  strict: = iterableFactory.newBuilder

  def filter(p: A => Boolean): C // ex. List[A]

  def iterableFactory: MyIterableFactory[CC]
}

trait MyIterableFactory[+CC[_]] {
  def from[A](source: IterableOnce[A]): CC[A] // non-strict
  def newBuilder[A]: mutable.Builder[A, CC[A]] // strict
}

abstract class MyBuilder[-A, +C](empty: C) {
  def addOne(elem: A): this.type
  def result(): C
  def ++= (xs: IterableOnce[A]): this.type
}

// Leaf collection types appropriately instantiate the type parameters
trait MyIterable[+A] extends  MyIterableOps[A, MyIterable, MyIterable[A]]// with IterableFactoryDefaults[A, MyIterable]

object MyCollections extends App {

  // bad example
  trait BadCollection[A] {
    def item: A
    def map[B](f: A => B): BadCollection[B]
    override def toString = s"item=$item"
  }

  class BadBox[A](val item: A) extends BadCollection[A] {
    override def map[B](f: A => B): BadBox[B] = new BadBox[B](f(item)) // note: need to implement for every transformation method!!
  }

  val badBox: BadBox[Int] = new BadBox(1)
  val badIntBox: BadBox[Int] = badBox.map(_ + 1)
  val badStrBox: BadBox[String] = badBox.map("string " + _.toString)
  println(badIntBox) // item=2
  println(badStrBox) // item=string 1

  // good example: abstracting over collection type (i.e. CC)
  trait CollectionOps[A, CC[_]] {
    def item: A
    def map[B](f: A => B): CC[B] = collectionFactory.from(f(item)) // note: shared, no need to implement for every transformation method!!
    // dedicated to a factory, map to collection type offered by the factory (type class)

    def collectionFactory: CollectionFactory[CC] // used to produce the same Collection type for map()
    override def toString = s"item=$item"
  }

  trait CollectionFactory[CC[_]] {
    def from[B](item: B): CC[B] // a factory that produces the same Collection type
  }

  class Box[A](val item: A) extends CollectionOps[A, Box] {

    override def collectionFactory: CollectionFactory[Box] = new CollectionFactory[Box] { // a factory that produces a Box
      override def from[B](e: B): Box[B] = new Box(e)
    }
  }

  val box: Box[Int] = new Box(1)
  val intBox: Box[Int] = box.map(_ + 1)
  val strBox: Box[String] = box.map("string " + _.toString)
  println(intBox) // item=2
  println(strBox) // item=string 1

}
