package design_pattern.behavioral

// https://github.com/josephguan/scala-design-patterns/tree/master/behavioral/template-method
// the pattern defines the skeleton of an algorithm, deferring the implementation of some primitive steps to subclasses
// inversion of control: "Don't call us, we'll call you"
//   don't call superclass' method, let superclass' method calls you
// factory method pattern is a specialization of template method
//   the factory method is specifically  for object creation, and the implementation of factory method is defined in subclasses

// abstract class
//   it defines abstract primitive operations and a template method that uses these primitive operations
abstract class Customer {

  def goToRestaurant(): Unit = {
    val orders = order()
    println(s"I am eating $orders.")
    println(s"The $orders is ${comments()}")
  }

  protected def order(): String

  protected def comments(): String

}

// concrete classes
//   it implements the primitive operations to carry out subclass-specific steps of the template
class Vegan extends Customer {
  override protected def order(): String = "vegetable"

  override protected def comments(): String = "yummy"
}

class MeatLover extends Customer {
  override protected def order(): String = "steak"

  override protected def comments(): String = "yummy"
}

object TemplateMethodApp extends App {
  val vegan = new Vegan
  vegan.goToRestaurant()
  // I am eating vegetable.
  // The vegetable is yummy

  val meatLover = new MeatLover
  meatLover.goToRestaurant()
  // I am eating steak.
  // The steak is yummy
}
