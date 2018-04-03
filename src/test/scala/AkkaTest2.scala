import scala.concurrent.Future
import akka.actor.{Actor, ActorLogging, Props}
import akka.actor.Status.Failure
import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.event.LoggingReceive
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
/*
trait Race {
  def start: Future[Any]
}

class Marathon extends Race {

  import scala.concurrent.ExecutionContext.Implicits.global

  override def start: Future[Any] = Future {
    println("Marathon is a running long job in a Future")
      // the Marathon is a Race that is supposed to run longer
    Thread.sleep(300)
    // we are making it fail so that the Coach can starts it up again
    throw new RuntimeException("Making Marathon fails")
  }
}

// a Runner is an Actor defined to do specific task when it receives a  message, ex. a Start, Failure or Stop message
object Runner {
  // the props() method indicates that the actor requires one field when constructed, i.e. an Race object
  //def props(race: Race) = Props(classOf[Runner], race)
  //  def apply(clazz: Class[_], args: Any*):
  //    create a Props given its Actor class and its constructor arguments
  //  but this is not recommended, as it may throw IllegalArgumentException if no matching constructor found
  def props(race: Race) = Props(new Runner(race))

  // Runner Actor's messages (it is a good practice to define an Actor's messages in its companion object and use import later)
  case object Start
  case object Stop
  case object StartWithFuture
}
class Runner(race: Race) extends Actor with ActorLogging { // lower-level actor
  // actor Runner extends akka.actor.ActorLogging to automatically get a reference to a logger
  // i.e. val log = akka.event.Logging(context.system, this)
  //      so that you can write: log.debug("message") in the Actor class

  import Runner.{Start, Stop}

  override def receive: Receive = LoggingReceive {
    case Start =>
      //sender ! "OK" // send an ack message to the sender
      log.debug("Runner receives Message Start")
      race.start.wait()
      //throw new RuntimeException("Never gets executed.") // [ERROR] [akka://race/user/coach/runner] MarathonRunner is tired * 3
                                                            // "java.lang.RuntimeException: MarathonRunner is tired"           * 3
    case Failure(throwable) =>
      log.debug("Runner receives Message Failure(throwable)")
      throw throwable
    case Stop => // never called, as we want the Actor to run forever in this example
      log.debug("Runner receives Message Stop")
      context.stop(self)
      // context.stop(self): to stop an actor actively
      // whenever an actor is stopped, all of its children actors are recursively stopped
  }
}


// a Coach is an Actor that look after the Runner
object Coach {
  // the props() method indicates that the actor does not require any field when constructed
  //def props(): Props = Props[Coach]
  // def apply[T <: Actor: ClassTag](): Props
  //   Returns a Props that has default values except for "creator" which will be a function that creates an instance
  //     of the supplied type using the default constructor
  //  but this is not recommended, as it may throw IllegalArgumentException if no matching constructor found
  def props(): Props = Props(new Coach)

  // Coach Actor's messages
  case object StartWork
  case object RestartRunner
}
class Coach() extends Actor with ActorLogging { // top-level actor

  import Runner.{Start}
  import Coach.{StartWork, RestartRunner}

  // each Actor has an implicit val context: ActorContext, which can be used to create non-top-level actors
  // when the Coach Actor is initialized, it executes context.actorOf to create a new Actor Runner and supervise it
  val runner = context.actorOf(
    Runner.props(new Marathon).withDispatcher("my-pinned-dispatcher"),
    "runner"
  )
  // the "my-pinned-dispatcher" is defined in application.conf with "type = PinnedDispatcher"
  // Pinned dispatcher:
  //   the dispatcher dedicates a unique thread for each actor using the thread
  //   i.e. each actor has a dedicated thread from its own thread pool
  //   the dispatcher is useful when the actors are doing I/O operations or performing long-running calculations
  //   (the default is Dispatcher, where all actors share threads from the same thread pool)

  // overridable strategy used to supervise its child actors
  //   we can define supervisorStrategy for its children Actors
  //   whenever an actor fails (throws an Exception) it is temporarily "suspended"
  //     i.e. it does not process messages and does not consume any resources apart from memory
  //   the default supervisor strategy is to "stop and restart the child", i.e. all failures result in a restart by default
  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 2, withinTimeRange = 5 seconds) {
    // it applied OneForOneStrategy which means only take action on the child that failed
    // it tries to restart the Runner Actor 2 times with in 5 seconds and stops the Runner Actor if it still fails

    case _: RuntimeException =>
      // if Runner fails with RuntimeException, it Restart the Actor
      log.debug("Recevied RuntimeException in supervisorStrategy()")
      sender ! Start // [INFO] [akka://race/user/coach/runner] Message Start from Actor[akka://race/user/coach] to
                     //        Actor[akka://race/user/coach/runner] was not delivered (after 2 retries)
      Restart        // this will suspend itself, terminate its child Runner actor, and then create and restart a new child Runner actor
  }

  override def receive = LoggingReceive {
    case StartWork =>
      log.debug("Coach received message StartWork")
      runner ! Start
    case RestartRunner =>
      log.debug("Coach received message RestartRunner")
      self ! StartWork
  }
}

object AkkaTest {
  def main(args: Array[String]): Unit = {
    // run the code
    val baseConfig = ConfigFactory.load() // load akka configuration defined in application.conf
    val system = ActorSystem.create("race", baseConfig)
    val coach = system.actorOf( // create a top-level-actor
      Coach.props(),
      "coach"
    )
    coach ! Coach.StartWork     // send an message to the top-level actor
  }
}
*/