package design_pattern.structural

import scala.collection.mutable.ArrayBuffer

// https://github.com/josephguan/scala-design-patterns/tree/master/structural/decorator
// https://www.artima.com/articles/scalas-stackable-trait-pattern
// this pattern is similar in structure to the decorator pattern
//   it differs in that it involves decoration of class composition instead of object composition
//   i.e. a stackable trait decorates the core trait at compile time
//        a decorator object decorates the core object at run time
// in this pattern, a trait (or class) can play one of three roles:
//
//      base
//    --|  |--
//    |       |
//  core  stackable
//
// 1) base trait (an interface that defines abstract methods)
// 2) core trait (a concrete class that implements all the abstract methods defined in the interface)
// 3) stackable trait (a decorator class that overrides the abstract methods defined in the interface)
//      it uses scala's `abstract override` modifiers and invokes the `super` implementation of the same method
//      as a result, the stackable modifies the core's behavior
// you can select any of the stackables & mix them into a class to obtain a new class that has all of the modifications you chose

// interface (base trait)
trait IntQueue {
  def get(): Int
  def put(x: Int)
}

// base class (core trait)
class BasicIntQueue extends IntQueue {

  private val buffer = new ArrayBuffer[Int]

  override def get() = buffer.remove(0)

  override def put(x: Int) = {
    buffer += x
  }
}

// decorator (stackable trait)
//   it modifies the behavior of an underlying (super) core trait rather than defining a concrete class itself
trait Doubled extends IntQueue {

  // use Scala `abstract override` to declare that it overrides the mixed-in trait put method, instead of binding to any concrete implementation
  // i.e. the modifier basically says that there has to be a class implementing this method which appears after this trait with the abstract override modifier
  //      this is necessary in order to execute super.put() method
  abstract override def put(x: Int) = {
    super.put(2 * x)
  }
}

// decorator (stackable trait)
trait Incrementing extends IntQueue {

  abstract override def put(x: Int) = {
    super.put(x + 1)
  }
}

object StackableTraitApp extends App {

  val queue = new BasicIntQueue with Doubled with Incrementing
  // i.e. queue = new Incrementing(new Doubled(new BasicIntQueue()))
  //      when you make a call to queue.put(0)
  //      it makes a call to Incrementing's put() method, in which its decoratee's put() method is called via super.put()
  queue.put(0) // put (2 *(0 + 1)) = 2
  queue.put(1) // put (2 *(1 + 1)) = 4
  queue.put(2) // put (2 *(2 + 1)) = 6
  println(queue.get()) // 2
  println(queue.get()) // 4
  println(queue.get()) // 6

  println

  class AdvancedIntQueue extends BasicIntQueue with Doubled with Incrementing {}
  // i.e. queue2 = new AdvancedIntQueue == new Incrementing(new Doubled(new BasicIntQueue()))

  val queue2 = new AdvancedIntQueue
  queue2.put(0) // put (2 *(0 + 1)) = 2
  queue2.put(1) // put (2 *(1 + 1)) = 4
  queue2.put(2) // put (2 *(2 + 1)) = 6
  println(queue2.get()) // 2
  println(queue2.get()) // 4
  println(queue2.get()) // 6

}
