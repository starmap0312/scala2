package design_pattern.structural

import scala.collection.mutable

// https://github.com/josephguan/scala-design-patterns/tree/master/structural/flyweight
// the pattern use shared state to support large numbers of fine-grained objects efficiently
// the pattern is similar to abstract factory pattern in that:
//   it introduces a factory class & the client uses the factory to create products
//   it differs in that the factory decides whether to provides a shared product from the pool or creates a new unshared product

// flyweight interface (product interface)
//   it declares an interface through which flyweights can receive and act on unshared state
trait Tea {
  // name is shared state
  val name: String

  // table is unshared state
  def serve(table: Int): Unit = {
    println(s"Serving $name to table# $table. hashCode: $hashCode")
  }
}
// This is what Tea will look like if it is not a flyweight
//trait Tea {
//  val name: String
//  val table: Int  // create a object for each table
//  def serve(): Unit
//}

// tea type
object Tea {

  trait Type

  case object GreenTea extends Type
  case object UnsharedTea extends Type
}

// shared concrete flyweight (concrete products)
//   it implements the flyweight interface and adds storage for shared state
//   any state it stores must be shared, i.e. it must be independent of the concrete flyweight object's context
class GreenTea extends Tea {
  override val name: String = "Green Tea"
}

// unshared concrete flyweight
//   not all flyweight subclasses need to be shared
//     i.e. the Flyweight interface enables sharing but it doesn't enforce it
//   it's common for unshared concrete flyweight objects to have concrete flyweight objects as children at some level in the flyweight object structure
class UnsharedTea extends Tea {

  override val name: String = "Unshared Tea"

  // unshared state
  val price = 10

  override def serve(table: Int): Unit = {
    println(s"Serving $name to table# $table. Price is $price. hashCode: $hashCode")
  }
}

// flyweight factory
//   it creates and manages flyweight objects
//   it ensures that flyweights are shared or not shared properly
//   when a client requests a flyweight, it supplies an existing instance or creates one, if none exists
class TeaMaker {
  val teaPool = mutable.Map[Tea.Type, Tea]()

  def make(teaType: Tea.Type): Tea = {
    if (teaPool.isDefinedAt(teaType)) {
      teaPool.get(teaType).get
    }
    else {
      teaType match {
        case Tea.GreenTea =>
          val tea = new GreenTea
          teaPool.put(teaType, tea) // GreenTea is shared flyweight & stored in a pool (Map)
          tea
        case Tea.UnsharedTea =>
          new UnsharedTea()
      }
    }
  }
}

// client
//   it maintains a reference to flyweights (products)
//   it computes or stores the unshared state of flyweights
class TeaShop {

  // the tea shop makes tea for customers
  //   it makes a big pot of green tea and serve several customers than make a cup of green tea for each customer
  //   but or some VIP customers, it provides unshared tea

  private val orders = mutable.LinkedHashMap[Int, Tea]() // a reference to flyweights (products)
  private val maker = new TeaMaker() // a concrete factory that creates the products

  def takeOrder(table: Int, teaType: Tea.Type): Unit = {
    orders.put(table, maker.make(teaType))
  }

  def serve(): Unit = {
    orders.foreach {
      case (table, tea) => tea.serve(table)
    }
  }
}

object FlyweightApp extends App {
  val teaShop = new TeaShop()

  teaShop.takeOrder(1, Tea.GreenTea)
  teaShop.takeOrder(2, Tea.GreenTea)
  teaShop.takeOrder(3, Tea.UnsharedTea)
  teaShop.takeOrder(4, Tea.UnsharedTea)

  teaShop.serve()
  // Serving Green Tea to table# 1. hashCode: 1060830840
  // Serving Green Tea to table# 2. hashCode: 1060830840
  // Serving Unshared Tea to table# 3. Price is 10. hashCode: 2114889273
  // Serving Unshared Tea to table# 4. Price is 10. hashCode: 1025799482
}
