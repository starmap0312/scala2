package basics

// https://www.tutorialspoint.com/difference-between-traits-and-abstract-classes-in-scala
// https://medium.com/swlh/the-practical-difference-between-abstract-classes-and-traits-in-scala-4e2fb8e03be
// 0:
//   trait: similar to Java interface, but allows partially implemented
//   abstract class: similar to Java abstract classes which allows partially implemented
// 1:
//   trait: does NOT allow constructor parameters!!
//   abstract class: allow constructor parameters
// 2:
//   trait: support "multiple" inheritance!!
//   abstract class: does not support multiple inheritance
// 3:
//   trait: an object instance can have a trait added to it!! (note all the abstract member of the trait must be defined in the instance)
//   abstract class: an object instance cannot have an abstract class added to it
// 4:
//   trait: completely inter-operable with Java if it does not contain any implementation code!! (i.e. we can treat trait as a Java interface if all members left undefined/abstract)
//   abstract class: completely inter-operable with Java code
// 5:
//   trait: super calls are "dynamically bound" so it is stackable, ex. extends A with B with C, then you can use 'super' in C to refer to the stacked (A with B)
//   abstract class: super calls are "statically bound" so it is NOT stackable

trait TraitA {
  def name: String

  def method = { // unlike Java interface, trait allows partially implemented!!
    println(s"TraitA method: $name")
  }
}

abstract class AbstractClassB(name: String) { // abstract class allows constructor parameters

  def method = {
    println(s"AbstractClassB method: $name")
  }
}

abstract class AbstractClassB2() {
  // the abstract keyword is not necessary in the field/method definition
  val name: String

  def method = {
    println(s"AbstractClassB2 method: $name")
  }
}

class SubclassA extends TraitA {
  override def name: String = "SubclassA"// must implement abstract member name
}

class SubclassA2(val str: String) extends TraitA {
  override def name: String = str // note abstract member name is defined in SubclassA2
}

class SubclassA3(val name: String) extends TraitA // note abstract member name is defined in SubclassA3
class SubclassA4(val name: String) // note abstract member name is defined in SubclassA4

class SubclassB extends AbstractClassB("SubclassB")
class SubclassB2 extends AbstractClassB2 {
  override val name: String = "SubclassB2"
}

object ScalaTraitVsAbstractClass extends App {

  val subclassA = new SubclassA()
  val subclassA2 = new SubclassA2("SubclassA2 constructor parameter")
  val subclassA3 = new SubclassA3("SubclassA3 constructor parameter")
  val subclassA4 = new SubclassA4("SubclassA4 constructor parameter") with TraitA // an anonymous class
  // an object instance can have a trait added to it: note the abstract member name must be defined in the instance

  subclassA.method // TraitA method: SubclassA
  subclassA2.method // TraitA method: SubclassA2 constructor parameter
  subclassA3.method // TraitA method: SubclassA3 constructor parameter
  subclassA4.method // TraitA method: SubclassA4 constructor parameter

  val subclassB = new SubclassB()
  val subclassB2 = new SubclassB2()

  subclassB.method // AbstractClassB method: SubclassB
  subclassB2.method // AbstractClassB2 method: SubclassB2
}
