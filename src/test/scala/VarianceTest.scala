// we should make a mutable container invariant, otherwise run-time error may occurs
//   ex. class Array[T](_length: Int) extends Serializable with Cloneable { ... }
//       (i.e. Array[T] does not relate to Array[T'] for T <: T')

class Fruit
class Apple extends Fruit
class AppleX extends Apple
object AppleY extends Apple

// 1) the contravariant and covariant type check at compile-time
//     with the checks, we are safe to replace wherever the supertype occurs with the subtype
//     (i.e. liskov substitution principle)
// if we specify an Array to be covariant, i.e. Array[AppleX] <: Array[Apple],
// then the type parameter can only appear as method return values, not appear as method parameters
// ex.
// class Array[+T] {
//  def update(x: T) = ??? // compile error: covariant type T occurs in parameters
// }                       // so it fails to satisfy liskov substitution principle
//
// therefore, we should make sure its methods pass variant checks:
//   update() method is contravariant, so that the subclass can perform the update() method where the superclass does
//   get() method is covariant       , so that the subclass can perform the get() method where the superclass does
// ex.
//   val arr1: Array[AppleX] = Array(new AppleX)
//   val arr2: Array[Apple]   = arr1 // suppose we want Array[AppleX] <: Array[Apple] for AppleX <: Apple
//   arr2.update(AppleY)             // this is OK if the update method is contravariant
//   val e2: Apple = arr2.get()      // this is OK if the get method is covariant
// however, as the Array is mutable, so this can lead to contradiction at run-time
// ex.
//   val e1: AppleX = arr1.get()     // the element is updated to AppleY, which is NOT AppleX

// the compiler helps with the variance checks for covariant and contravariant types
abstract class MutableArray[-T, +R] {
  def update(x: T): MutableArray[T, R] // we want the update method to be contravariant
  //def update(x: R)                   //   not allowed: we get compile error as the variance check fails as the covariant type appears at method parameter
  def get(): R                         // we want the get method to be covariant
  //def get(): T                       //   not allowed: we get compile error: the variance check fails as the contravariant type appears at return result
}
// note: MutableArray[Apple, AppleX] is a subtype of MutableArray[Apple, Apple]

// the compiler helps to identify the contradiction of defining a mutable array at compile time
class MutableArrayImpl[T, R](var e: R) extends MutableArray[T, R] {
  override def update(x: T): MutableArray[T, R] = {
//    this.e = x       // not allowed: T and R has no relationship
    this               //   get compile error: type T does not conform to expected type R
  }
  override def get(): R = this.e
}

// 2) the type bound check at compile-time
abstract class ImmutableArray[+R] {
  def update[U >: R](x: U): ImmutableArray[U]
  def get(): R
}
// ex. ImmutableArray[AppleX] <: ImmutableArray[Apple]

class ImmutableArrayImpl[R](val e: R) extends ImmutableArray[R] {
  override def update[U >: R](x: U): ImmutableArray[U] = {
    new ImmutableArrayImpl[U](x)
  }
  override def get(): R = this.e
}

object VarianceTest {
  def main(args: Array[String]): Unit = {
    // 1) contravariant and covariant type check at compile time
    val list1: MutableArray[Apple, AppleX] = new MutableArrayImpl[Apple, AppleX](new AppleX)
    val e1: AppleX = list1.get()
    val list1u: MutableArray[Apple, AppleX] = list1.update(AppleY)     // Allowed
    val list1v: MutableArray[Apple, AppleX] = list1.update(new AppleX) // Allowed
    val list1w: MutableArray[Apple, AppleX] = list1.update(new Apple)  // Allowed
//    val list1x: MutableArray[Apple, AppleX] = list1.update(new Fruit)  // Not Allowed
    //  compile error: type mismatch expected: Apple, found: Fruit

    val list2: MutableArray[Apple, Apple]  = list1                        // Allowed: up-cast to super type
    val list2u: MutableArray[Apple, Apple] = list2.update(AppleY)          // Allowed
    val e2: Apple = list2.get()                                            // Allowed

    // the variant checks make sure to identify the following issue at compile time
    val list3: MutableArray[AppleX, AppleX] = new MutableArrayImpl[AppleX, AppleX](new AppleX)
    val e3: AppleX = list3.get()
    //val list4: MutableArray[Apple, Apple] = list3                       // NOT Allowed
    // compile error: MutableArray[AppleX, AppleX] does not conform to expected type MutableArray[Apple, Apple]
    // list4.update(AppleY)  // the up-cast is NOT Allowed because the subtype is not able to perform the supertype operation

    // 2) type bound type check at compile time
    val list5: ImmutableArray[AppleX] = new ImmutableArrayImpl[AppleX](new AppleX)
    val e5: AppleX = list5.get()
    val list5u: ImmutableArray[Apple] = list5.update(AppleY)     // Allowed
    val list5v: ImmutableArray[Fruit]    = list5.update(AppleY)     // Allowed
    val list5w: ImmutableArray[Object] = list5.update(AppleY)     // Allowed
    val list5x: ImmutableArray[Apple] = list5.update(new Apple)// Allowed
    val list5y: ImmutableArray[Fruit]    = list5.update(new Fruit)   // Allowed
    val list5z: ImmutableArray[Object] = list5.update(new Fruit)   // Allowed
    //val list5u: ImmutableArray[AppleX] = list5.update(AppleY) // NOT Allowed
    //  compile error: type ImmutableArray[Apple] does not conform with expected type ImmutableArray[Apple]

    val list6: ImmutableArray[Apple]  = list5                   // Allowed: the up-cast is Allowed because the subtype is able to perform the supertype operation
    val e6: Apple = list6.get()
    val list6u: ImmutableArray[Fruit]    = list6.update(new Fruit)   // Allowed: as Set >: Apple >: AppleX, so it still conform with the type bound
    //val list6u: ImmutableArray[Apple] = list6.update(new Set) // NOT Allowed
    //  compile error: type ImmutableArray[Set] does not conform with expected type ImmutableArray[Apple]


    // other examples
    // example1
    class Box[+A](element: A) { // it is safe to be covariant due to its immutability
      def get(): A = element
      def set[B >: A](elem: B): Box[B] = new Box(elem) // ok, as we can use a Box[Apple] as a Box[Fruit] which should accept all supertypes of B >: Fruit (was B >: Apple)
      // def set(elem: A) is not allowed. otherwise, we can use a Box[Apple] as a Box[Fruit] which should accept all Fruit types
      // def set[B <: A](elem: B): Box[B] is also not allowed. otherwise, we can use a Box[Apple] as a Box[Fruit] which should accept all subtypes of B <: Fruit (was B <: Apple)
    }

    val box: Box[Fruit] = new Box[Apple](new Apple) // ok: Box[Apple] is a subtype of Box[Fruit]
    val fruit: Fruit = box.get // ok: we can get the element as a supertype
    val newBox: Box[Fruit] = box.set(new AppleX) // ok: we create a new Box[Fruit]
    val newNewBox: Box[Fruit] = box.set(new Fruit) // ok: we create a new Box[Fruit]

    // example2
    class Box2[+A <: Fruit](element: A) {
      def get(): A = element
      def set[B <: Fruit](elem: B): Box2[B] = new Box2(elem) // change to a Box also of type Fruit
      // def set[B](elem: B): Box2[B] // changing to any other type is not allowed, as the Box needs to contain a Fruit type
    }
    val box2: Box2[Apple] = new Box2(new Apple) // ok: Box2[Apple] is a subtype of Box2[Fruit]
    val fruit2: Apple = box2.get // ok: we can get the element as a supertype
    val newBox2: Box2[AppleX] = box2.set(new AppleX) // ok: we create a new Box2[AppleX]

  }
}
