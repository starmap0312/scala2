// Java is always Pass-by-Value
//   Java has (reference) pointers which is strictly pass-by-value
//   a new reference pointer is always created, so it's pass-by-value
//   the confusion occurs when you manipulates the instance through the new reference pointer, it may change the instance's value, making it work like pass-by-reference
// ref: https://www.javadude.com/articles/passbyvalue.htm
// ref: https://www.cprogramming.com/tutorial/references.html
// TL;DR: Java object reference works just like a C++ pointer
class Dog {
    private String name;

    public Dog(String str) {
        this.name = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }
}


public class JavaIsPassByValue {

    public static void rename(Dog d) { // a new reference pointer is created for the Dog d instance
        d = new Dog("foo"); // a new Dog instance is created and assigned to the new reference pointer, not the original max Dog reference pointer
    }

    public static void setName(Dog d) { // a new reference pointer is created for the Dog d instance
        d.setName("foo"); // set the Dog d instance a new name
    }

    public static void main(String[] args) {

        // 1) Java is always Pass-by-Value
        Dog max = new Dog("Max"); // creating the "max" dog
        System.out.println(max.getName()); // Max: at this point, aDog points to the "max" dog

        rename(max);
        System.out.println(max.getName()); // Max: max still points to the "max" dog

        setName(max); // this sets a new name to the max Dog
        System.out.println(max.getName()); // foo

    }
}