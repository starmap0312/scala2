package design_pattern.structural
// https://github.com/josephguan/scala-design-patterns/tree/master/structural/bridge
// the pattern defines a a class hierarchy (abstraction -> implementation):
//   it decouples an abstraction from its implementation so that the two can vary independently
//   it avoid a permanent binding between an abstraction (high-level operations) and its implementation (primitive operations)
// it lets you combine the different abstractions and implementations and extend them independently

// implementor interface
//   it provides only primitive operations, ex. getColor
//   the implementation interface doesn't have to correspond exactly to abstraction's interface
//   i.e. the two interfaces can be quite different
trait Theme {
  def getColor: String
}

// concrete implementors
class DarkTheme extends Theme {
  override def getColor: String = "Dark Black"
}

class LightTheme extends Theme {
  override def getColor: String = "Off White"
}

// abstraction
//   it provides higher-level operations based on the implementor primitives, ex. getContent uses theme.getColor
//   it maintains a reference to an object of type implementor
abstract class WebPage(protected var theme: Theme) {

  def getContent: String

  def changeTheme(newTheme: Theme): Unit = {
    theme = newTheme
  }
}

// refined abstraction (concrete classes)
//   it extends the abstraction interface
class AboutPage(aboutTheme: Theme) extends WebPage(aboutTheme) {

  override def getContent: String = "About page in " + theme.getColor
}

class HelpPage(helpTheme: Theme) extends WebPage(helpTheme) {

  override def getContent: String = "Help page in " + theme.getColor
}

// client
//   changes in the implementation of an abstraction should have no impact on clients
object BridgeApp extends App {
  val about = new AboutPage(new DarkTheme)
  println(about.getContent) // About page in Dark Black

  val help = new HelpPage(new LightTheme)
  println(help.getContent) // Help page in Off White

  help.changeTheme(new DarkTheme)
  println(help.getContent) // Help page in Dark Black
}
