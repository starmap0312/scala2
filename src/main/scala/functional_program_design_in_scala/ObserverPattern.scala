package functional_program_design_in_scala

// observer pattern:
// 1) an imperative approach to handle events
// 2) pros:
//    it decouples views (target/subscribers) from models (source/publishers)
//    it makes different implementations of views easier
// 3) cons:
//    it is imperative: i.e. handle() method is of Unit type and has side-effect of mutating the state
//    it easily leads to bugs, ex. source & target need to be coordinated (it easily leads to callback hell)
//    it makes concurrency difficult, i.e. if multiple models get updated at the same time
//    once models get updated, the views get updated immediately (so they are tightly coupled)
// better design: use of functional programming
// 1) composable event abstraction
// 2) events are first class
//    events are represented as messages
//    handlers of events are also first class (i.e. actors)
//    complex handlers can be composed from primitive ones

trait Source { // the Source generates events (like Publisher)
  private var targets: Set[Target] = Set()

  def subscribe(target: Target): Unit = targets += target
  def unsubscribe(target: Target): Unit = targets -= target
  def publish(): Unit = targets.foreach(_.handle(this))
}

trait Target { // the Target listens to the events (like Subscriber)
  def handle(source: Source): Unit
}

class BankAccount extends Source {
  private var balance = 0 // the state of the Source (model), which may change along the time

  def deposit(amount: Int): Unit =
    if (amount > 0) {
      balance += amount
      publish  // whenever the state of the Source changes, all the Targets gets notified
    }
  def withdraw(amount: Int): Unit =
    if (amount > 0 && amount <= balance) {
      balance -= amount
      publish  // whenever the state of the Source changes, all the Targets gets notified
    } else {
      throw new Error("insufficient balance")
    }
  def currentBalance = balance
}

class Consolidator(sources: List[BankAccount]) extends Target {
  private var total: Int = _ // the state of the Target (view)
  private def compute() = {
    total = sources.map(_.currentBalance).sum
  }
  sources.foreach(_.subscribe(this))
  compute

  def handle(source: Source): Unit = { // a callback function is Unit: it has side effect of mutating the state (total)
    compute // the state of the Target (view, subscriber) depends on the stage of the Source (model, publisher)
  }
  def totalBalance = total
}

object ObserverPattern extends App {
  val account1 = new BankAccount
  val account2 = new BankAccount
  val consolidator = new Consolidator(List(account1, account2))
  println(consolidator.totalBalance) // 0
  account1.deposit(10)
  println(consolidator.totalBalance) // 10
  account2.deposit(20)
  println(consolidator.totalBalance) // 30
  account2.withdraw(5)
  println(consolidator.totalBalance) // 25
}
