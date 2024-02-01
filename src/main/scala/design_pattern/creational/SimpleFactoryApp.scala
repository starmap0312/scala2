package design_pattern.creational

// https://github.com/josephguan/scala-design-patterns/tree/master/creational/simple-factory
// the pattern allows you to create a system in which its client is independent of how the products are created, composed, and represented
// the pattern is similar to the factory kit pattern

// product interface
trait Operation {
  def getResult(a: Double, b: Double): Double
}

// concrete products
class AddOperation extends Operation {
  override def getResult(a: Double, b: Double): Double = {
    a + b
  }
}

class SubOperation extends Operation {
  override def getResult(a: Double, b: Double): Double = {
    a - b
  }
}

class MulOperation extends Operation {
  override def getResult(a: Double, b: Double): Double = {
    a * b
  }
}

class DivOperation extends Operation {
  override def getResult(a: Double, b: Double): Double = {
    if (b == 0) throw new Exception("b can not be zero")
    a / b
  }
}

// product companion (simple factory, creator)
//   it creates objects without exposing the instantiation logic to the client
//   the companion object's apply method is the best place to implement the static factory method of a simple factory
//     it is used to create different concrete operations
object Operation {

  sealed trait Op
  case object Add extends Op
  case object Sub extends Op
  case object Mul extends Op
  case object Div extends Op

  // note: we could define as apply(), so that the client create an operation via, ex. Operation("*")
  def create(op: Op) = op match {
    case Add => new AddOperation()
    case Sub => new SubOperation()
    case Mul => new MulOperation()
    case Div => new DivOperation()
  }
}

// client
object SimpleFactoryApp extends App {
  // the client uses a simple factory to create products (the product interface provides such a static method for creation)
  val operation = Operation.create(Operation.Mul)
  val result = operation.getResult(2, 3)
  println(result) // 6.0
}
