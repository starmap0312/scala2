package design_pattern

// https://github.com/josephguan/scala-design-patterns/tree/master/creational/singleton
// the pattern ensures a class only has one instance and it provides a global point of access to it
//   there is exactly one instance of a class and it is accessible by all clients
//   the sole instance can extend a class by subclassing and the clients do not need to modify the code of base class

// singleton
//   Scala provides concise direct realization of the singleton pattern
object Singleton {}

class SingletonApp extends App {
  val s1 = Singleton
  val s2 = Singleton
  println(s1 == s2)
}
