package design_pattern

// https://github.com/josephguan/scala-design-patterns/tree/master/behavioral/visitor
// this pattern lets you define a new operation (visitor) without changing the classes of the elements (visitees) on which it operates
// this pattern is similar to strategy pattern in that an operation (strategy) is a function that operate on the elements (data)
//   it differs in that the object structure (client) contains the visitees (elements, data) instead of an operation (strategy)
//   moreover, each visitee (elements, data) must implement an accept method that accepts the operation (strategy) that operate on itself

// visitor (operation)
//   it keeps related operations together in one class: this avoids polluting the visitee (element) classes
//   it determines the concrete class of the visitee (element) being visited
//   it accesses the visitee (element) directly through its interface
object AnimalOperation {

  type Type = Animal => Unit // type of visitor

  // concrete visitors (operations)
  def speak(animal: Animal): Unit = animal match {
    case monkey: Monkey => println("Ooh oo aa aa!")
    case lion: Lion => println("Roaaar!")
    case dolphin: Dolphin => println("Tuut tuttu tuutt!")
    case _ => // do nothing
  }

  def swim(animal: Animal): Unit = animal match {
    case dolphin: Dolphin => println("Dolphin swim fast!")
    case _ => // do nothing
  }
}


// visitee (element)
//   assume that the visitee class rarely changes, but you often want to define new operations on it
//   it accepts an operation and perform the operation on itself
trait Animal {
  def accept(operation: AnimalOperation.Type)
}

// concrete visitees (elements)
class Monkey extends Animal {
  override def accept(operation: AnimalOperation.Type): Unit = {
    operation(this)
  }
}

class Lion extends Animal {
  override def accept(operation: AnimalOperation.Type): Unit = {
    operation(this)
  }
}

class Dolphin extends Animal {
  override def accept(operation: AnimalOperation.Type): Unit = {
    operation(this)
  }
}

// object structure (client)
//   it contains many visitees (elements) and you want to perform operations on these visitee depending on their concrete classes
//   it provides a method to allow an operation (visitor) to operate on (visit) its elements (data)
class Zoo(animals: Animal*) { // animals: a collection such as a Seq or a Set

  def accept(operation: AnimalOperation.Type): Unit = {
    animals.foreach {
      _.accept(operation)
    }
  }
}

object VisitorApp extends App {
  val zoo = new Zoo(new Monkey, new Lion, new Dolphin)
  zoo.accept(AnimalOperation.speak)
  zoo.accept(AnimalOperation.swim)
}
