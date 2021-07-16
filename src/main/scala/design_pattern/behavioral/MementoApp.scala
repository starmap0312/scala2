package design_pattern.behavioral

import scala.collection.mutable

// https://github.com/josephguan/scala-design-patterns/tree/master/behavioral/memento
// the pattern allows you to externalize an object's internal state so that it can restore its state later
// it does not break the object's encapsulation, i.e. it does not expose the object's implementation details (internal state)

// memento
//  it stores internal state of the originator object
//  it protects against access by objects other than the originator
trait NotebookMemento

// originator
//   it creates a memento containing a snapshot of its current internal state
//   it uses the memento to restore its internal state
class Notebook {

  // concrete memento
  //   it is defined as a private class of originator, so no one other than the originator knows how to access the concrete memento
  private case class NotebookMementoInternal(content: String) extends NotebookMemento

  private var content = new StringBuffer()

  def getMemento: NotebookMemento = { // externalize the originator's state
    NotebookMementoInternal(content.toString)
  }

  def setMemento(memento: NotebookMemento): Unit = memento match { // restore the originator's state
    case NotebookMementoInternal(state) => content = new StringBuffer(state)
    case _ => throw new IllegalArgumentException()
  }

  def write(words: String): Unit = {
    content.append(words)
  }

  def show(): Unit = {
    println(content)
  }
}

// caretaker
//   it is responsible for the memento's safekeeping
//   it never operates on or examines the contents of a memento
object MementoApp extends App {
  val mementoStack = mutable.Stack[NotebookMemento]()
  val notebook = new Notebook

  notebook.write("I have a dream... ") // change notebook state (content)
  mementoStack.push(notebook.getMemento) // use notebook to create a Memento containing its current state (content); push the Memento to a stack
  notebook.show() // I have a dream... // print out the notebook state (content)

  notebook.write("I have a dream that one day ")
  mementoStack.push(notebook.getMemento)
  notebook.show() // I have a dream... I have a dream that one day

  notebook.write("black boys and black girls will be able to join hands ")
  mementoStack.push(notebook.getMemento)
  notebook.show() // I have a dream... I have a dream that one day black boys and black girls will be able to join hands

  notebook.write("with white boys and white girls as sisters and brothers.")
  mementoStack.push(notebook.getMemento)
  notebook.show() // I have a dream... I have a dream that one day black boys and black girls will be able to join hands with white boys and white girls as sisters and brothers.

  notebook.setMemento(mementoStack.pop()) // pop out a Memento from the stack & use it to restore the notebook state (content)
  notebook.show() // I have a dream... I have a dream that one day black boys and black girls will be able to join hands with white boys and white girls as sisters and brothers.

  notebook.setMemento(mementoStack.pop())
  notebook.show() // I have a dream... I have a dream that one day black boys and black girls will be able to join hands

  notebook.setMemento(mementoStack.pop())
  notebook.show() // I have a dream... I have a dream that one day

  notebook.setMemento(mementoStack.pop())
  notebook.show() // I have a dream...
}
