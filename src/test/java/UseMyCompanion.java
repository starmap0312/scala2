public class UseMyCompanion {
    public static void main(String[] args) {
        // MySingleton2$.MODULE$ refers to the companion object of Scala class: i.e. object MyCompanion
        MyCompanion hello = MyCompanion$.MODULE$.apply("hello");
        System.out.println(hello.data());
    }
}
