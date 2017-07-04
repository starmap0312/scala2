// Java syntax has no declaration of singleton object
// 1) in Java, we use Singleton pattern instead, i.e. one public static field referencing a single instance
// 2) for every Scala singleton object, the compiler creates a Java class with a dollar sign added to the end
// example: object MySingleton (Scala) -> public final class MySingleton$ (Java)
//          MySingleton$.MODULE$ refers to the singleton object created by compiler at run-time
object MySingleton {
  def main(args: Array[String]) {
    println("Hello, world!")
  }
}
//
// the above is complied as the following Java class:
//
// public final class MySingleton$ extends java.lang.Object implements scala.ScalaObject {
//
//     public static final MySingleton$ MODULE$;
//     // the above static field: MODULE$ holds the single instance of the class (singleton) created at run time
//     // i.e. MySingleton$.MODULE$
//
//     public static {};
//     public App$();
//     public void main(java.lang.String[]);
//     public int $tag();
// }