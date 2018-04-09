import org.apache.commons.daemon.Daemon
import org.apache.commons.daemon.DaemonContext
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

// an Akka actor that actually provides the service
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

// an Application class that constructs ActorSystem, used to create the ServiceActor
class Application() {
  private val applicationName = "my-app"
  private[this] var started: Boolean = false
  implicit val system = ActorSystem(s"$applicationName")
  var service: ActorRef = ???
  import ServiceActor.{Start, Stop}

  def start() {
    println(s"Starting $applicationName Service")
    if (!started) {
      started = true
      service = system.actorOf(ServiceActor.props(), "my-service")
      service ! Start
    }
  }

  def stop() {
    println(s"Stopping $applicationName Service")
    if (started) {
      started = false
      service ! Stop
      system.terminate()
    }
  }
}

// a Daemon class with implemented init(), start(), stop(), destroy() methods implemented
class ApplicationDaemon() extends Daemon {
  val application = new Application

  def init(daemonContext: DaemonContext) {}
  def start() = application.start()
  def stop() = application.stop()
  def destroy() = application.stop()
}


// one can write a init.d script to run the jsvc command as follows:
// jsvc -Xms512m -Xmx1024m \
//      -pidfile /var/run/myapplication.pid \
//      -home /Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk/Contents/Home \
//      -cp /opt/myapplication/myapplication.jar \
//      -user yourserviceuser \
//      -Dconfig.file=/etc/mycompany/myapplication.conf \
//      -outfile /var/log/myapplication/myapplication_out.log \
//      -errfile /var/log/myapplication/myapplication_err.log \
//      -Dlogback.configurationFile=/etc/mycompany/myapplication_logback.xml \
//      com.example.myapplication.server.ApplicationDaemon
// the script takes arguments of start|stop
