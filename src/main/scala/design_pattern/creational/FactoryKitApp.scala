package design_pattern.creational

import scala.collection.mutable

// https://github.com/josephguan/scala-design-patterns/tree/master/creational/factory-kit
// this pattern allows to create a factory kit via a separated builder and create immutable products through its create method
//   the factory kit class can't anticipate what products it must create
//   you want many custom builders instead of a global one
//   the factory kit keeps a Map of lambda functions used to create the products
// this pattern is similar to loan pattern in that:
//   the factory kit loans its box (a resource) to a function of the client to build its box
//   it differs in that the resource (box) is initiated inside the load function instead of provided by the client
// this pattern is similar to simple factory pattern in that:
//   the client uses a factory method (factory kit) to create products
//   it differs in that the factory method (factory kit) is also built by the client

// product interface
trait Weapon {
  override def toString: String = s"${getClass.getSimpleName}"
}

// concrete products
class Axe extends Weapon
class Sword extends Weapon
class Bow extends Weapon

// factory kit (factory method)
//   a class that create product objects based on its toolbox
class WeaponFactoryKit(box: Map[String, Weapon]) {
  // use an immutable Map to provide products (the Map is built by the client's builder function)
  // note: if we need to initiate a new product through the factory kit each time, we could define a Map[String, () => Weapon], instead of Map[String, Weapon]

  def provide(key: String): Option[Weapon] = {
    box.get(key) // provide a product
  }
}

// factory method that creates a factory kit (factory method) for the products
object WeaponFactoryKit {

  def factory(builderFn: mutable.Map[String, Weapon] => Unit): WeaponFactoryKit = { // load function
    val mp = mutable.Map[String, Weapon]() // the resource (box) is initiated inside the load function instead of provided by the client
    builderFn(mp)
    new WeaponFactoryKit(mp.toMap)
  }
}

// client
object FactoryKitApp extends App {
  val factory: WeaponFactoryKit = WeaponFactoryKit.factory({ mp => // customize a builder for factory kit
    mp.put("axe", new Axe())
    mp.put("bow", new Bow())
    mp.put("sword", new Sword())
  })

  val axe1 = factory.provide("axe") // the client uses a factory kit to provide products
  val axe2 = factory.provide("axe") // the client uses a factory kit to provide products
  println(axe1) // Some(Axe)
  println(axe2) // Some(Axe)
  println(axe1 == axe2) // false

  // alternatively, the client uses the original constructor
  val factory2: WeaponFactoryKit = new WeaponFactoryKit(
    Map("axe" -> new Axe, "bow" -> new Bow, "sword" -> new Sword)
  )
  val axe3 = factory.provide("axe") // the client uses a factory kit to provide products
  val axe4 = factory.provide("axe") // the client uses a factory kit to provide products
  println(axe3) // Some(Axe)
  println(axe4) // Some(Axe)
  println(axe3 == axe4) // false

}
