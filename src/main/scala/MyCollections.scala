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
  trait Collection[A] {
    def map[B](f: A => B): Collection[B]
  }

  class BadBox[A](item: A) extends Collection[A] {
    override def map[B](f: A => B): BadBox[B] = new BadBox[B](f(item))

    override def toString = s"BadBox($item)"
  }

  val badBox: BadBox[Int] = new BadBox(1)
  val badIntBox: BadBox[Int] = badBox.map(_ + 1)
  val badStrBox: BadBox[String] = badBox.map(_.toString + " string")
  println(badIntBox) // BadBox(1)
  println(badStrBox) // BadBox(1 string)

  // better example: abstracting over collection types
  trait CollectionOps[A, CC[_]] {
    def map[B](f: A => B): CC[B] // map to another collection of the same type with possibly a different item
    def collectionFactory: CollectionFactory[CC] // used to produce the same Collection type for map()
  }

  trait CollectionFactory[CC[_]] {
    def from[B](item: B): CC[B] // a factory that produces the same Collection type
  }

  class Box[A](item: A) extends CollectionOps[A, Box] {

    override def map[B](f: A => B): Box[B] = collectionFactory.from(f(item)) // use the factory to produce the same Collection type with a different item

    override def collectionFactory: CollectionFactory[Box] = new CollectionFactory[Box] {
      override def from[B](e: B): Box[B] = new Box(e)
    }

    // or use another dedicated method: fromSpecific(f(item)) for map()
//    def fromSpecific[B](item: B): Box[B] = iterableFactory.from(item)

    override def toString = s"Box($item)"
  }

  val box: Box[Int] = new Box(1)
  val intBox: Box[Int] = box.map(_ + 1)
  val strBox: Box[String] = box.map(_.toString + " string")
  println(intBox) // Box(1)
  println(strBox) // Box(1 string)

}
