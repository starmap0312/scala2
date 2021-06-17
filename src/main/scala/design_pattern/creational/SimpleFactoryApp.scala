package design_pattern.creational

// https://github.com/josephguan/scala-design-patterns/tree/master/creational/simple-factory
// the pattern allows you to create a system in which its client is independent of how the products are created, composed, and represented

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

// product companinion (simple factory, creator)
//   it creates objects without exposing the instantiation logic to the client
//   the companion object's apply method is the best place to implement the static factory method of a simple factory
//     it is used to create different concrete operations
object Operation {
  def apply(op: String) = op match {
    case "+" => new AddOperation()
    case "-" => new SubOperation()
    case "*" => new MulOperation()
    case "/" => new DivOperation()
  }
}

// client
object SimpleFactoryApp extends App {
  val op = Operation("*") // the client uses a simple factory to create products
  val result = op.getResult(1, 2)
  println(result) // 2.0
}
