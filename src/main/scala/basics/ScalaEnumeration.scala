package basics

// ref: https://www.baeldung.com/scala/enumerations
// ref: https://betterprogramming.pub/when-to-and-when-not-to-use-enums-in-java-8d6fb17ba978
// ref: https://www.baeldung.com/scala/case-objects-vs-enumerations

// example: an enumeration to represent the fingers
object Fingers extends Enumeration {
  type Finger = Value // Value is an abstract class which represents the type of the enumerated values

  val Thumb, Index, Middle, Ring, Little = Value // this creates 5 Value instances with different id

  val FINGERS = List("Thumb", "Index", "Middle", "Ring", "Little")
}

// a Type-Safe Alternative: polymorphism & case object
sealed abstract class Finger(val name: String, val id: Int) {
  def matching = println(s"match ${name}")
}
object Finger {
  case object Thumb extends Finger("Thumb", 0)
  case object Index extends Finger("Index", 1)
  case object Middle extends Finger("Middle", 2)
  case object Ring extends Finger("Ring", 3)
  case object Little extends Finger("Little", 4)
  val values: Seq[Finger] = Seq(Thumb, Index, Middle, Ring, Little)
}
class FingersOperation {

  def isShortest(finger: Fingers.Finger) = {
    println(s"the finger is the shortest finger: ${finger == Fingers.Little}")
  }

  def matching(finger: Fingers.Finger) = {
    finger match {
      case Fingers.Little => println("match Little")
      case Fingers.Index  => println("match Index")
      case Fingers.Middle => println("match Middle")
      case Fingers.Ring   => println("match Ring")
      case Fingers.Thumb  => println("match Thumb")
      case _              => println("no match")
    }
  }

  def matchingStatic(finger: String) = {
    finger match {
      case "Little" => println("match Little")
      case "Index"  => println("match Index")
      case "Middle" => println("match Middle")
      case "Ring"   => println("match Ring")
      case "Thumb"  => println("match Thumb")
      case _        => println("no match")
    }
  }

  def matchingPolymorphism(finger: Finger) = {
    // we can easily add a new type without changing this method
    finger.matching
  }
}

object ScalaEnumeration extends App {
  // 1) Why is Enum good?
  val operation = new FingersOperation()

  // Retrieving the Values
  Fingers.values.foreach(finger => println(finger))
  // Thumb Index Middle Ring Little
  Fingers.values.foreach(finger => println(finger.id))
  // 0 1 2 3 4

  // it provides type safety (compile-time type check)
  operation.matching(Fingers.Little) // match Little
  operation.matching(Fingers.Index)  // match Index

  // vs. global static member
  // no type safety
  operation.matchingStatic("Little") // match Little
  operation.matchingStatic("Index") // match Index

  println(Fingers.FINGERS) // no type safety


  operation.isShortest(Fingers.Little)
  // the finger is the shortest finger: true
  operation.isShortest(Fingers.Index)
  // the finger is the shortest finger: false

  // 2) When is Enum a code smell?
  //    When new types are added, the match function will grow
  //    which violates the Open-Closed Principle (OCP) because it must be changed whenever new types are added

  //    Enum have two big disadvantages:
  //    a) because of type erasure, all enums have the same type at runtime
  //    b) because of unrestricted inheritance, the compiler can not detect incomplete pattern matches
  Finger.values.foreach(finger => println(finger.name))
  // Thumb Index Middle Ring Little
  Finger.values.foreach(finger => println(finger.id))
  // 0 1 2 3 4
  operation.matchingPolymorphism(Finger.Little) // match Little
  operation.matchingPolymorphism(Finger.Index)  // match Index

}
