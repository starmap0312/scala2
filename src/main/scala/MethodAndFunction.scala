// Expression vs. Method vs. Function
// 1) methods are NOT values, they don’t have a type and cannot exist on their own
//    methods are like expressions with parameters; their values are evaluated only when we providing them the parameters
//    they are an attribute of a structure in which they are defined
//      ex. inside a class, object or trait
//    methods must be defined using the def keyword
// 2) functions are values just like objects that can be passed around and returned
//    functions are defined like any other value or objects
//    functions can be defined using def, val or lazy val (based on  when the value is evaluated)
// ETA expansion
// 1) a simple technique for wrapping functions into an extra layer while preserving identical functionality
// 2) it is performed by the compiler to create functions out of methods
// 3) there are two ways to explicitly covert a method into a function
//    a) explicitly declare type of value to be a Function1 type
//       ex. val func: (Int) => Int = method
//    b) treat the method as a partially applied function by putting underscores after method name (lambda expression)
object MethodAndFunction {
  def main(args: Array[String]): Unit = {
    // 1) expression vs. method vs. function
    // 1.1) a expression can be evaluated to a value, at different times based on def/lazy val/val
    // 1.2) a method is an expression which can be evaluated only if we provide them parameters (it can be of zero parameter)
    // 1.3) a function is a FunctionN instance, i.e. Lambda, which can be passed around
    def expression = "foo"    // String      : a expression  which can be evaluated to a String every time it is accessed
    def method()   = "foo"    // ()String    : a method which can be evaluated to a String every time is is called by providing zero parameter
    println(expression)       // foo
    println((expression _)()) // foo
    println(method())         // foo
    println(method )          // foo, a syntax sugar of omitting () when calls a method with no argument and side effect
    println((method _)())     // foo, the parentheses cannot be omitted, as it will then represent a Function1 instance (i.e. Lambda)

    // 2) two ways to explicitly convert a method into a function
    //    i.e. ETA expansion (create a lambda instance)
    // 2.1) explicitly define the type as FunctionN type
    def function0 = expression _              // i.e. type: () => String
    def function1: Function1[Int, Int] = {    // i.e. type: (Int) => Int
      x => x + 1
    }
    def function2:(Int) => Int = {            // i.e. type: (Int) => Int
      _ + 1
    }
    def function3 = new Function1[Int, Int] { // i.e. type: (Int) => Int
       override def apply(x: Int) = x + 1
    }
    def function4 = new Function1[Int, Int] { // i.e. type: (Int) => Int
      override def apply(x: Int) = x + 1
    }
    // 2.2) treat a method as a partially applied function by putting underscores (compiler will convert for you)
    def method1(x: Int): Int = {              // i.e. type: (Int)Int
      x + 1
    }
    def function5 = method1(_)                // i.e. type: (Int) => Int
    println(function0())                      // foo
    println(function1(1))                     // 2
    println(function2(1))                     // 2
    println(function3(1))                     // 2
    println(function4(1))                     // 2
    println(function5(1))                     // 2

    // 2.3) ETA expansion: what compiler does behind the scene
    //      in Scala, we can provide a method when a function is expected (not recommended)
    //      the method will be automatically converted into a Function1 by compiler
    // ex. filter() and map() expect a Function1
    println(List(1, 2, 3).map(function1)) // List(2, 3, 4), pass in a Function1
    println(List(1, 2, 3).map(method1))   // List(2, 3, 4), automatically converted into a Function1 by compiler
    println(List(1, 2, 3).map(method1 _)) // List(2, 3, 4), explicitly converted into a Function1 object

    // 3) when is the Function1 instance created?
    // 3.1) a method can't be the final value
    //println(method1)                        // compile error: missing arguments for method
    // 3.2) a function can be the final value (can be passed around)
    println(function1)                        // Lambda

    // 4) expression, method, function defined inside a class
    class MyClass {
      /* constructor */
      var counter = 0
      def expressionInc = { // i.e. type: Unit
        counter += 1
      }
      def methodInc() = {   // i.e. type: ()Unit
        counter += 1
      }
      def functionInc = {   // i.e. type: () => Unit
        () => counter += 1
      }
    }
    val obj = new MyClass
    println(obj.counter)  // 0
    obj.expressionInc     // this evaluates the expression as it is accessed, thus incrementing the counter
    println(obj.counter)  // 1
    obj.methodInc()       // this calls the method, thus incrementing the counter
    println(obj.counter)  // 2
    // Scala allows the omission of parentheses on calling methods of no argument as long as it has no side effect (unlike println)
    obj.methodInc         // this also calls the method
    println(obj.counter)  // 3
    obj.functionInc       // this does not call the function, but treat it as a value/object
    println(obj.counter)  // 3
    obj.functionInc()     // we cannot omit () when calling a Function1
    println(obj.counter)  // 4

    // 5) infix notation: syntactic sugar for calling class's method member
    class Number(n: Int) {
      val value = n
      def plus(x: Int): Int = {
        value + x
      }
    }
    val num = new Number(1)
    println(num.plus(1)) // 2
    println(num plus(1)) // 2: omit .
    println(num plus 1)  // 2: omit . and ()
  }
}
