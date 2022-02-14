// we should make a mutable container invariant, otherwise run-time error may occurs
//   ex. class Array[T](_length: Int) extends Serializable with Cloneable { ... }
//       (i.e. Array[T] does not relate to Array[T'] for T <: T')
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

class Fruit
class Apple extends Fruit
class AppleX extends Apple
object AppleY extends Apple
class Orange extends Fruit

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
    // bad example1-1: w/o generic type, i.e. no type parameter
    {
      class Box(element: Any) {
        def get(): Any = element
        def set(elem: Any): Box = new Box(elem) // ok, as we can use a Box[Apple] as a Box[Fruit] which should accept all supertypes of B >: Fruit (was B >: Apple)
      }

      val box: Box = new Box(new Apple) // i.e. a Box[Any]
      val apple: Apple = box.get.asInstanceOf[Apple] // no type cast (need explicit cast, which may throw java.lang.ClassCastException)
      val newBox: Box = box.set(new AppleX) // no compile-time type safety
      val newBox2: Box = box.set(new Orange) // no compile-time type safety
    }

    // bad example1-2: w/o generic type, i.e. no type parameter
    {
      class Box(element: Fruit) {
        def get() = element
        def set(elem: Fruit) = new Box(elem)
      }
      val box: Box = new Box(new Apple)
      val fruitBox: Box = box // i.e. a Box[Fruit]
      val apple: Fruit = box.get // limited auto type cast to Fruit
      val newBox: Box = box.set(new AppleX) // limited compile-time type safety about Fruit
      val newBox2: Box = box.set(new Orange) // limited compile-time type safety about Fruit
    }

    // good example1-1: w/ generic type, w/o type bounds in the set() method
    {
      class Box[+A](element: A) {
        def get(): A = element
        def set[B](elem: B): Box[B] = new Box(elem)
      }

      val box: Box[Apple] = new Box[Apple](new Apple) // a Box[Apple]
      val fruitBox: Box[Fruit] = box // ok: as both can accept any subtype of Any in the set() method
      val apple: Apple = box.get // auto type cast to Apple
      val newBox: Box[AppleX] = box.set(new AppleX) // compile-time type safety about AppleX
      val newBox2: Box[Orange] = box.set(new Orange) // compile-time type safety about Orange
    }

    // good example1-2: w/ generic type, w/ type bounds in the type parameter & set() method
    {
      class Box[+A <: Fruit](element: A) {
        def get(): A = element
        def set[B <: Fruit](elem: B): Box[B] = new Box(elem)
      }
      val box: Box[Apple] = new Box(new Apple) // a Box[Apple]
      val fruitBox: Box[Fruit] = box // ok: as both can accept any subtype of Fruit in the set() method
      val apple: Apple = box.get // auto type cast to Apple
      val newBox: Box[AppleX] = box.set(new AppleX) // B is referred as type AppleX, as B must be a subtype of Fruit
      val newBox2: Box[Orange] = box.set(new Orange) // B is referred as type Orange, as B must be a subtype of Fruit
    }

    // good example1-3: w/ generic type, w/ type bounds in the set() method
    {
      class Box[+A](element: A) { // it is safe to be covariant due to its immutability
        def get(): A = element
        def set[B >: A](elem: B): Box[B] = new Box(elem) // ok, as we can use a Box[Apple] as a Box[Fruit] which should accept all supertypes of B >: Fruit (was B >: Apple)
        // def set(elem: A) is not allowed. otherwise, we can use a Box[Apple] as a Box[Fruit] which should accept all Fruit types
        // def set[B <: A](elem: B): Box[B] is also not allowed. otherwise, we can use a Box[Apple] as a Box[Fruit] which should accept all subtypes of B <: Fruit (was B <: Apple)
      }
      val box: Box[Apple] = new Box[Apple](new Apple) // a Box[Apple]
      val fruitBox: Box[Fruit] = box // ok: as a Box[Fruit] accepts any supertype of Fruit and a Box[Apple] accepts any supertype of Apple, so a Box[Apple] can be used as a Box[Fruit]
      val apple: Apple = box.get // auto type cast to Apple
      val newBox: Box[Apple] = box.set(new AppleX) // B is referred as type Apple; it's not a Box[AppleX] as B must be a supertype of Apple
      val newBox2: Box[Fruit] = box.set(new Orange) // B is is referred as type Fruit; it's not a Box[Orange] as B must be a supertype of Apple
    }
  }
}
