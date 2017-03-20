
/**
  * Created by kuanyu on 3/20/17.
  */
object FunctionMethod {
  def main(args: Array[String]): Unit = {
    // example1: methods vs. functions
    def method(x: Int): Int = 2 * x                // (x: Int)Int: expression with Int parameter
    // the above is not evaluated yet, i.e. no instance or object created
    val function: (Int) => Int = (x: Int) => 2 * x // (Int) => Int: Function1
    // the above instantiated a Function1 object
    class Func1 extends Function1[Int,Int] {       // a Function1 class defined
      def apply(x: Int): Int = x + 1
    }
    val func1 = new Func1
    // the above instantiated a Function1 object
    class Func2 extends ((Int) => Int) {           // (Int) => Int is syntactic sugar for Function1[Int, Int]
      def apply(x: Int): Int = x + 1
    }
    val func2 = new Func2
    println(method(1))         // 2
    println(function(1))       // 2
    println(func1(1))          // 2
    println(func2(1))          // 2

    // 1.1) instances already created?
    // a method can't be the final value
    //method                   // compile error: missing arguments for method
    // a function can be the final value
    function                   // (Int) => Int (Function1 object)
    // 1.2) Parameter list is optional for methods but mandatory for functions
    // a method can have no parameter list
    def method2 = 100          // Int: expression with no parameter
    // a function must have a parameter list
    val function2 = () => 100  // () => Int: Function1
    // method name means invocation: automatically converted into a Function1 object and invoked
    println(method2)           // 100
    // function name means the Function1 object itself: need () to invoke the function object
    println(function2())       // 100

    // 1.3) ETA expansion: what compiler does behind the scene
    //      in Scala, we can provide a method when a function is expected (not recommended)
    //        ex. filter() and map() expect a Function1 object
    //      in this case, a method are automatically converted into a Function1 object
    println(List(1, 2, 3).map(function)) // List(2, 4, 6)
    println(List(1, 2, 3).map(method))   // List(2, 4, 6), method is automatically converted into a Function1 object
    println(List(1, 2, 3).map(method _)) // List(2, 4, 6), method is explicitly converted into a Function1 object


    // example2
    class MyClass {
      /* constructor */
      var counter = 0
      def expressionInc = { counter += 1 }     // Unit: expression with no parameter and evaluated to Unit
      def methodInc() = { counter += 1 }       // Unit: expression with no parameter and evaluated to Unit
      val functionInc = { () => counter += 1 } // () => Unit: Function1
      // this evaluates the expression immediately and returns the function as a final, named variable
    }
    val obj = new MyClass
    println(obj.counter)  // 0

    // obj.expressionInc is an expression which will be evaluated to Unit when called
    obj.expressionInc     // this evaluates the expression
    println(obj.counter)  // 1

    // obj.methodInc is a class method which has no argument and returns Unit
    obj.methodInc()       // this evaluates the expression
    println(obj.counter)  // 2
    // Scala allows the omission of parentheses on calling methods of no argument as long as it has no side effect (unlike println)
    obj.methodInc         // this also evaluates the expression
    println(obj.counter)  // 3

    // obj.functionInc is () => Unit: Function1
    obj.functionInc       // this returns the function as a value, not calling the function
                          // compiler warning: a pure expression does nothing in statement position
    println(obj.counter)  // 3
    obj.functionInc()     // we need () to call a Function1 object
    println(obj.counter)  // 4
  }
}
