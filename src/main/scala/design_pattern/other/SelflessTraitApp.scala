package design_pattern.other

// https://github.com/josephguan/scala-design-patterns/tree/master/other/selfless-trait
// the pattern allows library designers to provide services that their clients can access either through mixins or imports
//   this gives users a choice between mixin composition and importing, which makes a library easier to use
// when designing a library, you can partition the services offered by the library into traits
//   your library users can mix into each class only the services (traits) they need from the library
//   downside 1) name conflicts, ex. users cannot mixin two traits with methods of the same signatures (an overload conflict)
//   downside 2) it is a bit awkward to experiment with the services offered by a trait, because
//               before the trait's services can be accessed, it must be mixed into some class or object
//   these downsides can be addressed by importing the members of a trait

trait Human

class Guy extends Human with Friendly {} // option 1) users can use mixin (with Friendly), which has a downside of overload conflicts

class KindGuy extends Human {

  import Friendly.{ greet => sayHi } // option 2) users can use imports (import Friendly), an alternative to mixin the trait
  // Scala allows users to import the members of any object & rename the members

  def greet(): String = sayHi() + " Have a nice day!"

}

// trait
//   it defines part of a library's behavior
trait Friendly {
  def greet(): String = "Hello!"
}

// trait companion object
//   it instantiates a singleton object that implements the trait
object Friendly extends Friendly
// Scala allows traits to have a companion object (a singleton object) that has the same name as its companion trait

object SelflessTraitApp extends App {
  println(new Guy().greet()) // Hello!
  println(new KindGuy().greet()) // Hello! Have a nice day!
}
