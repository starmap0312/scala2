// type PartialFunction[A, B]:
// 1) it is a subclass of Function1[A, B]: i.e. A => B
//    it takes only single parameter
// 2) a unary function (one parameter) where the domain does not necessarily include all values of type A
// 3) isDefinedAt():
//    it allows use to test dynamically if a value is in the domain of the function
//    it is the responsibility of the caller to call isDefinedAt() before calling apply()
//      because if isDefinedAt() is false, it is not guaranteed apply() will throw an exception to indicate an error condition
//      if an exception is not thrown, evaluation may result in an arbitrary value
// why use PartialFunction instead of Function1?
//   the user of a PartialFunction may choose to do something different with input that is declared to be outside its domain
//   ex. use its method orElse() to chain another partial function to handle input outside the declared domain

/*
object MyPartialFunction {

  // fallback_pf is used as both unique marker object and special fallback function that returns it
  private[this] val fallback_pf: MyPartialFunction[Any, Any] = {
    case _ => fallback_pf
  }
  private def checkFallback[B] = fallback_pf.asInstanceOf[MyPartialFunction[Any, B]]
  private def fallbackOccurred[B](x: B) = (fallback_pf eq x.asInstanceOf[AnyRef])

  private class OrElse[-A, +B] (f1: MyPartialFunction[A, B], f2: MyPartialFunction[A, B]) {

    def apply(x: A): B = {
      f1.applyOrElse(x, f2)
    }

    def isDefinedAt(x: A): Boolean = {
      f1.isDefinedAt(x) || f2.isDefinedAt(x)
    }

    // applies this partial function to the given argument when it is contained in the function domain
    // or applies fallback function where this partial function is not defined
    def applyOrElse[A1 <: A, B1 >: B](x: A1, default: A1 => B1): B1 = {
      val z = f1.applyOrElse(x, checkFallback[B])
      if (!fallbackOccurred(z)) z else f2.applyOrElse(x, default)
    }

    def orElse[A1 <: A, B1 >: B](that: MyPartialFunction[A1, B1]) = {
      new OrElse[A1, B1](f1, f2 orElse that)
    }
  }
}

trait MyPartialFunction[-A, +B] extends Function1[A, B] {
  // as it extends Function1, it already has compose() method implemented, and apply() is declared

  import MyPartialFunction._

  def isDefinedAt(x: A): Boolean

  // composes this partial function with a fallback partial function which gets applied where this partial function is not defined
  def orElse[A1 <: A, B1 >: B](that: MyPartialFunction[A1, B1]): MyPartialFunction[A1, B1] = {
    new OrElse[A1, B1](this, that)
  }

  def applyOrElse[A1 <: A, B1 >: B](x: A1, default: A1 => B1): B1 = {
    if (isDefinedAt(x)) {
      apply(x)
    } else {
      default(x)
    }
  }
}
*/
object MyPartialFunctions {
  def main(args: Array[String]): Unit = {

  }
}
