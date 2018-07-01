package functional_program_design_in_scala

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

// 1) a Monad is a class with flatMap & unit methods that satisfy 3 Monad laws
trait Monad[T] {
  def flatMap[U](f: T => Monad[U]): Monad[U]
}
// def unit[T](x: T): Monad[T]
// ex.
//   List is a monad with      unit(x) =   List(x)
//   Set is a monad with       unit(x) =    Set(x)
//   Option is a monad with    unit(x) =   Some(x)
//   Generator is a monad with unit(x) = single(x)
//
// 2) map() can be defined for every monad as a combination of flatMap and unit:
// i.e.
//   m map f == m flatMap { x => unit(f(x))}
//           == m flatMap (f andThen unit)
//           == m flatMap (f _ andThen unit _)
//
// 3) 3 Monad laws:
//   associativity law:
//     ((m flatMap f) flatMap g) == m flatMap (x => (f(x) flatMap g))
//   left unit law:
//     unit(x) flatMap f == f(x)
//   right unit law:
//     m flatMap unit    == m
// ex.
abstract class Option[+T] {
  def flatMap[U](f: (T) => Option[U]): Option[U] = this match {
    case Some(x) => f(x)
    case None => None
  }
  // map can be defined using flatMap & unit
  def map[U](f: (T) => U): Option[U] = this flatMap { x => Some(f(x)) }
}
case class Some[T](x: T) extends Option[T]
case object None         extends Option[Nothing] // Nothing is a Bottom Type, i.e. a subtype of all types
// Unlike Java, which allows raw types, Scala requires that you specify type parameters
// ex. val list:  List          = List(1) ... compile error: type List takes type parameters
//     val empty: List[Nothing] = List()  ... as Nothing is bottom type, empty list can be up-casted to List[T] for any type T
//     similarly, None can be up-casted to Option[T] for any type T
// ex. val intList: List[Int] = List(1) ::: Nil
//     Nil is of type List[Nothing], so the concatenation can be up-casted to type List[Int]
// ex. def ex: Nothing = throw new Exception("s") ... def ex is of type Nothing

object Try {
  def apply[T](expr: => T): Try[T] = {
    try { // a non-Fatal error is caught by the unit method
      Success(expr)
    } catch {
      case NonFatal(ex) => Failure(ex)
    }
  }
}
abstract class Try[+T] {
  def flatMap[U](f: T => Try[U]): Try[U] = this match {
    case Success(x) => {
      try { // a non-Fatal error is caught by the flatMap method
        f(x)
      } catch {
        case NonFatal(ex) => Failure(ex)
      }
    }
    case failure: Failure => failure
  }
  def map[U](f: T => U): Try[U] = this match {
    case Success(x) => Try(f(x))
    case failure: Failure => failure
  }
}
case class Success[T](x: T)       extends Try[T]
case class Failure(ex: Throwable) extends Try[Nothing]
// Failure is a Try[Nothing], so it has flatMap() method implemented ... flatMap a Failure returns a Failure

// Future[T] is a Monad that handles latency and failure
object Future {
  //def apply[T](body: => T)(implicit executor: ExecutionContext): Future[T]
  // the constructor starts an asynchronous computation and immediately returns a Future, with which you can register a callback
}
trait Future[T] {
  def onComplete(callback: Try[T] => Unit)(implicit executor: ExecutionContext): Unit
  // ex. future onComplete {
  //       case Success(value) =>
  //       case Failure(ex)    =>
  //     }
}

object Monads extends App {
  // 1) 3 Monad Laws:
  // 1.1) left unit law
  def f(x: Int) = Some(x * 2)
  val opt1 = Some(1) flatMap f
  val opt2 = f(1)
  println(opt1) // Some(2)
  println(opt2) // Some(2)

  // 1.2) right unit law
  val opt3 = opt1 flatMap {x => Some(x)}
  val opt4 = opt1
  println(opt3) // Some(2)
  println(opt4) // Some(2)

  // 1.3) associativity law:
  def g(x: Int) = Some(x + 3)
  val opt5 = ((opt1 flatMap f) flatMap g)
  val opt6 = opt1 flatMap (x => (f(x) flatMap g))
  println(opt5) // Some(7)
  println(opt6) // Some(7)

  // 2) benefits of following 3 Monad Laws:
  // 2.1) associativity law simplifies the for-expression
  val opt7 = for {
    y <- (for (x <- Some(1); y <- Some(x + 2)) yield y) // a nested for-expression
    z <- Some(y + 3)
  } yield {
    z
  }
  // we can inline the nested for-expression as follows:
  val opt8 = for {
    x <- Some(1)
    y <- Some(x + 2)
    z <- Some(y + 3)
  } yield {
    z
  }
  println(opt7) // Some(6)
  println(opt8) // Some(6)

  // 3) Try
  // 3.1) for-expression
  val try1 = for {
    x <- Try(1)
    y <- Try(2)
  } yield (x + y)
  val try2 = for {
    x <- Try(1)
    y <- Try[Int](throw new Exception)
  } yield (x + y)
  println(try1) // Success(3)
  println(try2) // Failure(java.lang.Exception)
  // 3.2) Try is not a Monad, i.e. it does not follow left unit law
  //      Try(expr) flatMap f != f(expr)
  //      the left-hand side never raises a non-fatal exception, whereas
  //      the right-hand side may raise a non-fatal exception by f or expr
  def expr1: String = throw new RuntimeException
  def f1(x: String) = Try("success")
  def expr2: String = "success"
  def f2(x: String): Try[String] = throw new RuntimeException
  val try3 = Try {
    expr1 // throws a non-fatal exception
  } flatMap f1
  println(try3) // Failure(java.lang.RuntimeException), non-fatal exception is caught and wrapped in Failure
  //val try3 = f1(expr1) // throw java.lang.RuntimeException
  val try4 = Try {
    expr2
  } flatMap f2 // throws a non-fatal exception
  println(try4) // Failure(java.lang.RuntimeException), non-fatal exception is caught and wrapped in Failure
  //val try4 = f2(expr2) // throw java.lang.RuntimeException

  // bullet-proof principle:
  //   an expression wrapped by Try and then be flatMap & map will never throw an non-fatal exception
}
