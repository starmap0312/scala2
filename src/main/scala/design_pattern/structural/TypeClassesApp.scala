package design_pattern.structural

// https://github.com/josephguan/scala-design-patterns/tree/master/structural/type-classes
// a type class is a type system construct that supports ad hoc polymorphism
// the type classes pattern supports retroactive extension
//   i.e. the ability to extend existing classes with new functionality without needing to touch or re-compile the original class
// use type classes if you want to avoid a lot of adapters

// type classes (adapter)
//   it takes one or more type parameters: it is usually designed to be stateless
//   it's a generic-type class
trait Speakable[T] {
  def say(): String
}

// target interface
trait Animal

// concrete targets (adaptees)
//   it defines the real object that is used by the client as a function parameter
class Monkey extends Animal
class Lion extends Animal

object Animal {

  // implicit objects (singleton) of type classes
  //   it is the implicit object converting Target to TypeClass[Target]
  implicit object SpeakableMonkey extends Speakable[Monkey] {
    override def say(): String = "I'm monkey. Ooh oo aa aa!"
  }
  // implicit val SpeakableMonkey = new Speakable[Monkey] { override def say(): String = "I'm monkey. Ooh oo aa aa!" }

  implicit object SpeakableLion extends Speakable[Lion] {
    override def say(): String = "I'm Lion. Roaaar!"
  }
}



// client
//   it defines functions which take parameter T and implicit parameter TypeClass[T]
//   it takes the target (adaptee) as a parameter & operates on its implicit object (adapter object) instead
class Human {

  // the client can say hello to some target (Animal), and it does not know what is his concrete type & he is actually not Speakable
  //   the target will be wrapped in a Speakable object and be passed in implicitly instead
  // note that the target (Animal) does not have the say() method, and only the implicit Speakable object implements it
  def sayHelloTo[A](target: A)(implicit s: Speakable[A]): String = {
    s"Human say hello & get reply: ${s.say()}"
  }
  // the implicit scope contains all sort of companion objects and package object that bear some relation to the implicit's type
  // ex. a implicit object can be defined in the package object of the type, the companion object of the type,etc.

  // implementation using context-bounds
  //    as a shortcut for implicit parameters with only one type parameter, Scala also provides so-called context bounds
  //    if you want to access that implicitly available value, you need to call the implicitly method
  //  ex.
  //  def sayHelloTo[A: Speakable[A]](target: A): String = {
  //    s"Human say hello, get reply ${implicitly[Speakable[A]].say()}"
  //  }
}

object TypeClassesApp extends App {
  val human = new Human()
  val monkey = new Monkey()
  val lion = new Lion()
  println(human.sayHelloTo(monkey)) // Human say hello & get reply: I'm monkey. Ooh oo aa aa!
  println(human.sayHelloTo(lion)) // Human say hello & get reply: I'm Lion. Roaaar!
}
