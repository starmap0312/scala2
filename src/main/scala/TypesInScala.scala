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
    // 3.1) covariant: [+T]
    //      C[T’] is a subclass of C[T] for T' subclassing T
    class Covariant[+T]                                 // defined class Covariant
    val cv: Covariant[AnyRef] = new Covariant[String]   // type: Covariant[AnyRef]
    // this is OK because instance new Covariant[String] can be up-casted to Covariant[AnyRef]
    //val cv: Covariant[String] = new Covariant[AnyRef] // compile error: type mismatch
    // this is NOT OK because new Covariant[AnyRef] cannot be down-casted to Covariant[String]

    // 3.2) contravariant: [+T]
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
  }
}
