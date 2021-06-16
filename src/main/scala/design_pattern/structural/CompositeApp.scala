package design_pattern.structural

// https://github.com/josephguan/scala-design-patterns/tree/master/structural/composite
// the pattern composes objects into tree structures to represent part-whole hierarchies
// it lets clients treat individual objects and compositions of objects uniformly

// component
//   it declares the interface for objects in the composition
//   it declares methods for accessing and managing the components
trait Action {
  def act(): Unit
}

// leaf (concrete components)
//   it defines behavior for primitive objects in the composition
class TurnRight extends Action {
  override def act(): Unit = println("turn right")
}

class TurnLeft extends Action {
  override def act(): Unit = println("turn left")
}

class Forward extends Action {
  override def act(): Unit = println("go forward")
}

// composite
//   it stores child components and may define behavior for components having children
//   it implements the child-related operations
//   Scala allows you to pass variable length arguments to the function, i.e. actions: Seq[Action]
class CompositeAction(actions: Action*) extends Action {
  // note that the composition of these components (actions) is also a component (action)

  override def act(): Unit = {
    actions.foreach(a => a.act())
  }
}

// client
//   it manipulates objects in the composition
//   it treats all the components in the composite structure uniformly
object CompositeApp extends App {
  val action = new CompositeAction(new Forward, new TurnLeft, new TurnRight)
  action.act()
  // go forward
  // turn left
  // turn right
}
