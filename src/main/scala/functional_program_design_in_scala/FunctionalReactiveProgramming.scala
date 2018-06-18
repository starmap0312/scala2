package functional_program_design_in_scala

// the Signal API (interface)
// each Signal maintains:
// 1) its current value
// 2) the current expression that defines the signal value
// 3) a set of observers: i.e. the other signals that depend on its value
//    (if the signal changes, all observers need to be re-evaluated)
class Signal[T](expr: => T) {
  import Signal._
  private var myExpr: () => T = _
  private var myValue: T = _
  private var observers: Set[Signal[_]] = Set()

  update(expr)
  protected def update(expr: => T): Unit = { // declare as protected as we do not want client of Signal to call update()
    myExpr = () => expr                      // this makes Signal immutable
    computeValue()
  }
  protected def computeValue(): Unit = {
    val newValue = caller.withValue(this)(myExpr())
    if (myValue != newValue) {
      myValue = newValue
      val obs = observers
      observers = Set()
      obs.foreach(_.computeValue())
    }
  }

  def apply(): T = {
    observers += caller.value
    assert(!caller.value.observers.contains(this), "cyclic signal definition")
    myValue
  }
}
object Signal {
  private val caller = new StackableVariable[Signal[_]](NoSignal) // initially, there is no caller
  // (simple implementation: caller is global state)
  def apply[T](expr: => T) = new Signal(expr)
}

// the Var API (interface)
class Var[T](expr: => T) extends Signal[T](expr) { // it adds one more method: update() to class Signal
  override def update(expr: => T): Unit = super.update(expr) // Var should be able to call Signal's update()
}
object Var {
  def apply[T](expr: => T) = new Var(expr)
}

class StackableVariable[T](init: T) {
  private var values: List[T] = List(init)
  def value: T = values.head
  def withValue[R](newValue: T)(op: => R): R = {
    values = newValue :: values
    try op finally values = values.tail
  }
}

object NoSignal extends Signal[Nothing](???) {
  // sentinel object: a special Signal that does not have value and implementation
  override def computeValue(): Unit = () // override NoSignal computeValue() as it has no expression associated (???: notImplementedError)
}

object FunctionalReactiveProgramming extends App {
  // example1:
  val sig = Var(3)
  println(sig()) // 3
  sig.update(5)  // from now on, sig returns 5 instead of 3
  println(sig()) // 5
  // the above can be abbreviated as the following (because of the update method)
  // like, arr(1) = 5 is equivalent to arr.update(1, 5)
  sig() = 5

  // example2: a FRP method to replace the observer pattern
  class BankAccount { // the Source which uses Signal as its state implementation
    val balance = Var(0)
    def deposit(amount: Int): Unit = {
      if (amount > 0) {
        val value = balance()
        balance() = value + amount // update the Signal
      }
    }
    def withdraw(amount: Int): Unit = {
      if (0 < amount && amount <= balance()) {
        val value = balance()
        balance() = value - amount // update the Signal
      } else throw new Error("insufficient fund")
    }
  }
  def consolidated(accts: List[BankAccount]): Signal[Int] = { // the Target
    Signal(accts.map(_.balance()).sum)
  }
  val account1 = new BankAccount()
  val account2 = new BankAccount()
  val consolidator = consolidated(List(account1, account2))
  println(consolidator()) // 0
  account1.deposit(20)
  println(consolidator()) // 20
  account1.withdraw(10)
  println(consolidator()) // 10

  val xchange = Signal(29.95)
  val inDollar = Signal(consolidator() * xchange())
  println(inDollar()) // 299.5
  account1.withdraw(5)
  println(inDollar()) // 149.75
}
