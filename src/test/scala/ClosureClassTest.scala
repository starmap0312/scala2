
object ClosureClassTest {
  def main(args: Array[String]): Unit = {
    val cc = new ClosureClass
    cc.printResult { "hello world" }               // pass in an expression
    cc.printResult { x: String => {x + " world"} } // pass in a Function1
  }
}
