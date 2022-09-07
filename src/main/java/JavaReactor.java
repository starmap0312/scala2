import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class JavaReactor {

    String methodThatThrowsExecption(String input) {
        if (input.isEmpty()) throw new RuntimeException("input is empty");
        return input;
    }

    public static void main(String[] args) throws InterruptedException {

        JavaReactor reactor = new JavaReactor();

        var mono = Mono.just("")
            .map(reactor::methodThatThrowsExecption)
            .doOnError(ex -> System.out.println("doOnError"));

        // 1.1) subscribe
        //    subscribe Consumer to the Mono
        //    i.e. consume all the elements in the sequence, handle errors and react to completion.
        mono.subscribe(
            (it)-> System.out.println("OnNext"),   // OnNext
            (ex) -> System.out.println("OnError"),  // OnError
            () -> System.out.println("OnComplete") // OnComplete
        );
        // RuntimeException is caught by Mono
        // doOnError
        // OnError
        System.out.println();

        // 1.2) subscribe vs. block
        System.out.println("start Thread: " + Thread.currentThread().getName());
        Mono<Integer> optionalMono1 = Mono.just(1);
        optionalMono1
            .subscribe(result -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("subscribe Thread: " + Thread.currentThread().getName());
            });
        System.out.println("end Thread: " + Thread.currentThread().getName());
        System.out.println();
        // start Thread: main
        // subscribe Thread: main
        // end Thread: main

        System.out.println("start Thread: " + Thread.currentThread().getName());
        Mono<Integer> optionalMono2 = Mono.just(1);
        optionalMono2
            .doOnNext(result -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("doOnNext Thread: " + Thread.currentThread().getName());
            })
            .block();
        System.out.println("end Thread: " + Thread.currentThread().getName());
        System.out.println();
        // start Thread: main
        // doOnNext Thread: main
        // end Thread: main

        // 2)
        var empty = Mono.empty();
        System.out.println(empty.block());                                            // null
        System.out.println(empty.blockOptional());                                    // Optional.empty
        System.out.println(Mono.justOrEmpty(Optional.empty()).blockOptional()); // Optional.empty
        System.out.println();

        // mono handle blocking calls
        Function<String, String> blockingCall = (String input) -> {
            System.out.println("start of long blocking call");
            System.out.println("tread name: " + Thread.currentThread().getName());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("end of long blocking call");
            return input;
        };

        var blockingMono1 = Mono.just("123")
            .doOnNext(input -> System.out.println("tread name: " + Thread.currentThread().getName()))
            .map(input -> blockingCall.apply(input))
            .doOnNext(input -> System.out.println("tread name: " + Thread.currentThread().getName()))
            .block();
        System.out.println(blockingMono1);
        // tread name: main                <- always work on the main thread
        // start of long blocking call
        // tread name: main
        // end of long blocking call
        // tread name: main
        // 123
        System.out.println();

        var blockingMono2 = Mono.just("123")
            .doOnNext(input -> System.out.println("tread name: " + Thread.currentThread().getName()))
            .publishOn(Schedulers.boundedElastic())
            .map(input -> blockingCall.apply(input))
            .doOnNext(input -> System.out.println("tread name: " + Thread.currentThread().getName()))
            .block();
        System.out.println(blockingMono2);
        System.out.println();
        // tread name: main                <- main thread
        // start of long blocking call
        // tread name: boundedElastic-1    <- publish on a separate thread
        // end of long blocking call
        // tread name: boundedElastic-1    <- continue to work on the separate thread
        // 123

        var blockingMono3 = Mono.just("123")
            .doOnNext(input -> System.out.println("tread name: " + Thread.currentThread().getName()))
            .map(input -> blockingCall.apply(input))
            .subscribeOn(Schedulers.boundedElastic())
            .doOnNext(input -> System.out.println("tread name: " + Thread.currentThread().getName()))
            .block();
        System.out.println(blockingMono3);
        System.out.println();
        // tread name: boundedElastic-1
        // start of long blocking call
        // tread name: boundedElastic-1
        // end of long blocking call
        // tread name: boundedElastic-1
        // 123

        var blockingMono4 = Mono.just("123")
            .doOnNext(input -> System.out.println("tread name: " + Thread.currentThread().getName()))
            .publishOn(Schedulers.boundedElastic())
            .map(input -> blockingCall.apply(input))
            .subscribeOn(Schedulers.boundedElastic())
            .doOnNext(input -> System.out.println("tread name: " + Thread.currentThread().getName()))
            .block();
        System.out.println(blockingMono4);
        System.out.println();
        // tread name: boundedElastic-1
        // start of long blocking call
        // tread name: boundedElastic-2
        // end of long blocking call
        // tread name: boundedElastic-2
        // 123
    }
}
