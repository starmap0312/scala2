// Self-Type Annotations:
// trait A { self: B => ... }
// 1) let you specify additional type expectations for this
// 2) it is also used to create an alias for this

// example: simple alias, not type ascription
//   if you don’t include a type ascription, Scala will assume that the type of self is the type of trait A
// i.e. it is equivalent to the following:
//   trait A {
//     private[this] val self = this
//    }
class A { self => // this sets "A.this" as type B, and make "self" an alias of "A.this"

  def id = "class A"

  class inner {
    def id = "inner: " + self.id  // we give A.this an alias "self", so that we can easily refer to it
  }

  val instanceOfInner = new inner
}


// Akka extension example
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
    val a = new A
    println(a.id)                 // class A
    println(a.instanceOfInner.id) // inner: class A

  }
}
