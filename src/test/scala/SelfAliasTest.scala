// Self-Type Annotations:
// trait A { self: B => ... }
// 1) let you specify additional type expectations for this
//    i.e. this sets "A.this" as type B
// 2) it is also used to create an alias for this
//    i.e. this makes "self" an alias of "A.this"
//
// simple alias and no type ascription
//   if you don’t include a type ascription, Scala will assume that the type of self is the type of trait A
//   trait A {
//     self =>
//     ...
//   }
//   (the above is equivalent to the following)
//   trait A {
//     private[this] val self = this
//     ...
//   }

// example1: the self alias
// useful when you have an inner class and want to distinguish outer and inner classes' this (alias one of them as self)
class A { self => // this makes "self" an alias of "A.this"

  def id = "class A"

  class inner {
    def id = "inner: " + self.id  // we give A.this an alias "self", so that we can easily refer to it
  }

  val instanceOfInner = new inner
}

// example2: reassign the type of this
// useful when you want to mixin two superclasses
// Ref: https://docs.scala-lang.org/tour/self-types.html
trait User {
  def username: String
}
trait Tweeter {
  this: User =>  // reassign the type of this to trait User
  def tweet(tweetText: String) = println(s"$username: $tweetText")
}
class VerifiedTweeter(val username_ : String) extends Tweeter with User {
  // we mixin User because Tweeter required it
  def username = s"VerifiedTweeter: $username_"
}

// real example: Akka extension
//   ex. class Actor is assumed as the type of "this" in trait Counting, and
//       "self" is introduced as an alias for "this" in trait Counting
//         this alias is useful for accessing the "this" reference from an inner class of trait Counting
//         i.e. you can use "self" in instead of "Counting.this" when accessing "this" reference of  trait Counting within its nested class
//
//   trait Counting { self: Actor =>
//     def increment() = CountExtension(context.system).increment()
//   }
//
//   class MyCounterActor extends Actor with Counting {
//     def receive = {
//       case someMessage => increment()
//     }
//   }

object SelfAliasTest {
  def main(args: Array[String]): Unit = {
    // example1:
    val a = new A
    println(a.id)                 // class A
    println(a.instanceOfInner.id) // inner: class A

    // example2:
    val realBeyonce = new VerifiedTweeter("Beyonce")
    realBeyonce.tweet("Hello")    // VerifiedTweeter: Beyonce: Hello
  }
}
