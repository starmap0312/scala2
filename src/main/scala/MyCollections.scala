import scala.collection.{Iterable, IterableFactoryDefaults, IterableOnce, IterableOps, View, mutable}
// Architecture of Scala Collections:
//   https://docs.scala-lang.org/overviews/core/architecture-of-scala-213-collections.html
// Implementing Custom Collections:
//   https://docs.scala-lang.org/overviews/core/custom-collections.html

// 1) first version
// Collection transformation operations
trait MyIterable1[A] {

  def map[B](f: A => B): MyIterable1[B]
  // returns the same concrete collection type with a different type of elements, ex. map, flatMap, collect
  //   ex. map called on a List[Int] may return a List[String]
  // we need to abstract over the resulting `collection type constructor` (i.e. CC[_], ex. List[_])

  def filter(p: A => Boolean): MyIterable1[A]
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
  println("hello")

}
