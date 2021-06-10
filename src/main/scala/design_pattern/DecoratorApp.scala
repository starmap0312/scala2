package design_pattern

// https://github.com/josephguan/scala-design-patterns/tree/master/structural/decorator
// the pattern attaches additional responsibilities to an object dynamically
// it provides a flexible alternative to subclassing for extending functionality
//   it is dynamic and transparent and does not affect other objects
// use the pattern when extension by subclassing is impractical
//   ex. if it leads to a large number of independent subclasses for every combination
//   ex. if a class definition is hidden and unavailable for subclassing

// component
//   the interface for objects that have specific responsibilities
trait Coffee {

  def getCost: Int

  def getDescription: String
}

// concrete component
//   an class that implements every responsibilities of the component interface
class SimpleCoffee extends Coffee {

  override def getCost: Int = 10

  override def getDescription: String = "simple coffee"
}

// concrete decorator
//   it maintains a reference to a component object and it conforms to component's interface as well
//   `abstract override` is needed as the base class method is abstract (a trait)
trait MilkAdded extends Coffee {
  abstract override def getCost: Int = super.getCost + 5

  abstract override def getDescription: String = s"${super.getDescription} with milk"
}

// concrete decorator
trait SugarAdded extends Coffee {
  abstract override def getCost: Int = super.getCost + 3

  abstract override def getDescription: String = s"${super.getDescription} with sugar"
}

object DecoratorApp extends App {
  val coffee1 = new SimpleCoffee with SugarAdded
  println(coffee1.getCost) // 18 = 10 + 3
  println(coffee1.getDescription) // simple coffee with sugar

  // scala trait mixin
  //   ex. new trait A with B with C
  //       A -> B -> C is constructed in sequence
  //         i.e. a wrapped object new C(B(A)) is created
  //       C.method() -> B.method() -> A.method() is called in sequence when the wrapped object's method() is called
  //         i.e. when the wrapped object C(B(A)).method() is called
  val coffee2 = new SimpleCoffee with MilkAdded with SugarAdded
  println(coffee2.getCost) // 18 = 10 + 5 + 3, i.e. SugarAdded(MilkAdded(SimpleCoffee)).getCost()
  println(coffee2.getDescription) // simple coffee with milk with sugar, i.e. SugarAdded(MilkAdded(SimpleCoffee)).getDescription()
}
