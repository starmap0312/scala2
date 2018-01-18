// Ref: https://www.beyondthelines.net/computing/scala-future-and-execution-context/
//
// Future vs. ExecutionContext
// 1) a Future is just a placeholder for something that does not exist yet
// 2) we need an ExecutionContext in order to be able to use a Future
//    one can just use the global execution context, there are 3 ways:
//    2.1) Importing the global execution context:
//         import scala.concurrent.ExecutionContext.Implicits.global
//           i.e. implicit lazy val global: ExecutionContext
//    2.2) Using an implicit variable:
//         implicit val executor =  scala.concurrent.ExecutionContext.global
//    2.3) Passing explicitly the execution context:
//         Future { /* do something */ }(executor)
//
// trait ExecutionContext {
//   def execute(runnable: Runnable): Unit
//   ...
// }
// note: executor.execute(this) actually invokes run() which in turns execute the onComplete callback
//
// Future vs. Promise
// 1) a Promise is something that gives a Future
// 2) A Future is a placeholder for a result that can be read when available
//    A Promise is like a "writable-once" container that can be used to complete a Future with the written value
//    ("writable-once" is the reason why DefaultPromise extends AtomicReference)
//
// object Future {
//   def apply[T](body: => T)(implicit executor: ExecutionContext): Future[T] =
//    unit.map(_ => body)
// }
// note: Future.unit == Future.successful(()) == Future[Unit]
//
// def successful[T](result: T): Future[T] = Promise.successful(result).future
//
// object Promise {
//   def successful[T](result: T): Promise[T] = fromTry(Success(result))
//   def fromTry[T](result: Try[T]): Promise[T] = impl.Promise.KeptPromise[T](result) // i.e. an already completed promise
// }
//
// the main pitfalls to avoid:
// 1) avoid using the global ExecutionContext for non-cpu bound tasks (ex. Prefer a dedicated ThreadPool for IO-bound tasks)
// 2) avoid not using "blocking" construct (Always useful, even for "documenting" the code)
// 3) avoid not knowing the parallelism settings
// 4) avoid assuming blocking works with any ExecutionContext

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object FutureTest {
  def taskA(): Future[Unit] = Future {
    println("Starting taskA")
    Thread.sleep(1000) // wait 1secs
    println("Finished taskA")
  }

  def main(args: Array[String]): Unit = {
    // 0) import scala.concurrent.ExecutionContext.Implicits.global
    //    the import is needed, otherwise "Cannot find an implicit ExecutionContext" is thrown when constructing a Future
    //    the reason is because the Future.apply() requires an implicit parameter ExecutionContext:
    //      def apply[T](body: =>T)(implicit executor: ExecutionContext)

    val future: Future[String] = Future { "a future task" }

    // 1) Await.result([Awaitable], [Duration]):
    Await.result(future, 1 seconds)

    // 2) future.value:
    //    returns Option[Try[T]]
    if (future.isCompleted) {
      println(future.value) // Some(Success(a future task))
    }



  }
}
