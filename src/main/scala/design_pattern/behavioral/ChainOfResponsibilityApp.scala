package design_pattern.behavioral

// https://github.com/josephguan/scala-design-patterns/tree/master/behavioral/chain-of-responsibility
// the pattern decouples the sender (client/publisher) of a request from its receiver (handler/strategy/subscriber/observer)
//   the request is processed by a chain until some object handles it
// this pattern is similar to strategy pattern:
//   it differs in that a receiver (handler/strategy/subscriber) is defined as a partial function instead of a function
//   the handlers are chained together by orElse method

// request
sealed abstract class Request
case class FixBugRequest(desc: String) extends Request
case class FeatureRequest(desc: String) extends Request
case class ProductRequest(desc: String) extends Request
case class FakeRequest(desc: String) extends Request
// response
case class Response(req: Request, handled: Boolean)

// handler interface
object RequestHandler {
  // in functional programing, a handler is actually a function
  type Type = PartialFunction[Request, Response]

  // concrete handlers
  val developer: RequestHandler.Type = {
    case req@FixBugRequest(desc) =>
      println(s"I am a developer. I can fix this bug: $desc")
      Response(req, handled = true)
  }

  val architect: RequestHandler.Type = {
    case req@FeatureRequest(desc) =>
      println(s"I am a architect. I can implement this feature: $desc")
      Response(req, handled = true)
  }

  val CTO: RequestHandler.Type = {
    case req@ProductRequest(desc) =>
      println(s"I am a CTO. I can make this product: $desc")
      Response(req, handled = true)
  }

  val noOne: RequestHandler.Type = {
    case req: Request =>
      println("No one is responsible for this request.")
      Response(req, handled = false)
  }
}

// client
//   instead of configuring the chain of handlers by constructor, the chain is defined as a private member
class SoftwareCompany {

  val handlersChain: RequestHandler.Type = { // receiver/observer
    RequestHandler.developer orElse RequestHandler.architect orElse RequestHandler.CTO orElse RequestHandler.noOne
  }

  def send(req: Request): Response = {
    handlersChain(req) // apply the chain of handlers to the request
  }
}

object ChainOfResponsibilityApp extends App {
  val client = new SoftwareCompany()

  // the client sends requests to its handlers chain
  client.send(FixBugRequest("bug"))
  client.send(FeatureRequest("feature"))
  client.send(ProductRequest("product"))
  client.send(FakeRequest("fake"))
}
