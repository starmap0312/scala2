// implement scala trait in java code
// 1) you CAN NOT use the implemented methods of a Scala trait from Java
//    you need to wrap the trait in a class
// 1) a Java class can’t extend a Scala trait that has implemented methods
/*
public class JavaMyTraitImpl implements MyTrait {
    private String name = null;

    public JavaMyTraitImpl(String name) {
        this.name = name;
    }

    @Override
    public String traitName() {      // provide the implementation
        return this.name;
    }

    @Override
    public String upperTraitName() { // delegate to the additional compiled class: MyTrait$class
        return MyTrait$class.upperTraitName(this);
    }

    public static void main(String[] args) {
        new JavaMyTraitImpl("hello");
    }
}
// cannot find symbol MyTrait$class (don't know why the additional class is not compiled)
*/

// to implement a partially implemented scala trait, we need a scala class wrapper
// so you may define a abstract class in scala directly if you need to use it in Java code
public class JavaMyTraitImpl extends MyTraitWrapper {
    private String name = null;

    public JavaMyTraitImpl(String name) {
        this.name = name;
    }

    @Override
    public String traitName() {
        return this.name;
    }

    public static void main(String[] args) {
        JavaMyTraitImpl hello = new JavaMyTraitImpl("hello");
        System.out.println(hello.upperTraitName()); // HELLO
    }
}