package concurrent

import java.util.concurrent.Executors

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

object Throttling {
  def main(args: Array[String]): Unit = {
    // 1) default ExecutionContext (global):
    //    it is backed by a work-stealing thread pool
    //    it uses a target number of worker threads equal to the number of available processors (parallelism-level)

    import scala.concurrent.ExecutionContext.Implicits.global
    println("Number of CPUs = ${sys.runtime.availableProcessors}") // 8
    val futures = for (i <- 1 to 24) yield {
      Future {
        println(s"instantiate future${i}")   // each time 8 futures are instantiated
        // a computationally expensive method, ex. open a socket and send some data
        Thread.sleep(1000)
        s"computed value ${i}"
      }
    }
    val resultSeq = Await.result(Future.sequence(futures), 3.5 seconds)
    resultSeq foreach (println _)// value 1, ..., value 24

    // 2) use customized ExecutionContext
    val numWorkers = 30
    println(s"Number of threads = ${numWorkers}") // 30
    implicit val ec = ExecutionContext.fromExecutorService(Executors.newWorkStealingPool(numWorkers))
    val futures2 = for (i <- 1 to 24) yield {
      Future {
        println(s"instantiate future${i}")   // each time 8 futures are instantiated
        // a computationally expensive method, ex. open a socket and send some data
        Thread.sleep(1000)
        s"computed value ${i}"
      } (ec)
    }
    val resultSeq2 = Await.result(Future.sequence(futures2), 3.5 seconds)
    resultSeq2 foreach println // value 1, ..., value 24
  }
}
