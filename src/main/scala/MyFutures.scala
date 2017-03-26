import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
// Scala Future:
// 1) a Future represents a value which may not currently be available
// 2) will be available at some point, or an exception if that value could not be made

//import scala.concurrent.Future

object MyFuture { // Future companion object

}

trait MyFuture[+T] {

  // returns whether the future had already been completed with a value or an exception
  def isCompleted: Boolean

  // returns the current value of this Future (Some/None)
  def value: Option[MyTry[T]]

  // when this future is completed (either through a value or an exception), apply the provided function
  def onComplete[U](f: MyTry[T] => U): Unit

  // When this future is completed successfully (i.e., with a value),
  //   apply the provided partial function to the value if the partial function is defined at that value
  def onSuccess[U](pf: PartialFunction[T, U]): Unit = onComplete {
    case MySuccess(v) =>
      pf.applyOrElse[T, Any](v, Predef.identity[T]) // Exploiting the cached function to avoid MatchError
    case _ =>
  }

  def onFailure[U](pf: PartialFunction[Throwable, U]): Unit = onComplete {
    case MyFailure(t) =>
      pf.applyOrElse[Throwable, Any](t, Predef.identity[Throwable]) // Exploiting the cached function to avoid MatchError
    case _ =>
  }

  // creates a new Future by applying the specified function to the result of this Future
  // if there is no exception thrown when $f is applied then that exception will be propagated to the resulting future
  def transform[S](f: MyTry[T] => MyTry[S]): MyFuture[S]

  def transformWith[S](f: MyTry[T] => MyFuture[S]): MyFuture[S]

  // creates a new future by applying a function to the successful result of this future
  // if this future is completed with an exception then the new future will also contain this exception
  def map[S](f: T => S): MyFuture[S] = {
    transform(_ map f)
  }

  // creates a new future by applying a function to the successful result of this future
  // returns the result of the function as the new future.
  // if this future is completed with an exception then the new future will also contain this exception
  def flatMap[S](f: T => MyFuture[S]): MyFuture[S] = transformWith {
    case MySuccess(s) => f(s)
    case MyFailure(_) => this.asInstanceOf[MyFuture[S]]
  }

  // creates a new future by filtering the value of the current future with a predicate
  def filter(p: T => Boolean): MyFuture[T] = {
    map {
      r => if (p(r)) r else {
        throw new NoSuchElementException("Future.filter predicate is not satisfied")
      }
    }
  }
}

object MyFutures {
  def main(args: Array[String]): Unit = {


  }
}
