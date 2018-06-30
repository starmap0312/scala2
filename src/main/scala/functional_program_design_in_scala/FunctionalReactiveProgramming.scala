package functional_program_design_in_scala

class StackableVariable[T](init: T) {
  private var values: List[T] = List(init)
  def value: T = values.head
  def withValue[R](newValue: T)(op: => R): R = {
    values = newValue :: values
    try op finally values = values.tail
  }
}
object Signal {
  // a simple implementation: i.e. use global state for caller
  private val caller = new StackableVariable[Signal[_]](NoSignal) // initially, only the sentinel object, i.e. there is no caller
  def apply[T](expr: => T) = new Signal(expr)
}
// the Signal API (interface), where each Signal maintains:
// 1) its current value, i.e. var myValue: T
// 2) the current expression that defines the signal value, i.e. var myExpr: () => T
// 3) a set of observers: i.e. var observers: Set[Signal[_]], the other signals that depend on its value
//    (if the signal gets updated, all observers need to be re-evaluated as well)
class Signal[T](expr: => T) {
  import Signal._ // import the global caller object
  private var myValue: T = _
  private var myExpr: () => T = _
  private var observers: Set[Signal[_]] = Set()

  update(expr)
  protected def update(expr: => T): Unit = { // declare as protected as we do not want client of Signal to call update()
    myExpr = () => expr                      // this makes Signal immutable
    evaluate()
  }
  // ex. sig2() = Signal(sig1() + 5)
  //   we have myExpr = () => (sig1() + 5) and then sig2's value gets evaluated()

  protected def evaluate(): Unit = {
    val newValue = caller.withValue(this)(myExpr()) // sig2 is first added to the global caller stack, then sig1() gets applied
    if (myValue != newValue) {  // whenever the signal's value gets updated, the signal's observers' value get updated as well
      myValue = newValue
      val obs = observers
      observers = Set()
      obs.foreach(_.evaluate()) // when sig1's value gets updated, all its observers get updated as well
    }
  }

  def apply(): T = {
    observers += caller.value   // when sig1 gets applied, sig1 adds the caller stack head, i.e. sig2 to sig1's observers
    assert(!caller.value.observers.contains(this), "cyclic signal definition")
    myValue
  }
}

// 1) the sentinel object: a special Signal that does not have value and implementation
object NoSignal extends Signal[Nothing](???) {
  override def evaluate(): Unit = () // override NoSignal evaluate() as it has no expression associated (???: notImplementedError)
}
// 2) the variable Signal, i.e. a Var API (interface)
object Var {
  def apply[T](expr: => T) = new Var(expr)
}
class Var[T](expr: => T) extends Signal[T](expr) {           // it adds one more method: update() to class Signal
  override def update(expr: => T): Unit = super.update(expr) // Var should be able to call Signal's update()
}

object FunctionalReactiveProgramming extends App {
  // example1:
  val sig1 = Var(3)
  val sig2 = Signal(sig1() + 5)
  println(sig1()) // 3
  println(sig2()) // 3 + 5 = 8
  sig1() = 5      // i.e. sig.update(5)
  println(sig1()) // 5
  println(sig2()) // 5 + 5 = 10
  sig1() = 10     // i.e. sig.update(5)
  println(sig1()) // 10
  println(sig2()) // 10 + 5 = 10

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
