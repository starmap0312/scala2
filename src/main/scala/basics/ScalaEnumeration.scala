package basics

// ref: https://www.baeldung.com/scala/enumerations

// example: an enumeration to represent the fingers
object Fingers extends Enumeration {
  type Finger = Value // Value is an abstract class which represents the type of the enumerated values

  val Thumb, Index, Middle, Ring, Little = Value // this creates 5 Value instances with different id
}

class FingersOperation {

  def isShortest(finger: Fingers.Finger) = finger == Fingers.Little
}

object ScalaEnumeration extends App {
  // Retrieving the Values
  val operation = new FingersOperation()

  println(Fingers.Thumb.id, Fingers.Index.id, Fingers.Middle.id, Fingers.Ring.id, Fingers.Little.id) // (0,1,2,3,4)
  println(operation.isShortest(Fingers.Little)) // true
  println(operation.isShortest(Fingers.Index))  // false
}
