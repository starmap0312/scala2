package design_pattern

// https://github.com/josephguan/scala-design-patterns/tree/master/creational/builder
// the pattern separates the construction of a complex object from its representation
//   thus, the same construction process can create different representations

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

// director
//   it constructs a complex object using the builder interface
object BuilderApp extends App {
  val car = new DIYMotorBuilder().setEngine("V6").setWheels(4).setColor("Red").build()
  println(car) // Motor(V6, 4, Red)
}
