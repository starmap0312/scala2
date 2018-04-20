package concurrent

// Scala ExecutionContext vs. Java Executor
// https://blog.knoldus.com/2016/07/25/java-executor-vs-scala-executioncontext/
// 1) Java Executor:
//    an interface for encapsulate the decision of how to run concurrently executable work tasks, with an abstraction
//    it decouples task submission from the mechanics of how each task will be run (ex. thread use details, scheduling, etc)
//  interface Executor{
//    public void execute(Runnable command)
//  }
//  1.1) Executor decides on which thread and when to call run method of Runnable object
//  1.2) Executor object can start a new thread specifically for this invocation of execute or
//       even the execute the Runnable object directly on the caller thread
//  1.3) Tasks scheduling is depends on implementation of Executor
//  1.4) ExecutorService is a sub interface of Executor for manage termination and
//       methods that can produce a future for tracking progress of one or more asynchronous tasks
//  1.5) Java Executor implementations include ThreadPoolExecutor and ForkJoinPoll
//  2) Scala ExecutionContext trait:
//     it offers a similar functionality of Java Executor but it is more specific to Scala
//  trait ExecutionContext {
//    def execute(runnable: Runnable): Unit     // same as Java Executor
//    def reportFailure(cause: Throwable): Unit // called whenever some tasks throw an exception
//  }
//  2.1) ExecutionContext has an companion object which have methods for creating ExecutionContext object from Java Executor or ExecutorService
//       (i.e. methods that act as a bridge between Java and Scala)
//  2.2) ExecutionContext companion object contains the default execution context (i.e. global) which internally uses a ForkJoinPool instance

// The type of thread pools you spawn vastly depend on the actions they are mean to perform
// 1) CachedThreadPool: for short running/cheap tasks
//    for short running tasks, ex. database queries, you are better off with a CachedThreadPool
//    each individual task is relatively cheap but creating a new thread is expensive
//    Note: Thread creation is much more expensive then allocating a single object
//          ex. acquiring a monitor lock or updating an entry in a collection
// 2) FixedThreadPool: for long running/expensive tasks
//    for expensive operations, you want to limit the amount of threads running at the same time
//    because for memory, performance etc., are already exhausted
// 3) ForkJoinPool: Divide et impera
//    for expensive computation, you can divide it into smaller bits individual workers can compute

import java.util.concurrent.Executors

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

object ScalaExecutionContext {
  def main(args: Array[String]): Unit = {
    // 1) default ExecutionContext (global):
    //    it is backed by a work-stealing thread pool
    //    it uses a target number of worker threads equal to the number of available processors (parallelism-level)

    val default = scala.concurrent.ExecutionContext.Implicits.global
    println("Number of CPUs = ${sys.runtime.availableProcessors}") // 8
    val futures = for (i <- 1 to 24) yield {
      Future {
        println(s"instantiate future${i}")   // each time 8 futures are instantiated
        // a computationally expensive method, ex. open a socket and send some data
        Thread.sleep(1000)
        s"computed value ${i}"
      } (default)
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
