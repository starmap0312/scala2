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

  // bad example: no abstracting over collection ops
  {
    trait Collection[A] {
      def item: A
      def map[B](f: A => B): Collection[B]
      override def toString = s"item=$item"
    }

    class Box[A](val item: A) extends Collection[A] {
      override def map[B](f: A => B): Box[B] = new Box[B](f(item)) // note: need to implement for every transformation method!!
    }

    val box: Box[Int] = new Box(1)
    val intBox: Box[Int] = box.map(_ + 1)
    val strBox: Box[String] = box.map("string " + _.toString)
    println(intBox) // item=2
    println(strBox) // item=string 1
  }

  // good example: abstracting over collection ops
  {
    trait CollectionOps[A, CC[_]] {
      def item: A
      def map[B](f: A => B): CC[B] = collectionFactory.from(f(item)) // note: shared, no need to implement for every transformation method!!
      // dedicated to a factory, map to collection type offered by the factory (type class)

      // the same-result-type principle: a transformation method on a collection should yield a collection of the same type
      def collectionFactory: CollectionFactory[CC] // used to produce the same Collection type, i.e. CC

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

  // good example: abstracting over map ops (extended to two type parameters K and V)
  {
    trait MapOps[K, V, CC[_, _]] { // define another trait with with two type parameters and the map function takes a tuple, instead of a value
      def item: (K, V)
      def map[K2, V2](f: (K, V) => (K2, V2)): CC[K2, V2] = collectionFactory.from(f(item._1, item._2))

      def collectionFactory: MapFactory[CC]

      override def toString = s"item=$item"
    }

    trait MapFactory[CC[_, _]] {
      def from[K2, V2](item: (K2, V2)): CC[K2, V2]
    }

    class Map[K, V](val item: (K, V)) extends MapOps[K, V, Map] {

      override def collectionFactory: MapFactory[Map] = new MapFactory[Map] {
        override def from[K2, V2](e: (K2, V2)): Map[K2, V2] = new Map(e)
      }
    }

    val aMap: Map[String, Int] = new Map("one", 1)
    val strMap: Map[String, String] = aMap.map((x, y) => (x, (y + 1).toString))
    println(aMap)   // item=(one,1)
    println(strMap) // item=(one,2)
  }
}
