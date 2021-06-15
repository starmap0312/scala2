package design_pattern

// https://github.com/josephguan/scala-design-patterns/tree/master/creational/factory-method
// the pattern allows a class to defer the instantiation of products to subclasses of the creator
//   the client delegates responsibility to one of the creator subclasses: you localize the knowledge of which creator subclass is the delegate

// product interface
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

// creator interface
//   it declares the factory method, which returns an object of type Product
//   it may also define a default implementation of the factory method that returns a default product
trait Chef {
  def makeNoodle(): Noodle // a factory method that creates the product: it cannot anticipate what object it must create

  def cook(): Unit = {
    val noodle = makeNoodle()
    println(s"The noodle is ${noodle.flavor()}. Yummy!")
  }
}

// concrete creator
//   it creates a concrete product
class ThaiChef extends Chef {
  override def makeNoodle(): Noodle = new PadThai() // the subclass specifies what object it creates
}

class ItalianChef extends Chef {
  override def makeNoodle(): Noodle = new Spaghetti()
}

// client
object FactoryMethodApp extends App {
  val chef = new ItalianChef
  chef.cook()
}
