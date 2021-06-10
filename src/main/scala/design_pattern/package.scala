package object design_pattern {

  // adapter
  //   it adapts the interface of adaptee to the target interface
  implicit class Knife2ArmAdapter(knife: Knife) extends Arm {
    override def fire(): String = knife.stab()
  }
}
