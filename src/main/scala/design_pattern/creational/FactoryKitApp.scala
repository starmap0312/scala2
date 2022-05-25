package design_pattern.creational

import scala.collection.mutable

// https://github.com/josephguan/scala-design-patterns/tree/master/creational/factory-kit
// this pattern allows to create a factory kit via a separated builder and create immutable products through its create method
//   the factory class can't anticipate what products it must create
//   you want many custom builders instead of a global one
//   the factory keeps a Map of lambda functions used to create the products
// this pattern is similar to loan pattern in that:
//   the factory loans its box (a resource) to a function of the client to build its box
//   it differs in that the resource (box) is initiated inside the load function instead of provided by the client
// this pattern is similar to simple factory pattern in that:
//   it differs in that the client uses a dedicated factory to create products, instead of a static method defined in product interface
//   it differs in that the factory interface also provides a static method for the client to construct the factory

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
class WeaponFactory(box: Map[String, Weapon]) {
  // use an immutable Map to create products (the Map is built by the client's builder function)
  // note: if we need to initiate a new product through the factory kit each time, we could define a Map[String, () => Weapon], instead of Map[String, Weapon]

  def create(key: String): Option[Weapon] = {
    box.get(key) // create a product
  }
}

// factory method that creates a factory kit (factory method) for the products
object WeaponFactory {

  // the factory interface provides a static method for the client to construct the factory
  def newFactory(builderFn: mutable.Map[String, Weapon] => Unit): WeaponFactory = { // load function
    val mp = mutable.Map[String, Weapon]() // the resource (box) is initiated inside the load function instead of provided by the client
    builderFn(mp)
    new WeaponFactory(mp.toMap)
  }
}

// client
object FactoryKitApp extends App {
  val factory: WeaponFactory = WeaponFactory.newFactory({ mp => // customize a builder for factory kit
    mp.put("axe", new Axe())
    mp.put("bow", new Bow())
    mp.put("sword", new Sword())
  })

  val axe1 = factory.create("axe") // the client uses a factory kit to create products
  val axe2 = factory.create("axe") // the client uses a factory kit to create products
  println(axe1) // Some(Axe)
  println(axe2) // Some(Axe)
  println(axe1 == axe2) // false

  // alternatively, the client uses the original constructor
  val factory2: WeaponFactory = new WeaponFactory(
    Map("axe" -> new Axe, "bow" -> new Bow, "sword" -> new Sword)
  )
  val axe3 = factory.create("axe") // the client uses a factory kit to create products
  val axe4 = factory.create("axe") // the client uses a factory kit to create products
  println(axe3) // Some(Axe)
  println(axe4) // Some(Axe)
  println(axe3 == axe4) // false

}
