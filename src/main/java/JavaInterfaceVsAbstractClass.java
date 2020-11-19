// https://www.geeksforgeeks.org/difference-between-abstract-class-and-interface-in-java/
// Type of methods:
//   interface: only abstract methods, i.e. no implementation
//   abstract class: have both abstract and "non-abstract" methods, i.e. can be partially implemented
// Multiple implementation
//   interface: extend another Java interface only
//   abstract class: extend another Java class and implement multiple interfaces
// Type of variables:
//   interface: only static and final variables
//   abstract class: static, final, non-final, and non-static variables
interface InterfaceA {
    int staticVar = 0; // a static (i.e. a class level variable) and final (i.e. constant) variable
    int getVar();
    void setVar(int v);
}

interface InterfaceB extends InterfaceA { // an interface can only extend another Java interface
    void b();
}

class ConreteClass implements InterfaceA {

    @Override
    public int getVar() {
        return this.staticVar;
    }

    @Override
    public void setVar(int v) {
//        this.var = v; // it's a final variable, so we cannot assign a value to final variable 'var' (compile-time check)
    }
}

abstract class AbstractA {
    static final int staticVar = 0;
    int var = 1;

    abstract void absMethod(); // abstract method
    void method() {
        System.out.println("method partially implemented in abstract class");
    }

}

public class JavaInterfaceVsAbstractClass {

    public static void main(String[] args) {
        System.out.println(InterfaceA.staticVar); // 0, it's a static variable, so we can access it through the class name
        System.out.println(InterfaceB.staticVar); // 0
        ConreteClass c = new ConreteClass();
        System.out.println(ConreteClass.staticVar); // 0
        System.out.println(c.getVar()); // 0

        AbstractA a = new AbstractA() {
            @Override
            void absMethod() {
                System.out.println("method implemented in a concrete class");
            }
        };
        System.out.println(AbstractA.staticVar); // 0
        System.out.println(a.staticVar); // 0
        System.out.println(a.var); // 1
        a.absMethod(); // method implemented in a concrete clas
        a.method(); // method partially implemented in abstract class
    }
}
