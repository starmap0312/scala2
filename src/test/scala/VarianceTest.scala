// 1) we should make a mutable container invariant, otherwise run-time error may occurs
//    ex. class Array[T](_length: Int) extends Serializable with Cloneable { ... }
//        (i.e. Array[T] does not relate to Array[T'] for T <: T')

// 2) let Scala perform contravariant and covariant checks at compile-time
//     with the checks, we are safe to replace wherever the supertype occurs with the subtype
//     (i.e. liskov substitution principle)
trait IntSet
class NonEmpty extends IntSet
object Empty extends IntSet

abstract class MyArray[-T, +R] {
  def update(x: T): MyArray[T, R]
  //def update(x: R)   // compile error: the variance check fails as the covariant type appears at method parameter
  def get(): R
  //def get(): T       // compile error: the variance check fails as the contravariant type appears at return result
}
// ex. MyArray[IntSet, NonEmpty] <: MyArray[IntSet, IntSet]

class MyArraryImpl[T, R](val e: R) extends MyArray[T, R] {
  override def update(x: T): MyArray[T, R] = {
    //this.e = x       // compile error: type T does not conform to expected type R
    this
  }
  override def get(): R = this.e
}
// the variance check helps to identify the above error at compile-time

// 3) let Scala refer the type bounds at compile-time
abstract class MyList[+R] {
  def update[U >: R](x: U): MyList[U]
  def get(): R
}
// ex. MyList[NonEmpty] <: MyList[IntSet]

class MyListImpl[T](val e: T) extends MyList[T] {
  override def update[U >: T](x: U): MyList[U] = {
    new MyListImpl(x)
  }
  override def get(): T = this.e
}


object VarianceTest {
  def main(args: Array[String]): Unit = {
    // 1) if Array is NOT invariant, it may lead to the following contradiction
    val a: Array[NonEmpty] = Array(new NonEmpty, new NonEmpty)
    //val b: Array[IntSet] = a // suppose we allow Array[NonEmpty] <: Array[IntSet] for NonEmpty <: IntSet
    //b(0) = Empty             // this is OK as if the update method is contravariant
    val c: NonEmpty = a(0)     // this is OK as if the get method is covariant
    // however, as Array does not check the contravariant and covariant
    //   it leads to a run-time error if we perform these operations directly
    //   so we should not make a mutable container variant

    // 3) type bounds
    val list1: MyList[NonEmpty] = new MyListImpl[NonEmpty](new NonEmpty)
    val e1: NonEmpty = list1.get()
    val list2: MyList[IntSet] = list1.update(Empty)
    val e2: IntSet = list2.get()
    //val list3: MyList[NonEmpty] = list1.update(Empty) // compile error: type MyList[IntSet] does not conform with expected type MyList[IntSet]


  }
}
