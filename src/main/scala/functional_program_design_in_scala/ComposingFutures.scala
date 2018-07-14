package functional_program_design_in_scala2

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object ComposingFutures extends App {

  // example: implement retry future block n times
  // 1) use recursion & call-by-name:
  def retry1[T](times: Int)(block: => Future[T]): Future[T] = {
    if (times == 0) {
      Future.failed(new Exception("Failed too many times"))
    } else {
      // def fallbackTo[U >: T](that: Future[U]): Future[U]
      block fallbackTo {
        retry1(times - 1)(block)
      }
    }
  }

  // 2) use foldLeft():
  def retry2[T](times: Int)(block: => Future[T]): Future[T] = {
    val attempts: List[() => Future[T]] = (1 to times).toList.map(_ => (() => block))
    val failed: Future[T] = Future.failed(new Exception("failure"))
    val result: Future[T] = attempts.foldLeft(failed)(
      (attempt, blockFunc) => attempt recoverWith {
        case _ => blockFunc()
      }
    )
    result
  }

  val task1 = retry1(3)(
    Future {
      println(s"future1")
      1
    }
  )
  println(Await.result(task1, 1.second)) // 1
  val task2 = retry2(3)(
    Future {
      println(s"future2")
      2
    }
  )
  println(Await.result(task2, 1.second)) // 2
}
