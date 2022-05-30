package design_pattern.creational

import scala.collection.mutable

// https://github.com/josephguan/scala-design-patterns/tree/master/creational/builder
// the pattern separates the construction of a complex object from its representation
//   thus, the same construction process can create different representations
// the pattern is similar to the factory kit pattern in that:
//   it allows the client to use the builder (factory) to create (complex) products
// the pattern allows you to decouple the construction of a product from the product business logic

// product
//   it represents the complex object under construction
case class Motor(engine: String, wheels: Int, color: String)

// builder interface
//   it specifies an abstract interface for creating parts of a complex product
trait MotorBuilder {

  def setEngine(engine: String): MotorBuilder

  def setWheels(wheels: Int): MotorBuilder

  def setColor(color: String): MotorBuilder

  def build(): Motor
}

// concrete builder
//   it constructs parts of the complex product by implementing the builder interface
class DIYMotorBuilder extends MotorBuilder {
  
  private var engine = ""
  private var wheels = 0
  private var color = ""

  override def setEngine(engine: String): MotorBuilder = {
    this.engine = engine
    this
  }

  override def setColor(color: String): MotorBuilder = {
    this.color = color
    this
  }

  override def setWheels(wheels: Int): MotorBuilder = {
    this.wheels = wheels
    this
  }

  // it provides an interface for retrieving the built product
  override def build(): Motor = {
    Motor(engine, wheels, color)
  }
}

// director (client)
//   it constructs a complex object using the builder interface
object BuilderApp extends App {
  val car = new DIYMotorBuilder().setEngine("V6").setWheels(4).setColor("Red").build()
  println(car) // Motor(V6, 4, Red)

  // other example
  val builder: mutable.Builder[(String, Int), Map[String, Int]] = Map.newBuilder[String, Int]
  builder.addOne("one" -> 1)
  builder.addOne("two" -> 2)
  builder.addOne("three" -> 3)
  val mp: Map[String, Int] = builder.result()
  println(mp) // Map(one -> 1, two -> 2, three -> 3)
}
