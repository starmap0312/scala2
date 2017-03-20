
/**
  * Created by kuanyu on 3/20/17.
  */
object FunctionMethod {
  def main(args: Array[String]): Unit = {
    class MyClass {
      /* constructor */
      var counter = 0
      def expressionInc = { counter += 1 }     // Unit
      def methodInc() = { counter += 1 }       // () => Unit
      val functionInc = { () => counter += 1 } // () => Unit
      // this evaluates the expression immediately and returns the function as a final, named variable
    }
    val obj = new MyClass
    println(obj.counter)  // 0

    // obj.expressionInc is an expression which will be evaluated to Unit when called
    obj.expressionInc // this evaluates the expression
    println(obj.counter)  // 1

    // obj.methodInc is a class method which has no argument and returns Unit
    obj.methodInc()   // this calls the method
    println(obj.counter)  // 2
    // Scala allows the omission of parentheses on calling methods of no argument as long as it has no side effect (unlike println)
    obj.methodInc     // this also calls the method
    println(obj.counter)  // 3
    
    
    // obj.functionInc is () => Unit
    obj.functionInc   // this returns the function as a value (Unit => Unit), not calling the function
    // warning: a pure expression does nothing in statement position
    println(obj.counter)  // 3
    obj.functionInc() // this calls the function object
    println(obj.counter)  // 4
  }
}
