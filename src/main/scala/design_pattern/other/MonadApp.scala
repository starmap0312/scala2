package design_pattern.other

// https://github.com/josephguan/scala-design-patterns/tree/master/other/monad
// the pattern allows you to chain operations (functions) easily, applied to a wrapped value (testee) one by one
//   binding functions can be described as passing one's output to another's input basing on the 'same type' contract
// the pattern is a realization of template method pattern in that:
//   the abstract monad defines the template methods, ex. validate or flatMap that uses some primitive operations, ex. isValid, get

// monad's companion object
//   it defines a constructor method (apply) that wraps a plain type object in a monadic value
object Validator {

  // wrap a value (testee) in a monadic value
  def apply[A](testee: A): Validator[A] = new Valid[A](testee)
}

// abstract monad: Monad[T]
//   it defines a function (get) that returns the wrapped plain type object
//   it defines functions (flatMap) that takes another function that returns a monadic value
trait Validator[+A] {

  protected val isValid: Boolean

  def get: A // get the wrapped value

  def validate(p: A => Boolean): Validator[A] = { // filter
    if (!isValid) Illegal
    else if (!p(this.get)) Illegal
    else this
  }

  def flatMap[B](f: A => Validator[B]): Validator[B] = {
    // apply a function to the wrapped value & returns a monadic value
    if (isValid) f(this.get) else Illegal
  }
}

// concrete monad
case class Valid[+A](testee: A) extends Validator[A] {
  override protected val isValid: Boolean = true
  override def get: A = testee
}

case object Illegal extends Validator[Nothing] {
  override protected val isValid: Boolean = false
  override def get = throw new IllegalStateException()
}

object MonadApp extends App {
  Validator("hello").validate(_.length > 6).flatMap(x => Valid(x.substring(6))) match {
    case Valid(s) => println(s)
    case Illegal => println("Illegal") // Illegal
  }

  Validator("hello world").validate(_.length > 6).flatMap(x => Valid(x.substring(6).length)) match {
    case Valid(s) => println(s) // 5
    case Illegal => println("Illegal")
  }
}
