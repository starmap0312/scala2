package design_pattern

// https://github.com/josephguan/scala-design-patterns/tree/master/other/monad
// the pattern allows you to chain operations (functions) together, applied to the testee (wrapped value) one by one
// Binding functions can be described as passing one's output to another's input basing on the 'same type' contract.
// monad
object Validator {

  // wrap a value (testee) in a monadic value
  def apply[A](testee: A): Validator[A] = new Valid[A](testee)
}

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
