package design_pattern.creational

// https://github.com/josephguan/scala-design-patterns/tree/master/creational/factory-method
// the pattern allows a class to defer the instantiation of products to subclasses of the creator
//   the client delegates responsibility to one of the creator subclasses: you localize the knowledge of which creator subclass is the delegate
// the pattern is similar to factory kit pattern in that:
//   it separates the creator (factory) class (abstraction) from the product classes (implementors)
//   the client interacts with the factory not with the products directly
//   it differs in that it defines different factory subclasses for each product, instead of a single factory class (with a product map) for all products
// the pattern is similar to the bridge pattern in that:
//   it separate the creator (factory) class (abstraction) from the product classes (implementors)
//   this allows the creator (factory) to have different implementations that create and operate on different products
//   the client then operate on the creator (factory) instead of the products
//   it differs in that the concrete creators (factory, refined abstractions) are defined to use different products (implementors)

// product interface (implementor)
//   it defines the interface of products the factory method creates
trait Noodle {
  def flavor(): String
}

// concrete products
//   it implements the product interface
class PadThai extends Noodle {
  override def flavor(): String = "Thai flavor"
}

class Spaghetti extends Noodle {
  override def flavor(): String = "Italian flavor"
}

// creator interface (abstraction, abstract factory)
//   it declares the factory method, which returns an object of type Product
//   it may also define a default implementation of the factory method that returns a default product
trait NoodleFactory {
  def create(): Noodle // a factory method that creates the product: it may not anticipate what object it must create

  def cook(): Unit = {
    val noodle = create()
    println(s"The noodle is ${noodle.flavor()}. Yummy!")
  }
}

// concrete creator (factory)
//   it creates a specific concrete product
//   there is a dedicated factory subclasses for each different product
class PadThaiChef extends NoodleFactory {
  override def create(): Noodle = new PadThai() // the subclass specifies what object it creates
}

class SpaghettiChef extends NoodleFactory {
  override def create(): Noodle = new Spaghetti()
}

// alternatively, we can define a client class that takes factory of a generic type (similar to abstract factory pattern)
class GenericChef[A <: Noodle](factory: () => A) {

  def cook(): Unit = {
    val noodle = factory()
    println(s"The noodle is ${noodle.flavor()}. Yummy!")
  }
}

// client
//   the client interacts with the factory interface, not the implementations or the products
object FactoryMethodApp extends App {
  // factory subclasses
  val chef1 = new PadThaiChef
  val chef2 = new SpaghettiChef
  chef1.cook() // The noodle is Thai flavor. Yummy!
  chef2.cook() // The noodle is Italian flavor. Yummy!

  // alternatively, use factory of a generic type
  val padThaiChef = new GenericChef(() => new PadThai)
  val spaghettiChef = new GenericChef(() => new Spaghetti)
  padThaiChef.cook()   // The noodle is Thai flavor. Yummy!
  spaghettiChef.cook() // The noodle is Italian flavor. Yummy!

}
