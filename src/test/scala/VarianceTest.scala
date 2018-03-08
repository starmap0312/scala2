// we should make a mutable container invariant, otherwise run-time error may occurs
//   ex. class Array[T](_length: Int) extends Serializable with Cloneable { ... }
//       (i.e. Array[T] does not relate to Array[T'] for T <: T')

// 1) use contravariant and covariant type check at compile-time
//     with the checks, we are safe to replace wherever the supertype occurs with the subtype
//     (i.e. liskov substitution principle)
trait IntSet
class NonEmpty extends IntSet
object Empty extends IntSet

// if we want an Array to be variant, i.e. Array[NonEmpty] <: Array[IntSet],
// then we should make sue its methods pass variant checks:
//   update() method is contravariant, so that the subclass can perform the update() method where the superclass does
//   get() method is covariant       , so that the subclass can perform the get() method where the superclass does
// ex.
//   val arr1: Array[NonEmpty] = Array(new NonEmpty)
//   val arr2: Array[IntSet]   = arr1 // suppose we want Array[NonEmpty] <: Array[IntSet] for NonEmpty <: IntSet
//   arr2.update(Empty)               // this is OK if the update method is contravariant
//   val e2: IntSet = arr2.get()      // this is OK if the get method is covariant
// however, this can lead to contradiction if the Array is mutable
//   val e1: NonEmpty = arr1.get()    // the element is updated to Empty, which is NOT NonEmpty

// the contravariant and covariant checks make sure that the up-casting is safe
// moreover, it helps to identify the above contradiction at compile time
abstract class MutableArray[-T, +R] {
  def update(x: T): MutableArray[T, R] // we want the update method to be contravariant
  //def update(x: R)                   //   get compile error: the variance check fails as the covariant type appears at method parameter
  def get(): R                         // we want the get method to be covariant
  //def get(): T                       //   get compile error: the variance check fails as the contravariant type appears at return result
}
// ex. MutableArray[IntSet, NonEmpty] <: MutableArray[IntSet, IntSet]

class MutableArrayImpl[T, R](val e: R) extends MutableArray[T, R] {
  override def update(x: T): MutableArray[T, R] = {
    //this.e = x       // the variance check helps to identify the contradiction of mutability at compile-time
    this               //   get compile error: type T does not conform to expected type R
  }
  override def get(): R = this.e
}

// 2) use type bound check at compile-time
abstract class ImmutableArray[+R] {
  def update[U >: R](x: U): ImmutableArray[U]
  def get(): R
}
// ex. ImmutableArray[NonEmpty] <: ImmutableArray[IntSet]

class ImmutableArrayImpl[T](val e: T) extends ImmutableArray[T] {
  override def update[U >: T](x: U): ImmutableArray[U] = {
    new ImmutableArrayImpl[U](x)
  }
  override def get(): T = this.e
}


object VarianceTest {
  def main(args: Array[String]): Unit = {
    // 1) use contravariant and covariant type check at compile time
    val list1: MutableArray[IntSet, NonEmpty] = new MutableArrayImpl[IntSet, NonEmpty](new NonEmpty)
    val e1: NonEmpty = list1.get()
    val list2: MutableArray[IntSet, IntSet] = list1.update(Empty)
    val e2: IntSet = list2.get()

    // the variant checks make sure the following issue at compile time
    val list3: MutableArray[NonEmpty, NonEmpty] = new MutableArrayImpl[NonEmpty, NonEmpty](new NonEmpty)
    val e3: NonEmpty = list3.get()
    // val list3: MutableArray[IntSet, IntSet] = list1.update(Empty) // NOT Allowed
    //   get compile error (type mismatch: expected NonEmpty, actual: Empty)

    // 2) use type bound type check at compile time
    val list4: ImmutableArray[NonEmpty] = new ImmutableArrayImpl[NonEmpty](new NonEmpty)
    val e4: NonEmpty = list4.get()
    val list5: ImmutableArray[IntSet] = list4.update(Empty)
    val e5: IntSet = list5.get()
    //val list5: ImmutableArray[NonEmpty] = list4.update(Empty) // NOT Allowed
    //   get compile error (type ImmutableArray[IntSet] does not conform with expected type ImmutableArray[IntSet])

  }
}
