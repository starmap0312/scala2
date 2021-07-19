package design_pattern.behavioral

// https://github.com/josephguan/scala-design-patterns/tree/master/behavioral/state
// the pattern allows an object to alter its behavior when its internal state changes
//   the pattern puts each branch of the conditional in a separate class
//   this lets you treat the object's state as an object in its own right that can vary independently from other objects
// the pattern is similar to the observer pattern in that:
//   the context (subject) maintains a state and allows the clients to change its state
//   it differs in that there is no list of observers that observe its state

// state interface
//   it defines an interface for encapsulating the behavior associated with a particular state
trait KeyboardState {
  def write(word: String): Unit
}

// concrete states
//   it implements the behavior associated with the state
class UpperCaseState extends KeyboardState {
  override def write(word: String): Unit = {
    println(word.toUpperCase)
  }
}

class LowerCaseState extends KeyboardState {
  override def write(word: String): Unit = {
    println(word.toLowerCase)
  }
}

// context (subject)
//   it maintains a current state (an instance of the concrete state)
//   it allows the clients to change its state, and its behavior will change whenever its state changes
class Keyboard {
  var state: KeyboardState = new LowerCaseState()

  def write(word: String): Unit = {
    state.write(word)
  }

  def pressCapsLock(): Unit = state match {
    case _: LowerCaseState => state = new UpperCaseState
    case _: UpperCaseState => state = new LowerCaseState
  }
}

object StateApp extends App {
  val keyboard = new Keyboard()
  keyboard.write("hello world") // hello world
  keyboard.pressCapsLock()
  keyboard.write("hello world") // HELLO WORLD
  keyboard.pressCapsLock()
  keyboard.write("hello world") // hello world
}
