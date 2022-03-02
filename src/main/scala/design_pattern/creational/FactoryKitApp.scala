package design_pattern.creational

import scala.collection.mutable

// https://github.com/josephguan/scala-design-patterns/tree/master/creational/factory-kit
// this pattern allows to create a factory kit via a separated builder and create immutable products through its create method
//   the factory kit class can't anticipate what products it must create
//   you want many custom builders instead of a global one
//   the factory kit keeps a Map of lambda functions used to create the products
// this pattern is similar to loan pattern in that:
//   the factory kit loans its toolbox (a resource) to a function of the client to build its toolbox
//   it differs in that the resource (toolbox) is initiated inside the load function instead of provided by the client
// this pattern is similar to simple factory pattern in that:
//   the client uses a factory method (factory kit) to create products
//   it differs in that the factory method (factory kit) is also built by the client

// product
trait Weapon

// concrete products
class Axe extends Weapon {
  override def toString: String = "Axe"
}

class Sword extends Weapon {
  override def toString: String = "Sword"
}

class Bow extends Weapon {
  override def toString: String = "Bow"
}

// factory kit (factory method)
//   a class that create product objects based on its toolbox
class WeaponFactoryKit(toolbox: Map[String, () => Weapon]) {
  // use an immutable Map of lambda functions to create products (the Map is built by the client's builder)

  def create(key: String): Weapon = {
    toolbox.get(key).get() // use the toolbox to create a product
  }
}

// factory method that creates a factory kit (factory method) for the products
object WeaponFactoryKit {

  def factory(builder: mutable.Map[String, () => Weapon] => Unit): WeaponFactoryKit = { // load function
    val toolbox = mutable.Map[String, () => Weapon]() // the resource (toolbox) is initiated inside the load function instead of provided by the client
    // we define a Map of functions to products: () => Weapon, instead of a Map of products: Weapon
    //   so that each time the client creates a product through the factory kit, a new product is initiated
    // if the products are shared singletons, then we could simply define Map[String, Weapon] here
    builder(toolbox)
    new WeaponFactoryKit(toolbox.toMap)
  }
}

// client
object FactoryKitApp extends App {
  val factory = WeaponFactoryKit.factory { toolbox => // customize a builder for factory kit
    toolbox.put("axe", () => new Axe())
    toolbox.put("bow", () => new Bow())
    toolbox.put("sword", () => new Sword())
  }

  val axe = factory.create("axe") // the client uses a factory kit to create project objects (products)
  println(axe) // Axe
  val axe2 = factory.create("axe") // the client uses a factory kit to create project objects (products)
  println(axe2) // Axe
  println(axe == axe2) // false
}
