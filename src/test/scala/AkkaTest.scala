import scala.concurrent.Future
import akka.actor.{Actor, ActorLogging, Props}
import akka.actor.Status.Failure
import akka.event.LoggingReceive
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
  override def receive: Receive = LoggingReceive {
    case Start =>
      sender ! "OK"
      log.debug("running...")
      Thread.sleep(10)
      throw new RuntimeException("MarathonRunner is tired")
    case Failure(throwable) => throw throwable
    case Stop =>
      log.debug("stopping runner")
      context.stop(self)
  }
}

case object StartWork
case object RestartRunner

// a Coach is an Actor that look after the Runner
object Coach {
  def props(): Props = Props[Coach]
}
class Coach() extends Actor with ActorLogging {

  // when the Coach Actor is initialized, it executes context.actorOf which creates a new Runner and supervises it
  val runner = context.actorOf(Runner.props(new Marathon).withDispatcher("my-pinned-dispatcher"), "runner")
  // started Actor on PinnedDispatcher: the reason is that we want the legacy process to be available

  // it defines supervisorStrategy for its children(Actors)
  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 2, withinTimeRange = 5 seconds) {
    // it applied OneForOneStrategy which means only take action on the child that failed
    // it tries to restart Actor 2 times with in 5 seconds and stops if Actor still fails

    case _: RuntimeException =>
      // if Runner fails with RuntimeExecption, it Restart the Actor
      sender ! Start
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
    val coach = system.actorOf(Coach.props(), "coach")
    coach ! StartWork
  }
}
