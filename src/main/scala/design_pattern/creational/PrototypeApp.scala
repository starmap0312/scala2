package design_pattern.creational

// https://github.com/josephguan/scala-design-patterns/tree/master/creational/prototype
// the pattern allows you to specify the kinds of objects to create using a prototypical instance
//   i.e. you can create new objects by copying this prototype object
// it avoids building a class hierarchy of factories that parallels the products class hierarchy
//   ex. use it when instances of a class can have one of only a few different combinations of state

// prototype
//   it declares an interface for cloning itself
trait Prototype extends Cloneable {
  override def clone(): AnyRef = super.clone()
}

// concrete prototype
//   it implements the operation of cloning itself
class Virus(var name: String, var character: String) extends Prototype {
  override def clone(): Virus = new Virus(name, character)
}

// concrete prototype
//   Scala `case class` is a kind of prototype
case class Bacteria(name:String, character: String)

// client
//   it creates a new object by asking a prototype to clone itself
object PrototypeApp extends App {
  val virus = new Virus("HIV", "BAD")
  println(virus == virus.clone())

  val bacteria = Bacteria("LAB", "GOOD")
  println(bacteria == bacteria.copy())
}
