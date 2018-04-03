import org.apache.commons.daemon.Daemon
import org.apache.commons.daemon.DaemonContext
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

class ApplicationDaemon() extends Daemon {
  def application = new Application

  def init(daemonContext: DaemonContext) {}
  def start() = application.start()
  def stop() = application.stop()
  def destroy() = application.stop()
}

object ServiceActor {
  def props() = Props(new ServiceActor)
  case object Start
  case object Stop
}
class ServiceActor() extends Actor with ActorLogging {
  import ServiceActor.{Start, Stop}
  override def receive: Receive = {
    case Start =>
      log.debug("ServiceActor receives Message Start")
    case Stop => // never called, as we want the Actor to run forever in this example
      log.debug("ServiceActor receives Message Stop")
      context.stop(self)
  }
}

class Application() {
  private[this] var started: Boolean = false
  private val applicationName = "my-app"
  implicit val system = ActorSystem(s"$applicationName")

  def start() {
    println(s"Starting $applicationName Service")
    if (!started) {
      started = true
      val service = system.actorOf(ServiceActor.props(), "my-service")
    }
  }

  def stop() {
    println(s"Stopping $applicationName Service")
    if (started) {
      started = false
      system.terminate()
    }
  }
}

object AkkaDaemonTest {

}
