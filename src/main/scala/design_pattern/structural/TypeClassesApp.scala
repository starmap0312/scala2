package design_pattern.structural

// https://github.com/josephguan/scala-design-patterns/tree/master/structural/type-classes
// a type class is a type system construct that supports ad hoc polymorphism
// the type classes pattern supports retroactive extension
//   i.e. the ability to extend existing classes with new functionality without needing to touch or re-compile the original class
// use type classes if you want to avoid a lot of adapters

// the pattern is similar to the magnet pattern but differs in that:
//   the type conversion is achieved by passing an implicit type class object to the client (higher-order function), and the client uses it instead
//   the concrete type classes are usually defined in the target class companion, instead of the type class companion

// https://medium.com/javarevisited/typeclasses-in-java-5ac37ad2483f
// a type class is an interface that defines some behaviors (functions)
//   a type class specifies a bunch of behaviors (functions)
//   when we decide to make type T an instance of a type class, we define what those behaviors (functions) mean for that original type T

// type classes interface (magnet interface/adapter)
//   it takes one or more type parameters: it is usually designed to be stateless
//   it's a generic-type class
trait Speakable[T] {
  def say(): String
}
// type class is a class (group) of types (T), used to add additional functionality (using a different implementation for a type T) without any changes to the original type T

// target interface
trait Animal

// concrete targets (adaptees)
//   it defines the real object that is used by the client as a function parameter
class Monkey extends Animal
class Lion extends Animal

// target class companion (implicit classes)
object Animal {

  // concrete type classes
  // 1) define implicit objects
  // implicit objects (singleton) of type classes
  //   it is the implicit object converting Target to TypeClass[Target]
  implicit object SpeakableMonkey extends Speakable[Monkey] {
    override def say(): String = "I'm monkey. Ooh oo aa aa!"
  }
  // alternatively, we can assign an anonymous type class instance to an implicit val
  // implicit val SpeakableMonkey = new Speakable[Monkey] { override def say(): String = "I'm monkey. Ooh oo aa aa!" }

  implicit object SpeakableLion extends Speakable[Lion] {
    override def say(): String = "I'm Lion. Roaaar!"
  }

  // 2) alternatively, define implicit conversion classes
  implicit class ImplcitConversionForMonkey(monkey: Monkey) extends Speakable[Animal] {
    override def say(): String = "I'm monkey. Ooh oo aa aa!"
  }

  implicit class ImplcitConversionForLion(lion: Lion) extends Speakable[Animal] {
    override def say(): String = "I'm Lion. Roaaar!"
  }
}

// client
//   it defines functions which take parameter T and implicit parameter TypeClass[T]
//   it takes the target (adaptee) as a parameter & operates on its implicit object (adapter object) instead
class Human {

  // 1) original: the client uses the target & an implicit object of type class is passed to the method as well
  // the client can say hello to some target (Animal), and it does not know what is his concrete type & he is actually not Speakable
  //   the target will be wrapped in a Speakable object and be passed in implicitly instead
  // note that the target (Animal) does not have the say() method, and only the implicit Speakable object implements it
  def sayHelloToWithImplicitObject[A](target: A)(implicit s: Speakable[A]): String = { // higher-order function that uses concrete type classes to generalize the behavior of the original type A
    s"Human say hello & get reply: ${s.say()}"
  }
  // the implicit scope contains all sort of companion objects and package object that bear some relation to the implicit's type
  //   ex. a implicit object can be defined in the package object of the type, the companion object of the type,etc.

  // 2) alternatively: the client uses the context bound notation, a syntactic sugar of the above
  def sayHelloToWithContextBound[A: Speakable](target: A): String = {
    val s = implicitly[Speakable[A]]
    s"Human say hello & get reply: ${s.say()}"
  }

  // 3) alternatively: the client uses the implicit conversion type class
  def sayHelloToWithImplicitConversion[A](s: Speakable[A]): String = {
    s"Human say hello & get reply: ${s.say()}"
  }

  // implementation using context-bounds
  //    as a shortcut for implicit parameters with only one type parameter, Scala also provides so-called context bounds
  //    if you want to access that implicitly available value, you need to call the implicitly method
  //  ex.
  //  def sayHelloTo[A: Speakable[A]](target: A): String = {
  //    s"Human say hello, get reply ${implicitly[Speakable[A]].say()}"
  //  }
}

object TypeClassesApp extends App {
  // example 1
  val human = new Human()
  val monkey = new Monkey()
  val lion = new Lion()
  println(human.sayHelloToWithImplicitObject(monkey)) // Human say hello & get reply: I'm monkey. Ooh oo aa aa!
  println(human.sayHelloToWithImplicitObject(lion)) // Human say hello & get reply: I'm Lion. Roaaar!

  // alternatively
  println(human.sayHelloToWithContextBound(monkey)) // Human say hello & get reply: I'm monkey. Ooh oo aa aa!
  println(human.sayHelloToWithContextBound(lion)) // Human say hello & get reply: I'm Lion. Roaaar!

  // alternatively
  println(human.sayHelloToWithImplicitConversion(monkey)) // Human say hello & get reply: I'm monkey. Ooh oo aa aa!
  println(human.sayHelloToWithImplicitConversion(lion)) // Human say hello & get reply: I'm Lion. Roaaar!

  // example 2
  // type class interface
  trait Monoid[A] {
    def empty: A
    def combine(x: A, y: A): A
  }
  object Monoid {

    // concrete type classes
    implicit object intMonoid extends Monoid[Int] {
      override def empty: Int = 0
      override def combine(x: Int, y: Int): Int = x + y
    }

    implicit object strMonoid extends Monoid[String] {
      override def empty: String = ""
      override def combine(x: String, y: String): String = x + y
    }
  }
  // Functor, Applicative, Monad, Monoid are famous type classes

  // client
  def combineAll[T](list: Seq[T])(implicit monoid: Monoid[T]): T = {
    list.fold(monoid.empty)(monoid.combine)
  }
  // with type class Monoid[T], we can define abstract higher-order functions that can be applied to all types T (or all Seq[T]) without changing T's source code
  // note: Java does not have implicit parameter

  println(combineAll(Seq(1, 2, 3)))       // 6
  println(combineAll(Seq("a", "b", "c"))) // abc

}
