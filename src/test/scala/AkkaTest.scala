import scala.concurrent.Future
import akka.actor.{Actor, ActorLogging, Props}
import akka.actor.Status.Failure
import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.event.LoggingReceive
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

case object Start
case object Stop
case object StartWithFuture

trait Race {
  def start: Future[Any]
}

class Marathon extends Race {

  import scala.concurrent.ExecutionContext.Implicits.global

  override def start: Future[Any] = Future {
    println("Marathon is a running long job in future")
    for (i <- 1 to 3) {
      // the Marathon is a Race that is supposed to run longer
      Thread.sleep(1000)
    }
    // we are making it fail so that the Coach can starts it up again
    throw new RuntimeException("Marathon fails")
  }
}

// a Runner is an Actor defined to do specific task when it receives a  message, ex. a Start, Failure or Stop message
object Runner {
  def props(race: Race) = Props(classOf[Runner], race)
}
class Runner(race: Race) extends Actor with ActorLogging {
  // lower-level actor

  override def receive: Receive = LoggingReceive {
    case Start =>
      sender ! "OK"
      log.debug("running...")
      Thread.sleep(100)
      throw new RuntimeException("MarathonRunner is tired") // [ERROR] [akka://race/user/coach/runner] MarathonRunner is tired * 3
                                                            // "java.lang.RuntimeException: MarathonRunner is tired"           * 3
    case Failure(throwable) =>
      log.debug("Runner receives Message Failure(throwable)")
      throw throwable
    case Stop => // never called, as we want the Actor to run forever in this example
      log.debug("stopping runner")
      context.stop(self)
      // context.stop(self): to stop an actor actively
      // whenever an actor is stopped, all of its children actors are recursively stopped
  }
}

case object StartWork
case object RestartRunner

// a Coach is an Actor that look after the Runner
object Coach {
  def props(): Props = Props[Coach]
}
class Coach() extends Actor with ActorLogging {
  // top-level actor

  // each Actor has an implicit val context: ActorContext, which can be used to create non-top-level actors
  // when the Coach Actor is initialized, it executes context.actorOf to create a new Actor Runner and supervise it
  val runner = context.actorOf(Runner.props(new Marathon).withDispatcher("my-pinned-dispatcher"), "runner")
  // the "my-pinned-dispatcher" is defined in application.conf with "type = PinnedDispatcher"
  // Pinned dispatcher:
  //   the dispatcher dedicates a unique thread for each actor using the thread
  //   i.e. each actor has a dedicated thread from its own thread pool
  //   the dispatcher is useful when the actors are doing I/O operations or performing long-running calculations
  //   (the default is Dispatcher, where all actors share threads from the same thread pool)

  // we define supervisorStrategy for its children Actors
  //   whenever an actor fails (throws an Exception) it is temporarily "suspended"
  //   the default supervisor strategy is to "stop and restart the child", i.e. all failures result in a restart by default
  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 2, withinTimeRange = 5 seconds) {
    // it applied OneForOneStrategy which means only take action on the child that failed
    // it tries to restart the Runner Actor 2 times with in 5 seconds and stops the Runner Actor if it still fails

    case _: RuntimeException =>
      // if Runner fails with RuntimeException, it Restart the Actor
      sender ! Start // [INFO] [akka://race/user/coach/runner] Message Start from Actor[akka://race/user/coach] to
                     //        Actor[akka://race/user/coach/runner] was not delivered (after 2 retries)
      Restart
  }

  override def receive = LoggingReceive {
    case StartWork => runner ! Start
    case RestartRunner =>
      log.debug("runner restarted, sending message to Run")
      self ! StartWork
  }
}

object AkkaTest {
  def main(args: Array[String]): Unit = {
    // run the code
    val baseConfig = ConfigFactory.load()
    val system = ActorSystem.create("race", baseConfig)
    val coach = system.actorOf(Coach.props(), "coach") // create a top-level-actor
    coach ! StartWork
  }
}
