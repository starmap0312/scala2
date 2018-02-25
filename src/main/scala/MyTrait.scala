// Scala trait in Java:
// trait MyTrait will be complied to the following two Java classes:
// 1) MyTrait (a Java interface without implementation)
//
//    public interface MyTrait extends scala.ScalaObject {
//      public abstract String traitName();
//      public abstract String upperTraitName();
//    }
//
// 2) MyTrait$class (an additional compiled Java class with implementation)
//
//    public abstract class MyTrait$class extends Object {
//      public static String upperTraitName(MyTrait);
//      public static void $init$(MyTrait);
//    }
//
// 3) MyTrait       is like an "interface"      in Java
//    MyTrait$class is like an "abstract class" in Java

//
// so we can implement the Scala trait in Java as follows:
//
//    public class JavaMyTraitImpl implements MyTrait { // implement the compiled Java interface: MyTrait
//      private String name = null;
//      public JavaMyTraitImpl(String name) {
//        this.name = name;
//      }
//
//      public String upperTraitName() {           // delegate to the additional compiled class: MyTrait$class
//        return MyTrait$class.upperTraitName(this);
//      }
//
//      public String traitName() {                // provide our own implementation
//        return name;
//      }
//    }

trait MyTrait {
  def traitName: String                      // unimplemented method in the interface
  def upperTraitName = traitName.toUpperCase // implemented method in the interface
}

abstract class MyTraitWrapper extends MyTrait {
}

abstract class MyAbstractClass {
  def traitName: String                      // unimplemented method in the interface
  def upperTraitName = traitName.toUpperCase // implemented method in the interface
}

