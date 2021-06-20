package design_pattern.structural

// https://github.com/josephguan/scala-design-patterns/tree/master/structural/facade
// the pattern is similar to the bridge pattern in that:
//   it introduces an abstraction layer to decouple the subsystems from the client
// the pattern is similar to the abstract factory or the cake pattern or in that:
//   the facade (abstraction) relies on a family of related subsystems (components, registry) to function properly

// subsystem classes (implementor)
//   it implements subsystem functionality performing primitive operations
//   the subsystem can get more complex as it evolves
class CPU {
  def powerOn(): Unit = println("CPU is powered on.")
}

class Fan {
  def run(): Unit = println("Fan is running.")
}

class Light {
  def flash(): Unit = println("Light is flashing.")
}

// facade (abstraction)
//   it provide a unified interface to a set of subsystems, a higher-level interface that makes the subsystems easier to use
//   it knows which subsystem classes are responsible for a request and delegates the request to the appropriate subsystems
class ComputerFacade {
  private val cpu = new CPU
  private val fan = new Fan
  private val light = new Light

  def powerOn():Unit = {
    cpu.powerOn()
    fan.run()
    light.flash()
  }
}

// client
object FacadeApp extends App {
  val computer = new ComputerFacade()
  computer.powerOn()
  // CPU is powered on.
  // Fan is running.
  // Light is flashing.
}
