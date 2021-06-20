package design_pattern.structural

import scala.collection.mutable

// https://github.com/josephguan/scala-design-patterns/tree/master/structural/flyweight

// Flyweight Interface
trait Tea {
  // name is intrinsic state, shared state
  val name: String

  // table is extrinsic state, unshared state
  def serve(table: Int): Unit = {
    println(s"Serving $name to table# $table. hashCode: $hashCode")
  }
}

// concrete flyweight
class GreenTea extends Tea {
  override val name: String = "Green Tea"
}

// unshared concrete flyweight
class UnsharedTea extends Tea {
  override val name: String = "Unshared Tea"

  // unshared state
  val price = 10

  override def serve(table: Int): Unit = {
    println(s"Serving $name to table# $table. Price is $price. hashCode: $hashCode")
  }
}

// Tea type
object Tea {

  trait Type

  case object GreenTea extends Type

  case object UnsharedTea extends Type

}

// This is what Tea will look like if it is not a Flyweight
//trait Tea {
//  val name: String
//  val table: Int  // create a object for each table
//  def serve(): Unit
//}

// Flyweight Factory
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
          teaPool.put(teaType, tea)
          tea
        case Tea.UnsharedTea =>
          new UnsharedTea()
      }
    }
  }

  def makeTeaInPool(teaType: Tea.Type, tea: Tea): Tea = {
    teaPool.put(teaType, tea)
    tea
  }
}

class TeaShop {
  private val orders = mutable.LinkedHashMap[Int, Tea]()
  private val maker = new TeaMaker()

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
