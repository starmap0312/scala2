// reference: https://popcornylu.gitbooks.io/java_multithread/content/thread.html

public class JavaThread {

    public static void main(String[] args) {

        // 1) create a Thread
        Thread thread = new Thread(() -> {
            System.out.println("hello java thread");
        });
        thread.start(); // start the thread & execute its Runnable#run()

        // 2) synchronization
        // bad practice: 2 treads accessing the same object at the same time
        class Shared {
            private int i;

            public int getAndIncr() {
                return i++;
            }
        }

        Shared sharedObj = new Shared();
        for(int i = 0; i < 2; i++) {
            final int x = i;
            Thread t = new Thread(() -> {
                for(int j = 0; j < 10; j++) {
                    System.out.format("Thread %s: %s\n", x, sharedObj.getAndIncr());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start(); // start the thread & execute its Runnable#run()
        }
        // ...
        // Thread 1: 17
        // Thread 0: 17

        try {
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();

        // good practice: use synchronized for the shard objects between threads
        class SharedSync {
            private int i;

            public int getAndIncr() {
                synchronized (this) { // you need to synchronize on some resource, ex. this (current object)
                    return i++;
                }
            }
        }

        SharedSync sharedSyncObj = new SharedSync();
        for(int i = 0; i < 2; i++) {
            final int x = i;
            Thread t = new Thread(() -> {
                for(int j = 0; j < 10; j++) {
                    System.out.format("Thread %s: %s\n", x, sharedSyncObj.getAndIncr());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start(); // start the thread & execute its Runnable#run()
        }
        // Thread 0: 18
        // Thread 1: 19
    }
}