package design_pattern

// https://github.com/josephguan/scala-design-patterns/tree/master/structural/decorator
// the pattern attaches additional responsibilities to an object dynamically
// it provides a flexible alternative to subclassing for extending functionality
//   it is dynamic and transparent and does not affect other objects
// use the pattern when extension by subclassing is impractical
//   ex. if it leads to a large number of independent subclasses for every combination
//   ex. if a class definition is hidden and unavailable for subclassing

// interface (component)
//   the interface for objects that have specific responsibilities
trait Coffee {

  def getCost: Int

  def getDescription: String
}

// base class (concrete component)
//   an class that implements every responsibilities of the component interface
class SimpleCoffee extends Coffee {

  override def getCost: Int = 10

  override def getDescription: String = "simple coffee"
}

// decorator (stackable trait)
//   it maintains a reference to a component object and it conforms to component's interface as well
//   it extends the interface which means that the trait can only be mixed into a class that also extends the interface
trait MilkAdded extends Coffee {
  abstract override def getCost: Int = super.getCost + 5
  // the modifier `abstract` is needed as it's super is a trait (interface)
  //   the modifier tells the compiler the trait will be mixed into some base class with concrete implementation of the method
  //   without the `abstract` modifier, you'll receives a compile error
  //  the super call will be dynamically bound: it will work as long as the trait is mixed in with a base class (concrete component)

  abstract override def getDescription: String = s"${super.getDescription} with milk"
}

// decorator
trait SugarAdded extends Coffee {
  abstract override def getCost: Int = super.getCost + 3

  abstract override def getDescription: String = s"${super.getDescription} with sugar"
}

object DecoratorApp extends App {
  val coffee1 = new SimpleCoffee with SugarAdded // i.e. SugarAdded(SimpleCoffee)
  println(coffee1.getCost) // 18 = 10 + 3
  println(coffee1.getDescription) // simple coffee with sugar

  // scala trait mixin
  //   ex. trait A with B with C
  //       i.e. C(B(A)), a wrapped object, is created: A -> B -> C is constructed in sequence
  //       when C(B(A)).method() is called, C.method() -> B.method() -> A.method() is called in sequence
  val coffee2 = new SimpleCoffee with MilkAdded with SugarAdded
  println(coffee2.getCost) // 18 = 10 + 5 + 3, i.e. SugarAdded(MilkAdded(SimpleCoffee)).getCost()
  println(coffee2.getDescription) // simple coffee with milk with sugar, i.e. SugarAdded(MilkAdded(SimpleCoffee)).getDescription()
}
