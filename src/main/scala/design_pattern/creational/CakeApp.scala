package design_pattern.creational

// https://github.com/josephguan/scala-design-patterns/tree/master/creational/cake
// Scala's answer to dependency injection (DI)
//   the pattern separates the creation of a client's dependencies from its own behavior
//   this allows program designs to be loosely coupled and to follow the "inversion of control" and single responsibility principles
// the pattern is similar to abstract factory in that:
//   the client has no knowledge of how these products are implemented: it operates on abstract products
//   it differs in that a registry is a trait to be mixed-in, whereas a factory is a class used to create products
// the pattern is similar to bridge pattern in that:
//   it separates the component class (implementor) from the client class (abstraction)
// the pattern is similar to facade pattern

//-------------------
// Engine Component
//-------------------
// component (implementor)
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

// client (abstraction)
//   dependency injection: the dependences or callbacks are injected to the client via mix-in a central registry class
abstract class Car {
  self: EngineComponent with WheelComponent with BrandComponent =>
  // use Scala self-types to declare that the trait must be mixed into another traits
  //   this makes the members of the dependency available without imports
  // i.e. Car must be mixed into traits of EngineComponent, WheelComponent, and BrandComponent
  //      therefore, it has value engine, wheel, and brand
  // self-types example:
  //    trait A { def name: String }
  //    1) class B { self: A => }
  //       self-types means that B `requires` A, which means:
  //       a) when B is extended, you are required to mix-in trait A
  //          otherwise, you got compile error: self-type C does not conform to B's self-type B with A
  //          ex. class C extends B with A { override def name: String = "john" }
  //       b) when B is instantiated, you are required to implement trait A
  //          ex. val c = new B with A { override def name: String = "john" }
  //    2) class B extends A
  //       subclassing means that B `is an` A, which means:
  //       b) when B is instantiated, you are required to implement trait A
  //          ex. class C extends B { override def name: String = "john" }
  //          ex. val c = new B { override def name: String = "john" }

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
  //   dependency injection: the dependences or callbacks are injected to the client via mix-in a central registry class
  // dependency injection: when a client uses some products, the binding between the client and the products is established at run-time
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
