package design_pattern

import scala.collection.mutable

// https://github.com/josephguan/scala-design-patterns/tree/master/behavioral/command
// this pattern is similar to chain of responsibility pattern:
//   it differs in that the handlers (commands) can be added (built) by the client (invoker)


// receiver
case class Television(channel: Int = 0, volume: Int = 0)

// command interface
object Command {
  // in functional programing, a command is actually a function
  type Type = (Television) => Television

  // concrete commands
  def setChannel(channel: Int)(tv: Television): Television = {
    Television(channel, tv.volume)
  }

  def setVolume(volume: Int)(tv: Television): Television = {
    Television(tv.channel, volume)
  }

}

// invoker (client)
//   it knows how to execute a command, and optionally does bookkeeping about the command execution
//   instead of configuring the stack of commands by constructor, the commands is added (built) by the invoke method
class RemoteController(tv: Television) {
  private val historyStack = mutable.ArrayStack[Command.Type]()
  private val redoStack = mutable.ArrayStack[Command.Type]()

  def invoke(cmd: Command.Type): Unit = {
    historyStack.push(cmd) // add a command
  }

  def undo(): Unit = {
    if (historyStack.nonEmpty) {
      val cmd = historyStack.pop() // remove a command
      redoStack.push(cmd)
    }
  }

  def redo(): Unit = {
    if (redoStack.nonEmpty) {
      val cmd = redoStack.pop()
      historyStack.push(cmd)
    }
  }

  def get(): Television = {
    historyStack.foldRight(tv)((cmd, tv) => cmd(tv)) // apply the stack of commands to the tv
  }

}

// client
//   it holds an invoker object and builds the commands for the invoker
//   client => invoker => command => receiver
object CommandApp extends App {
  val remote = new RemoteController(Television())
  remote.invoke(Command.setChannel(1))
  remote.invoke(Command.setChannel(5))
  remote.invoke(Command.setVolume(3))
  remote.invoke(Command.setVolume(20))
  println(remote.get()) // Television(5,20)
  remote.undo()
  remote.undo()
  remote.undo()
  remote.undo()
  remote.undo()
  remote.undo()
  println(remote.get()) // Television(0,0)
  remote.redo()
  remote.redo()
  remote.redo()
  println(remote.get()) // Television(5,3)
}
