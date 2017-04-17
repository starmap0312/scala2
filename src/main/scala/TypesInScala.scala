// AnyVal: Java primitives
// AnyRef: java.lang.Object
// Any: java.lang.Object and Java Primitives
object TypesInScala {
  def main(args: Array[String]): Unit = {
    // 1) Parametric polymorphism (Generic types)
    val list1: List[Any] = List(1, 2, "foo", "bar")  // type: List[Any]
    val list2 = List(1, 2, "foo", "bar")             // type: List[Any], inferred by compiler
    println(list1.head.asInstanceOf[Int])            // need ad-hoc casting in order to treat it as an Int instance
    println(list2.head.asInstanceOf[Int])            // need ad-hoc casting in order to treat it as an Int instance

    // 2) Type inference
    //    type is constantly inferred by compiler to leverage the syntactic overhead
    def id[T](x: T) = x                              // type: [T](x: T)T, a method/expression not yet instantiated
    //val id = (x: T) => id1(x)                      // compile error: not found type T
    println(id(1))                                   // type: Int,    inferred by compiler when expression evaluated
    println(id("one"))                               // type: String, inferred by compiler when expression evaluated

    // 3) Variance
    //    how to create class hierarchies for classes with generic types
    //    i.e. what is the relation between Container[T’] and Container[T] for type T’ subclassing type T?
    class Invariant[T]
    val inv1 = new Invariant[Number]
    inv1.asInstanceOf[Invariant[String]]                  // OK: runtime type casting
    //val inv2: Invariant[AnyRef] = new Invariant[String] // compile error: type mismatch, found: Covariant[String], required: Covariant[AnyRef]
    def func(inv: Invariant[AnyRef]) = {}
    //func(inv1)                                          // compile error: type mismatch, String <: AnyRef, but class Covariant is invariant in type T
    // 3.1) covariant: [+T]
    //      C[T’] is a subclass of C[T] for T' subclassing T
    class Covariant[+T]                                   // defined class Covariant
    val cv: Covariant[Number] = new Covariant[Integer]    // compiler type check OK: Covariant[AnyRef]
    // this is OK because instance new Covariant[String] can be up-casted to Covariant[AnyRef]
    //val cv: Covariant[String] = new Covariant[AnyRef]   // compile error: type mismatch
    // this is NOT OK because new Covariant[AnyRef] cannot be down-casted to Covariant[String]

    // 3.2) contravariant: [-T]
    //      C[T] is a subclass of C[T’] for T' subclassing T
    class Contravariant[-T]
    val ctv: Contravariant[String] = new Contravariant[AnyRef]
    // this is OK because instance new Contravariant[AnyRef] can be down-casted to Contravariant[String]
    //val ctv: Contravariant[AnyRef] = new Contravariant[String] // compile error: type mismatch
    // this is NOT OK because new new Contravariant[String] cannot be up-casted to Contravariant[AnyRef]

    // 3.3) example:
    //      ex. Chicken extends Bird extends Animal
    //          Duck    extends Bird extends Animal
    //          if you define a function that takes a Chicken, then that function would choke on a Duck
    //          if you define a function that takes an Animal, then that function can take a Duck
    //          so function parameters should be contravariant
    class Animal {
      val name = "animal"
    }
    class Bird extends Animal {
      override val name = "bird"
    }
    class Chicken extends Bird {
      override val name = "chicken"
    }
    class Duck extends Bird {
      override val name = "duck"
    }
    // function parameters are contravariant
    val getName: (Bird => String) = ((x: Animal) => x.name)
    //println(getName(new Animal))             // compile error: type mismatch: found Animal, required Bird
    println(getName(new Bird))                 // bird   : the Bird    instance is up-casted to Animal when calling the function
    println(getName(new Chicken))              // chicken: the Chicken instance is up-casted to Animal when calling the function
    println(getName(new Duck))                 // duck   : the Duck    instance is up-casted to Animal when calling the function
    // function’s return value type is covariant
    val getBird: (() => Bird) = (() => new Chicken)
    println(getBird().name)                    // chicken: the Bird instance is down-casted to Chicken when returned

    // 3.4) another example of contravariant and covariant
    //      trait Function1
    //        function parameters are contravariant
    //        function’s return value type is covariant
    trait Function1 [-T1, +R] extends AnyRef

    // 4) type bounds
    // 4.1) A =:= B: A must be equal to B
    // 4.2) A <:< B: A must be a subtype of B
    // 4.3) A <%< B: A must be viewable as B

    // 5) Type Bounds
    // 5.1) Upper Type Bounds: i.e. B <: A
    //      it declares that the type parameter B or the abstract type B refer to a subtype of type A
    //      in Scala, type parameters and abstract types may be constrained by a type bound
    class Cage[B <: Animal](x: B) {
      def animal: B = x
    }
    val dogCage = new Cage[Bird](new Bird)

    // 5.1) Lower Type Bounds: i.e. B >: A
    //      it declares that the type parameter B or the abstract type B refer to a supertype of type A
    case class ListNode[+A](h: A, t: ListNode[A]) {
      def head: A = h
      def tail: ListNode[A] = t
      // prepend() takes an object of a supertype and add it to the list head
      def prepend[B >: A](elem: B): ListNode[B] = ListNode(elem, this)
    }
    val strList: ListNode[String] = ListNode(null, null).prepend("hello").prepend("world")
    val anyList: ListNode[Any] = strList.prepend(12345)
    println(strList)
    println(anyList)

    // Generic Type in Scala
    val strlist: List[String] = List("abc", "def")
    //val strlist: List[String] = List(1, 2)             // type mismatch
    println(strlist.head.charAt(1))                      // b: strlist.head is auto-casted into String
    val anylist: List[Any] = List("abc", "def")
    println(anylist.head.asInstanceOf[String].charAt(1)) // no need to cast strlist.head into String by yourself
  }
}
