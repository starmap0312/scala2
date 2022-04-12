// reference: https://popcornylu.gitbooks.io/java_multithread/content/thread.html

public class JavaThread {

    public static void main(String[] args) {

        // 1) create a Thread
        Thread thread = new Thread(() -> {
            System.out.println("hello java thread");
        });
        thread.start(); // start the thread & execute its Runnable#run()

        // 2) synchronization between threads
        class MyClass {
            private int i;

            public synchronized int getAndIncr() {
                return i++;
            }
        }

    }
}