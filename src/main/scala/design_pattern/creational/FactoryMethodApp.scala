package design_pattern.creational

// https://github.com/josephguan/scala-design-patterns/tree/master/creational/factory-method
// the pattern allows a class to defer the instantiation of products to subclasses of the creator
//   the client delegates responsibility to one of the creator subclasses: you localize the knowledge of which creator subclass is the delegate
// the pattern is similar to the bridge pattern in that:
//   it separate the creator class (abstraction) from the product classes (implementors)
//   this allows the creator to have different implementations that create and operate on different products
//   the client then operate on the creator instead of the products
//   it differs in that the concrete creators (refined abstractions) are defined to use different products (implementors)

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
trait Chef {
  def createNoodle(): Noodle // a factory method that creates the product: it may not anticipate what object it must create

  def cook(): Unit = {
    val noodle = createNoodle()
    println(s"The noodle is ${noodle.flavor()}. Yummy!")
  }
}

// concrete creator
//   it creates a concrete product
class PadThaiChef extends Chef {
  override def createNoodle(): Noodle = new PadThai() // the subclass specifies what object it creates
}

class SpaghettiChef extends Chef {
  override def createNoodle(): Noodle = new Spaghetti()
}

// alternatively, we can define a creator that takes factory of a generic type
class GenericChef[A <: Noodle](factory: () => A) {

  def cook(): Unit = {
    val noodle = factory()
    println(s"The noodle is ${noodle.flavor()}. Yummy!")
  }
}

// client
object FactoryMethodApp extends App {
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
