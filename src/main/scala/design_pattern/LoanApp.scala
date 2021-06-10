package design_pattern

import java.io.{BufferedReader, FileReader}

// https://github.com/josephguan/scala-design-patterns/tree/master/behavioral/loan
// the pattern allows to loan a resource to a function
//   the client loans a resource to a client's function which would use it
//   the client can customize any function that it desires
//   the loan function takes care of the destruction of the resource
// the json parser defined in JsonBasics.scala is a good example of loan pattern
//

// loan function
//   it loans a resource to a function and close the resource after the function finished using it
object using {

  // Scala offers a functionality known as Structural Types
  type Resource = { def close(): Unit }

  def apply[R <: Resource, T](resource: => R)(f: R => T): T = {
    val source = Option(resource) // use the resource to do something
    try {
      f(source.get) // apply the passed-in function to the source
    } finally {
      for (s <- source) s.close()
    }
  }
}

class FakeFile {

  def close(): Unit = println("Closing fake file")
  def content: String =  "This is a fake File"
}

// client
object LoanApp extends App {
  using(new FakeFile()) { file =>
    println(file.content) // This is a fake File
  }
  // Closing fake file

  // scala.util.Using
  import scala.util.Using
  Using(new BufferedReader(new FileReader("README.md"))) { reader =>
    println(reader.readLine()) // # scala2
  }
}
