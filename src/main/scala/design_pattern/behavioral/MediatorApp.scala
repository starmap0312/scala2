package design_pattern.behavioral

import scala.collection.mutable.ListBuffer

// https://github.com/josephguan/scala-design-patterns/tree/master/behavioral/mediator
// the pattern promotes `loose coupling` by keeping objects from referring to each other directly
//   i.e. if a set of objects communicate with each other directly, the resulting inter-dependencies are difficult to understand
// otherwise, reusing an object is difficult if it refers to many other objects
// also, you cannot vary the interaction of objects independently
// the pattern is similar to the observer pattern in that:
//   it decouples the inter-dependencies between objects (colleagues/observers) that require communication
//   it introduces a intermediate mediator (subject) for the communication
//   it differs in that each colleague (observer) actively initiate the communication, instead of got notified passively in an event of subject state change

// mediator
//   it defines an interface for communicating with colleague objects
trait Organization {

  def countryDeclare(country: Country, msg: String): Unit
}

// concrete mediator (subject)
//   it implements cooperative behavior by coordinating colleague objects
//   it maintains a list of colleagues and is responsible for the communication of these colleagues
class UnitedNations extends Organization {

  val countries = new ListBuffer[Country]() // maintains a list of colleagues (dependents)

  def addMember(country: Country): Unit = {
    country.join(this)
    countries.append(country)
  }

  override def countryDeclare(country: Country, msg: String): Unit = { // notify the list of colleagues (dependents)
    println(s"${country.name} declared: '$msg'")
    for (c <- countries if c != country) {
      c.receive(msg)
    }
  }
}

// colleague (dependent/observer)
//   it defines an interface for services provided by colleagues objects
abstract class Country(val name: String) {

  protected var organization: Organization = _ // maintains a reference to the mediator (subject)

  def join(org: Organization): Unit = {
    organization = org
  }

  def declare(msg: String): Unit = { // the colleague can actively initiate the communication via the reference to the mediator
    organization.countryDeclare(this, msg)
  }

  def receive(msg: String): Unit = { // the colleague implements what to do when receiving the notification from the mediator (subject)
    println(s"$name received: '$msg'")
  }
}

// concrete colleagues
//   each Colleague class only knows its mediator object
//   each colleague communicates with its mediator instead of communicating with another colleague directly
class USA extends Country("USA")

class China extends Country("China")

class Canada extends Country("Canada")

object MediatorApp extends App {
  val china = new China
  val usa = new USA
  val canada = new Canada

  val united = new UnitedNations
  united.addMember(china)
  united.addMember(usa)
  united.addMember(canada)

  usa.declare("Hello World") // USA declared: 'Hello World'
  // China received: 'Hello World'
  // Canada received: 'Hello World'
}
