// implement scala trait in java code
// 1) you CAN NOT use the implemented methods of a Scala trait from Java
//    you need to wrap the trait in a class
// 1) a Java class can’t extend a Scala trait that has implemented methods
/*
public class JavaMyTraitImpl implements MyTrait {
    private String name = null;

    public JavaMyTraitImpl(String name) {
        super();
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
*/
