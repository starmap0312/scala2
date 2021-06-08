package design_pattern

// https://github.com/josephguan/scala-design-patterns/tree/master/behavioral/chain-of-responsibility
// the pattern decouples the sender of a request (client) from its receiver (handler/strategy)
//   the request is processed by a chain until some object handles it
// this pattern is similar to strategy pattern:
//   it differs in that a handler (strategy) is defined as a partial function instead of a function
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

  val chain: RequestHandler.Type = {
    RequestHandler.developer orElse RequestHandler.architect orElse RequestHandler.CTO orElse RequestHandler.noOne
  }

  def handleRequest(req: Request): Response = {
    chain(req) // apply the chain of handlers to the request
  }
}

object ChainOfResponsibilityApp extends App {
  val company = new SoftwareCompany()

  company.handleRequest(FixBugRequest("bug"))
  company.handleRequest(FeatureRequest("feature"))
  company.handleRequest(ProductRequest("product"))
  company.handleRequest(FakeRequest("fake"))
}
