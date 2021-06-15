package design_pattern.creational

// https://github.com/josephguan/scala-design-patterns/tree/master/creational/cake
// Scala's answer to dependency injection (DI)
//   the pattern separates the creation of a client's dependencies from its own behavior
//   this allows program designs to be loosely coupled and to follow the inversion of control and single responsibility principles
// the pattern is similar to abstract factory in that:
//   the client has no knowledge of how these products are implemented: it operates on abstract products
//   it differs in that the registry specifies the concrete products instead of the factories
//     a registry is a trait to be mixed-in, whereas a factory is a concrete class of abstract factory

//-------------------
// Engine Component
//-------------------
// component
//   it maintains a reference to an object of a type abstract product, ex. Engine
trait EngineComponent {
  val engine: Engine

  // abstract product
  //   it declares an interface for a type of product object
  trait Engine {
    def start(): Unit
  }

  // concrete products
  //   it implements the abstract product interface
  class V6Engine extends Engine {
    override def start(): Unit = println("Vroom Vroom Vroom... V6 Engine started.")
  }

  class V8Engine extends Engine {
    override def start(): Unit = println("Vroom Vroom Vroom... V8 Engine started.")
  }

}

//-------------------
// Wheel Component
//-------------------
trait WheelComponent {
  val wheel: Wheel

  trait Wheel {
    def rotate(): Unit
  }

  class MichelinWheel extends Wheel {
    override def rotate(): Unit = println("Michelin wheel rotated.")
  }

  class DunlopWheel extends Wheel {
    override def rotate(): Unit = println("Dunlop wheel rotated.")
  }

}

//-------------------
// Brand Component
//-------------------
trait BrandComponent {

  val brand: Brand

  trait Brand {
    def light(): Unit
  }

  class AudiBrand extends Brand {
    override def light(): Unit = println("I am Audi.")
  }

  class BMWBrand extends Brand {
    override def light(): Unit = println("I am BMW.")
  }

}

// registry
//   it combines all related components into one trait for the client
//   the creation of a client's dependencies is defined separately in the registry
trait AudiCarComponentRegistry extends EngineComponent with WheelComponent with BrandComponent {
  override val engine: Engine = new V6Engine()
  override val wheel: Wheel = new DunlopWheel()
  override val brand: Brand = new AudiBrand()
}

trait BMWCarComponentRegistry extends EngineComponent with WheelComponent with BrandComponent {
  override val engine: Engine = new V8Engine()
  override val wheel: Wheel = new MichelinWheel()
  override val brand: Brand = new BMWBrand()
}

// the pattern follows the inversion of control and single responsibility principles
//   it allows you define mock objects or stubs that can be used in unit tests
//   ex. you could define a new trait MockCarComponentRegistry for testing

// client
abstract class Car {
  self: EngineComponent with WheelComponent with BrandComponent =>
  // use Scala self-types to declare that the trait must be mixed into another traits
  //   this makes the members of the dependency available without imports
  // i.e. Car must be mixed into traits of EngineComponent, WheelComponent, and BrandComponent
  //      therefore, it has value engine, wheel, and brand

  // client's behavior which has dependencies on several products (components)
  // it has no knowledge of how these products are implemented
  def drive(): Unit = {
    brand.light()
    engine.start()
    wheel.rotate()
  }
}

object CakeApp extends App {
  // mix-in ComponentRegistry when creating a new client object
  // ex. to create an Audi car, just mix-in AudiCarComponentRegistry into a Car object
  val audi = new Car with AudiCarComponentRegistry
  audi.drive()
  //  I am Audi.
  //    Vroom Vroom Vroom... V6 Engine started.
  //    Dunlop wheel rotated.

  val bmw = new Car with BMWCarComponentRegistry
  bmw.drive()
  // I am BMW.
  // Vroom Vroom Vroom... V8 Engine started.
  // Michelin wheel rotated.
}
