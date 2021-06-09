package design_pattern

import scala.collection.mutable

// https://github.com/josephguan/scala-design-patterns/tree/master/creational/factory-kit
// this pattern allows to create a factory kit via a separated builder and create immutable products through its create method
//   the factory kit class can't anticipate what products it must create
//   you want many custom builders instead of a global one
//   the factory kit keeps a Map of lambda functions used to create the products

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
  // use a Map of lambda functions to create products

  def create(key: String): Weapon = {
    toolbox.get(key).get()
  }
}

object WeaponFactoryKit {

  def factory(builder: mutable.Map[String, () => Weapon] => Unit) = {
    val toolbox = mutable.Map[String, () => Weapon]()
    builder(toolbox)
    new WeaponFactoryKit(toolbox.toMap)
  }
}

// client
object FactoryKitApp extends App {
  val factory = WeaponFactoryKit.factory { toolbox => // custom a builder for factory kit
    toolbox.put("axe", () => new Axe())
    toolbox.put("bow", () => new Bow())
    toolbox.put("sword", () => new Sword())
  }

  // use factory kit to create project objects
  val axe = factory.create("axe")
  println(axe) // Axe
}
