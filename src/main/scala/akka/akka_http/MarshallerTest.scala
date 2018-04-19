package akka.akka_http

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.Done
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.server.Directives._
//import akka.http.scaladsl.server.Directives.{get, pathPrefix, onSuccess, complete, LongNumber}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
//import akka.http.scaladsl.model.Uri.Path.{/}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import spray.json.DefaultJsonProtocol._
//import spray.json.DefaultJsonProtocol.{jsonFormat1, jsonFormat2}

import scala.concurrent.Future
import scala.io.StdIn

// Set up a web-server that communicates with a mock database
// it marshals Item returned from database to Json String, and vice versa
object MarshallerTest {

  implicit val system = ActorSystem()             // needed to run the route
  implicit val materializer = ActorMaterializer() // needed for the future map/flatmap in the end and future in fetchItem and saveOrder
  implicit val executionContext = system.dispatcher

  var orders: List[Item] = Nil

  // domain model
  final case class Order(items: List[Item])
  final case class Item(name: String, id: Long)

  // formats for unmarshalling and marshalling (the following two implicits are temporal coupled)
  implicit val itemFormat = jsonFormat2(Item)
  implicit val orderFormat = jsonFormat1(Order)

  // (fake) async database query api
  def fetchItem(itemId: Long): Future[Option[Item]] = Future {
    orders.find(o => o.id == itemId)
  }

  def saveOrder(order: Order): Future[Done] = {
    orders = order match {
      case Order(items) => items ::: orders
      case _            => orders
    }
    Future { Done }
  }

  def main(args: Array[String]): Unit = {

    val route: Route = {
      get {
        pathPrefix("fetch" / LongNumber) { id =>
          // there might be no item for a given id
          val maybeItem: Future[Option[Item]] = fetchItem(id)

          onSuccess(maybeItem) {
            case Some(item) => complete(item)
            case None => complete(StatusCodes.NotFound)
          }
        }
      } ~ post {
        path("save") {
          entity(as[Order]) { order =>
            val saved: Future[Done] = saveOrder(order)
            onComplete(saved) { done =>
              complete("order created")
            }
          }
        }
      }
    }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server running at http://localhost:8080/\n" +
      s"Press RETURN to stop...")
    StdIn.readLine() // let it run until user presses return

    bindingFuture
      .flatMap(_.unbind())                // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done

  }
}
