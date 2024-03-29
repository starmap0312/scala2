// ref: https://www.baeldung.com/java-primitives

public class JavaMemberDefaultValue {

    public Integer aInteger;
    public Double aDouble;
    public Boolean aBoolean;
    public String aString;

    public int i;
    public double d;
    public boolean b;
    public char c;

    public static void main(String[] args) {

        JavaMemberDefaultValue o = new JavaMemberDefaultValue();

        System.out.println(o.aInteger); // null
        System.out.println(o.aDouble);  // null
        System.out.println(o.aBoolean); // null
        System.out.println(o.aString);  // null

        System.out.println(o.i);        // 0
        System.out.println(o.d);        // 0.0
        System.out.println(o.b);        // false
        System.out.println(o.c);        //  
    }
}