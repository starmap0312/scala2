package design_pattern

// https://github.com/josephguan/scala-design-patterns/tree/master/structural/adapter
// the pattern converts the interface of a class into another interface that the client expects

// target
//   it defines the domain-specific interface that client uses
trait Arm {
  def fire(): String
}

class AK47 extends Arm {
  override def fire(): String = "fire with AK47"
}

// adaptee
//   an existing interface that needs adapting (knife is not a subclass of Arm)
class Knife {

  def stab(): String = "stab with knife"
}

class Soldier {

  def fightWith(weapon: Arm): String = {
    weapon.fire()
  }
}

// client
//   it collaborates with objects conforming to the target interface
object AdapterApp extends App {
  val soldier = new Soldier()
  println(soldier.fightWith(new AK47())) // fire with AK47

  // an implicit adapter class Knife2ArmAdapter(knife: Knife) in the package object
  // so that the Knife is implicitly wrapped as an Knife2ArmAdapter object, which is also an Arm, and passed to the Soldier
  println(soldier.fightWith(new Knife())) // stab with knife
  // the soldier can now fight with a Knife which is not an Arm
}
