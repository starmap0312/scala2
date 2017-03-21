// Method vs. Function
// 1) methods are NOT values, they don’t have a type and cannot exist on their own
//    they are an attribute of a structure in which they are defined, ex. inside a class, object or trait
//    methods must be defined using the def keyword
// 2) functions are values just like integers, or other objects that can be passed around and returned
//    functions can be defined like any other value, by using def, val or lazy val (based on  when the value is evaluated)
// ETA expansion
// 1) a simple technique for wrapping functions into an extra layer while preserving identical functionality
// 2) it is performed by the compiler to create functions out of methods
// 3) two ways to covert a method into a function manually
//    a) explicitly declare type of value to be a Function1 type
//    b) treat the method as a partially applied function by putting underscores after method name (lambda expression)
object MethodAndFunction {
  def main(args: Array[String]): Unit = {
    // example 0: ETA expansion
    def f1   = "foo"  // String      : a value or expression whose evaluation is performed every time it is accessed
    def f2() = "foo"  // ()String    : a method that returns a string (no instance or object created yet)
    val f3   = f2 _   // () => String: a Function0 instance that can be passed around as a parameter
    println(f1)       // foo
    println(f2())     // foo
    println(f2)       // foo, the parentheses can be omitted if no side effect

    // example1: methods vs. functions
    def method(x: Int): Int = 2 * x                // (x: Int)Int: expression with Int parameter
    // the above is not evaluated yet, i.e. no instance or object created
    val function: (Int) => Int = (x: Int) => 2 * x // (Int) => Int: Function1 instance
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
    function                   // (Int) => Int (Function1 instance)
                               // compiler warning: a pure expression does nothing in statement position
    // 1.2) Parameter list is optional for methods but mandatory for functions
    // a method can have no parameter list
    def method2 = 100          // Int: expression with no parameter
    // a function must have a parameter list
    val function2 = () => 100  // () => Int: Function1 instance
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
