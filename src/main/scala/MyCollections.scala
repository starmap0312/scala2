import scala.collection.View
// Architecture of Scala Collections:
//   https://docs.scala-lang.org/overviews/core/architecture-of-scala-213-collections.html

// Collection transformation operations
trait MyList1[A] {
  // transformation operations, ex. map, filter, zip, etc.
  // return a collection type B that are unknown yet
  def map[B](f: A => B): MyList1[B]
  // returns the same concrete collection type with a different type of elements
  //   ex. called on a List[Int] may return a List[String]
  // we need to abstract over the resulting `collection type constructor`

  def filter(p: A => Boolean): MyList1[A]
  // returns the same concrete collection type with the same type of elements
  //   ex. called on a List[Int] returns a List[Int]
  // we need to abstract over the resulting `collection type`
}

// Abstracting over collection types
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
  def newBuilder[A]: MyBuilder[A, CC[A]] // strict
}

trait MyBuilder[-A, +C] {
  def addOne(elem: A): this.type
  def result(): C
}

trait MyIterable[+A]

// Leaf collection types appropriately instantiate the type parameters
trait MyList2[+A] extends MyIterable[A] with MyIterableOps[A, MyList2, MyList2[A]]

//trait MyMap1[K, +V] extends MyIterable[(K, V)]  with MyIterableOps[(K, V), MyMap1, MyMap1[K, V]] // error
// note: we have compile error as Map[K, V] takes two type parameters, unlike Iterable[A] that takes only one type parameter
//   Type constructor mismatch. Required: CC[_], Found: MyMap1[K, V]
// i.e. CC[_] type parameter of the IterableOps trait takes one type parameter whereas Map[K, V] takes two type parameters
// so we have to define another template trait MapOps to support collection types constructors with two types parameters
trait MyMapOps[K, +V, +CC[X, +Y] <: MyMapOps[X, Y, CC, _], +C <: MyMapOps[K, V, CC, C]] extends MyIterableOps[(K, V), MyIterable, C] {

  def map[K2, V2](f: ((K, V)) => (K2, V2)): CC[K2, V2]
  // returns the same concrete collection type with a different type of elements
  //   ex. called on a Map[String, Int] may return a Map[String, String]
  // note: we have name clash between defined (MapOps) and inherited (IterableOps) member
  //   from MapOps:
  //     def map[K2, V2](f: ((K, V)) => (K2, V2)): Map[K2, V2]
  //   from IterableOps:
  //     def map[B](f: ((K, V)) => B): Iterable[B]
  // same-result-type principle: wherever possible a transformation method on a collection yields a collection of the same type
  //   if the argument function returns a pair, the version from MapOps is used (as it's more specific), and the resulting collection is a Map
  //   if the argument function does not return a pair, the version from IterableOps is used, and the resulting collection is an Iterable

//  def filter(p: A => Boolean): C
  // returns the same concrete collection type with the same type of elements
  //   ex. called on a Map[String, Int] returns a Map[String, Int]
}

// so Map[K, V] can extend this trait and appropriately instantiate its type parameters:
trait MyMap[K, +V] extends MyIterable[(K, V)] with MyMapOps[K, V, MyMap, MyMap[K, V]]
// vs. trait MyList[+A] extends MyIterable[A] with MyIterableOps[A, MyList2, MyList2[A]]

object MyCollections extends App {
  println("hello")

}
