object ScalaThread extends App {

  // 1) create a Thread
  val thread = new Thread {
    override def run {
      println("hello scala thread");
    }
  }
  thread.start // start the thread & execute its Runnable#run()

  // 2) synchronization
  // bad practice: 2 treads accessing the same object at the same time
  class Shared {
    private var i = 0

    def getAndIncr: Int = {
      i += 1
      i - 1
    }
  }
  val sharedObj = new Shared()

  for (i <- 0 to 1) {
    val thread = new Thread {
      override def run {
        for (x <- 0 to 9) {
          println(s"Thread $i: ${sharedObj.getAndIncr}")
          Thread.sleep(1000)
        }
      }
    }
    thread.start
  }
  // Thread 0: 0
  // Thread 1: 0
  // Thread 1: 1
  // Thread 0: 1
  // Thread 1: 2
  // ...
  // Thread 0: 14
  // Thread 1: 14

  Thread.sleep(11000)
  println

  // good practice: use synchronized for the shard objects between threads
  class SharedSync {
    private var i = 0 // alternatively, instead of using synchronized, we can define an AtomicInteger

    def getAndIncr: Int = this.synchronized { // you need to synchronize on some resource, ex. this (current object)
      i += 1
      i - 1
    }
  }
  val sharedSyncObj = new SharedSync()
  for (i <- 0 to 1) {
    val thread = new Thread {
      override def run {
        for (x <- 0 to 9) {
          println(s"Thread $i: ${sharedSyncObj.getAndIncr}")
          Thread.sleep(1000)
        }
      }
    }
    thread.start
  }
  // Thread 0: 0
  // Thread 1: 1
  // Thread 0: 2
  // Thread 1: 3
  // ...
  // Thread 1: 21
  // Thread 0: 20

}
