package design_pattern.creational

// https://github.com/josephguan/scala-design-patterns/tree/master/creational/abstract-factory
// the pattern allows the client to use an abstract factory to create a family of related products without specifying their concrete classes
//    the client is independent of how its products are created, composed, and represented
//    the client uses a factory to create a family of related products
// the pattern is similar to the cake pattern

//-------------------
// Engines
//-------------------
// abstract product
//   an interface for a type of products
trait Engine {
  def start(): Unit
}

// concrete products
//   it defines a concrete product created by a corresponding concrete factory
class V6Engine extends Engine {
  override def start(): Unit = println("Vroom Vroom Vroom... V6 Engine started.")
}

class V8Engine extends Engine {
  override def start(): Unit = println("Vroom Vroom Vroom... V8 Engine started.")
}

//-------------------
// Wheels
//-------------------
trait Wheel {
  def rotate(): Unit
}

class MichelinWheel extends Wheel {
  override def rotate(): Unit = println("Michelin wheel rotated.")
}

class DunlopWheel extends Wheel {
  override def rotate(): Unit = println("Dunlop wheel rotated.")
}

//-------------------
// Engine
//-------------------
trait Brand {
  def light(): Unit
}

class AudiBrand extends Brand {
  override def light(): Unit = println("I am Audi.")
}

class BMWBrand extends Brand {
  override def light(): Unit = println("I am BMW.")
}

// abstract factory
//   it declares an interface with abstract factory methods that create abstract products
trait CarFactory {
  def createEngine(): Engine

  def createWheel(): Wheel

  def createBrand(): Brand
}

// concrete factories
//   a concrete factory is often a singleton, you can use `object` to make it
//   it implements all the factory methods to create concrete products
object AudiCarFactory extends CarFactory {
  override def createEngine(): Engine = new V6Engine()

  override def createWheel(): Wheel = new DunlopWheel()

  override def createBrand(): Brand = new AudiBrand
}

object BMWCarFactory extends CarFactory {
  override def createEngine(): Engine = new V8Engine()

  override def createWheel(): Wheel = new MichelinWheel()

  override def createBrand(): Brand = new BMWBrand
}

// client (creator interface)
//   it uses only interfaces of abstract factories to create its abstract products
class AutoCar(factory: CarFactory) {
  val engine = factory.createEngine()
  val wheel = factory.createWheel()
  val brand = factory.createBrand()

  def drive(): Unit = {
    brand.light()
    engine.start()
    wheel.rotate()
  }
}

object AbstractFactoryApp extends App {
  //  at run-time, a particular dependency is constructed
  val audi = new AutoCar(AudiCarFactory)
  audi.drive()

  val bmw = new AutoCar(BMWCarFactory)
  bmw.drive()
}
