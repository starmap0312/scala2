// in Scala, we can use the ClosureClass as follows:
//   val cc = new ClosureClass
//   cc.printResult { "hello world" }     // pass in a Function0 (an expression)
//   cc.printResult { x => x + " world" } // pass in a Function1
// in Java, we can use Scala's provided AbstractFunction0 and an AbstractFunction1 interfaces
//
// the class is compiled to Java class as follows:
//   public class ClosureClass extends java.lang.Object implements scala.ScalaObject {
//     public ClosureClass();                    // constructor
//     public void printResult(scala.Function0);
//     public void printResult(scala.Function1);
//   }
class ClosureClass {
  def printResult[T](f: => T) = {
    println(f)
  }

  def printResult[T](f: String => T) = {
    println(f("hello"))
  }
}