package concurrent

// Scala Future vs. Java Future (Java 5)
//   Scala Future allows for asynchronous computation without blocking more threads than necessary
// 1) for java.util.concurrent.Future, you cannot get the value without blocking
//    i.e. the only way to retrieve a value is the get() method (blocking)
// 2) for scala.concurrent.Future, you can get and map over it, ex. chain multiple Futures together in a monadic fashion

// Ref: https://www.beyondthelines.net/computing/scala-future-and-execution-context/
//
// Scala Future vs. Scala ExecutionContext
// 1) Future:
//    a Future is just a placeholder for something that does not exist yet
// 2) ExecutionContext:
//    similarly to the Java Executor, the Scala ExecutionContext allows to separate
//      the business logic (i.e. what the code does) from the execution logic (i.e. how the code is executed)
//    as a consequence, one cannot just import the global execution context and get away with that
//      instead we need to understand which execution context is needed and why
//    2.1) global execution context
//         import scala.concurrent.ExecutionContext.Implicits.global
//         it is the default one and the most easy one to setup
//    2.2) Blocking
//         import scala.concurrent.blocking
// Future {
//   blocking {
//     println(s"Starting task")
//     Thread.sleep(2000) // wait 2secs
//     println(s"Finished task")
//   }
// }
//    2.3) Other type of ExecutionContexts
//         use fromExecutor() constructor to create an ExecutionContext from any of the Java executor service
//         ex. FixedThreadPoolExecutor, SingleThreadPoolExecutor, CachedThreadPoolExecutor, ...)
//
// 3) we need an ExecutionContext in order to be able to use a Future
//    one can just use the global execution context, there are 3 ways:
//    3.1) Importing the global execution context:
//         import scala.concurrent.ExecutionContext.Implicits.global
//           i.e. implicit lazy val global: ExecutionContext
//    3.2) Using an implicit variable:
//         implicit val executor =  scala.concurrent.ExecutionContext.global
//    3.3) Passing explicitly the execution context:
//         Future { /* do something */ }(executor)
//
// trait ExecutionContext {
//   def execute(runnable: Runnable): Unit
//   ...
// }
// note: executor.execute(this) actually invokes run() which in turns execute the onComplete callback
// 3) note the callbacks are executed onto a different thread (depending on the ExecutionContext passed in)
//
// Future vs. Promise
// 1) a Promise is something that gives a Future
//    think of Future and Promise as two different sides of a pipe
//      On the Promise side, data is pushed in, and on the Future side, data can be pulled out
//       Promise -> (data) -> Future -> (data)
// 2) A Future is a placeholder for a result that can be read when available
//    A Promise is like a "writable-once" container that can be used to complete a Future with the written value
//    ("writable-once" is the reason why DefaultPromise extends AtomicReference)
//
// object Future {
//   def apply[T](body: => T)(implicit executor: ExecutionContext): Future[T] =
//    unit.map(_ => body)
// }
// note: Future.unit == Future.successful(()) == Future[Unit], i.e. a successful future containing Unit
//
// def successful[T](result: T): Future[T] = Promise.successful(result).future
// note: Future.successful(()) returns a completed promise which holds the value ()
//
// object Promise {
//   def successful[T](result: T): Promise[T] = fromTry(Success(result))
//   def fromTry[T](result: Try[T]): Promise[T] = impl.Promise.KeptPromise[T](result) // i.e. an already completed promise
// }
//
// the main pitfalls to avoid:
// 1) avoid using the global ExecutionContext for non-cpu bound tasks (ex. Prefer a dedicated ThreadPool for IO-bound tasks)
// 2) avoid not using "blocking" construct (always useful, even for "documenting" the code)
// 3) avoid not knowing the parallelism settings
// 4) avoid assuming blocking works with any ExecutionContext

import java.util.concurrent.CompletableFuture

import scala.compat.java8.FutureConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success} // a Java 8 compatibility kit for Scala

object ScalaFuture {
  def taskA(): Future[Unit] = Future {
    println("Starting taskA")
    Thread.sleep(1000) // wait 1secs
    println("Finished taskA")
  }

  def taskB(): Future[Unit] = Future {
    println("Starting taskB")
    Thread.sleep(2000) // wait 2secs
    println("Finished taskB")
  }

  def main(args: Array[String]): Unit = {
    // 0) import scala.concurrent.ExecutionContext.Implicits.global
    //    the import is needed, otherwise "Cannot find an implicit ExecutionContext" is thrown when constructing a Future
    //    the reason is because the Future.apply() requires an implicit parameter ExecutionContext:
    //      def apply[T](body: =>T)(implicit executor: ExecutionContext)
    //    future.onComplete()
    val future0 = Future {
      Thread.sleep(500)
      "future0"
    }
    future0 onComplete {
      t => println(s"${t.get} onComplete") // future0 onComplete
    }
    // future0.wait // throws IllegalMonitorStateException as the current thread is not the owner of the object monitor
    if (future0.isCompleted) {
      val result1 = future0.value.get.get // future0.value = Some(Success(future0))
      println(result1) // future0
    }
    // 0.1) Await.result([Future], [FiniteDuration]):
    //      an easier way to get the Successful value
    val result2 = Await.result(future0, 1 seconds)
    println(result2)   // future0

    // if the Future fails with exception, Await.result gets a Future(Failure)
    val result3 = Await.ready(Future { 1 / 0 }, 1 seconds)
    println(result3)   // Future(Failure(java.lang.ArithmeticException: / by zero))

    Future {       // when declaring a Future, a thread gets executed immediately
      "future0.1"
    } onComplete { // we can declare how to handle the result once the future is complete
      case Success(result) => println(s"Success($result)") // Success(future0.1)
      case Failure(ex) => println(s"Failure(${ex})")
    } // onComplete accepts a callback and is non-blocking

    Future {
      1 / 0 // this throws ArithmeticException
    } onComplete { // we can declare how to handle the result once the future is complete
      case Success(result) => println(s"Success($result)")
      case Failure(ex) => println(s"Failure(${ex})")       // Failure(java.lang.ArithmeticException: / by zero)
    }

    // 0.2) future.recover([PartialFunction]) and map([Function])
    Future {
      1 / 0     // this throws ArithmeticException
    } recover { // recover handles the exception
      case ex: ArithmeticException => 0
    } map {     // finally, map the value 0 to println
      case v: Int => println(v) // 0
    }
    Future {
      1
    } recover { // no exception in this case
      case ex: ArithmeticException => 0
    } map {     // finally, map the value 1 to println
      case v: Int => println(v) // 1
    }

    // 0.3) future.flatMap([T => Future]): Concatenate Future
    //      creates a new future by applying a function to the successful result of this future
    //        it returns the result of the function as the new future
    //      if this future is completed with an exception, then the new future will also contain this exception
    val f1 = Future { 10 } flatMap {
      x => Future { x + 10 }
    }
    println(Await.result(f1, 1 seconds)) // 20
    // a shorthand of the above
    val f2 = for {
      x <- Future { 10 }
      y <- Future { x + 10 }
    } yield y
    println(Await.result(f2, 1 seconds)) // 20
    // when exception happens
    val f3 = for {
      x <- Future { 1 / 0 } recover { case ex: ArithmeticException => 0 }
      y <- Future { x + 10 }
    } yield y
    println(Await.result(f3, 1 seconds)) // 10

    // 1) Await.result([Awaitable], [Duration]): T
    //    Await and return the result of type T of an Awaitable
    //    note: trait Future[+T] extends Awaitable[T]
    val future1: Future[String] = Future { "a future task" }
    Await.result(future1, 1 seconds)

    // 2) future.value: Option[Try[T]]
    //    the value of this Future
    //    2.1) if the future is not completed the returned value will be None
    //    2.2) if the future is completed the value will be:
    //         Some(Success(t))     if it contains a valid result, or
    //         Some(Failure(error)) if it contains an exception
    if (future1.isCompleted) {
      println(future1.value) // Some(Success(a future task))
    }
    // 3) option.get: T
    //    the value of this Option
    //    3.1) it returns the value if it is a Some
    //    3.2) it throws NoSuchElementException if it is a None (i.e. the option is empty)
    //    3.3) def getOrElse[B >: A](default: => B): B
    //         returns the option's value if the option is nonempty, otherwise, return the result of evaluating "default"
    //         ex. option.getOrElse("")
    // 4) try.get: T
    //    the value of this Try
    //    4.1) it returns the value if it is a Success
    //    4.2) it throws the exception if this is a Failure
    //    4.3) def getOrElse[U >: T](default: => U): U
    //         returns the value from this Success or the given default argument if this is a Failure
    //         ex. try.getOrElse("")

    // 5) Future.sequence(Seq[Future]): Future[Seq]
    //    this turns Seq[Future] into Future[Seq]
    //    asynchronously and non-blockingly transforms a Seq[Future[A]] into a Future[Seq[A]]
    //    Useful for reducing many Futures into a single Future
    val seq = Future.sequence(List(taskA(), taskB()))
    //
    // 6) Await.ready([Awaitable], [Duration]):
    //    Await the completed state of an Awaitable
    Await.ready(seq, 3.seconds)
    println(seq)            // Future(Success(List((), ())))

    // 7) val javaFuture: CompletableFuture[String] = scalaFuture.toJava.toCompletableFuture
    val scalaFuture = Future("hello")
    val javaCompletableFuture: CompletableFuture[String] = scalaFuture.toJava.toCompletableFuture
    println(javaCompletableFuture.get) // hello
  }
}
