package design_pattern

import scala.collection.mutable

// https://github.com/josephguan/scala-design-patterns/tree/master/creational/factory-kit
// this pattern allows to create a factory kit via a separated builder and create immutable products through its create method
//   the factory kit class can't anticipate what products it must create
//   you want many custom builders instead of a global one
//   the factory kit keeps a Map of lambda functions used to create the products
// this pattern is similar to loan pattern in that:
//   the factory kit loans its toolbox (a resource) to a function of the client to build its toolbox
//   it differs in that the resource (toolbox) is initiated inside the load function instead of provided by the client

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

// factory kit
//   a class that create product objects based on its toolbox
class WeaponFactoryKit(toolbox: Map[String, () => Weapon]) {
  // use an immutable Map of lambda functions to create products (the Map is built by the client's builder)

  def create(key: String): Weapon = {
    toolbox.get(key).get() // use the toolbox to create a product
  }
}

object WeaponFactoryKit {

  def factory(builder: mutable.Map[String, () => Weapon] => Unit) = { // load function
    val toolbox = mutable.Map[String, () => Weapon]() // the resource (toolbox) is initiated inside the load function instead of provided by the client
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

  // use factory kit to create project objects
  val axe = factory.create("axe")
  println(axe) // Axe
}
