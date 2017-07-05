// Scala trait in Java:
// trait MyTrait will be complied to the following two Java classes:
// 1) MyTrait (a Java interface without implementation)
//
//    public interface com.twitter.interop.MyTrait extends scala.ScalaObject {
//      public abstract java.lang.String traitName();
//      public abstract java.lang.String upperTraitName();
//    }
//
// 2) MyTrait$class (an additional compiled Java class with implementation)
//
//    public abstract class com.twitter.interop.MyTrait$class extends java.lang.Object {
//      public static java.lang.String upperTraitName(com.twitter.interop.MyTrait);
//      public static void $init$(com.twitter.interop.MyTrait);
//    }
//
// so we can implement the Scala trait in Java as follows:
//
//    public class JTraitImpl implements MyTrait { // implement the compiled Java interface: MyTrait
//      private String name = null;
//      public JTraitImpl(String name) {
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
  def traitName: String                      // a not-implemented method in the interface
  def upperTraitName = traitName.toUpperCase // an implemented method in the interface
}
