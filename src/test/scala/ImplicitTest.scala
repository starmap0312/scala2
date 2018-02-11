object helpers {
  // implicit classes must be defined inside of another trait/class/object
  //   this limits their visibility to a certain namespace
  // they only takes one non-implicit argument in their constructor
  //   they converts one type to the implicit class
  implicit class ImplicitSubstring(x: String) { // only
  def heading(n: Int) = x.substring(0, n)
    def ending(n: Int) = x.substring(n, x.length)
  }
  // the above implicit class is a shorthand for the following
  class Substring(x: String) {
    def head(n: Int) = x.substring(0, n)
    def end(n: Int) = x.substring(n, x.length)
  }
  implicit def Substring(x: String) = new Substring(x) // this implicit method converts String to the class instance
}

object ImplicitTest {
  def main(args: Array[String]): Unit = {
    // 6) implicit class
    import helpers._
    // an implicit conversion from String to ImplicitSubstring
    println("12345".heading(3)) // 123
    println("12345".ending(3))  // 45
    // an implicit conversion from String to Substring
    println("12345".head(3))    // 123
    println("12345".end(3))     // 45
  }
}
