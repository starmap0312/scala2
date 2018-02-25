public class JavaMyAbstractClassImpl extends MyAbstractClass {
    private String name = null;

    public JavaMyAbstractClassImpl(String name) {
        this.name = name;
    }

    @Override
    public String traitName() {
        return this.name;
    }

    public static void main(String[] args) {
        JavaMyAbstractClassImpl hello = new JavaMyAbstractClassImpl("hello");
        System.out.println(hello.upperTraitName()); // HELLO
    }
}
