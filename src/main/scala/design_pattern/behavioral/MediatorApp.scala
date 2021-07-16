package design_pattern.behavioral

import scala.collection.mutable.ListBuffer

// https://github.com/josephguan/scala-design-patterns/tree/master/behavioral/mediator
// the pattern promotes `loose coupling` by keeping objects from referring to each other directly
//   i.e. if a set of objects communicate with each other directly, the resulting inter-dependencies are difficult to understand
// otherwise, reusing an object is difficult if it refers to many other objects
// also, you cannot vary the interaction of objects independently

// mediator
//   it defines an interface for communicating with Colleague objects
trait Organization {

  def countryDeclare(country: Country, msg: String): Unit
}

// concrete mediator
//   it implements cooperative behavior by coordinating Colleague objects
//   it knows and maintains its colleagues

class UnitedNations extends Organization {

  val countries = new ListBuffer[Country]()

  def addMember(country: Country): Unit = {
    country.join(this)
    countries.append(country)
  }

  override def countryDeclare(country: Country, msg: String): Unit = {
    println(s"${country.name} declared: '$msg'")
    for (c <- countries if c != country) {
      c.receive(msg)
    }
  }
}

// colleague
//   it defines an interface for services provided by colleagues objects
abstract class Country(val name: String) {

  protected var organization: Organization = _

  def join(org: Organization): Unit = {
    organization = org
  }

  def declare(msg: String): Unit = {
    organization.countryDeclare(this, msg)
  }

  def receive(msg: String): Unit = {
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
