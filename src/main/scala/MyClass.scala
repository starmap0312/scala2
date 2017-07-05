import java.io.IOException

// there are four major items to consider when using a Scala class from Java:
// 1) Class parameters
// 1.1) declare a class constructor parameter without val/var
//      i.e. constructor args in Java (so you cannot access them outside the class)
// 1.2) declare a class constructor parameter with val/var
//      class MyClass(val/var name: String) { }
//
//      (is equivalent to the following)
//
//      class MyClass(name_: String) {
//        val/var name = name_  // use name() to access the value in Java
//      }
//
// 2) Class vals
//    a getter method is automatically defined for access in Java (so you can access the value outside the class)
//    ex. val foo ==> use the class method: foo() to access the value in Java
// 3) Class vars
//    a setter method _$eq automatically defined in Java
//    ex. var foo ==> use the class method: foo$_eq("newfoo");
//    note: you can use the @BooleanBeanProperty annotation to get POJO getter/setter definitions
//    ex. setFoo("newfoo");
//        getFoo();
// 4) Exceptions
//    Scala does not have checked exceptions, but Java does

  // 1) Class parameters
class MyClass(name: String, val acc: String, var mutable: String) {
  // i.e. val name = name_

  // 2) Class vals
  val foo = "foo"
  // 3) Class vars
  var bar = "bar"

  // 4) Exceptions: bad practice (Java complains that the body of s.dangerFoo never throws IOException)
  def dangerFoo() = {
    throw new IOException("SURPRISE!")
  }

    // 4) Exceptions: good practice (this allows us to continue using checked exceptions in Java)
  @throws(classOf[IOException])
  def dangerBar() = {
    throw new IOException("NO SURPRISE!")
  }
}
