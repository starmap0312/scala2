import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class JavaReactor {

    private static String apply(String x) {
        try {
            System.out.println("  in apply: tread name: " + Thread.currentThread().getName() + ", input: " + x);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return x;
    }

    String methodThatThrowsExecption(String input) {
        System.out.println("in methodThatThrowsExecption: input=" + input);
        if (input.isEmpty()) throw new RuntimeException("input is empty");
        return input;
    }

    public static void main(String[] args) throws InterruptedException {

        JavaReactor reactor = new JavaReactor();

        // 0.1) basics
        System.out.println("0.1)");
        Mono.just(1)
            .doOnNext(x -> apply(x.toString()))
            .map(x -> x + 1)
            .doOnNext(x -> apply(x.toString()))
            .map(x -> x + 1)
            .doOnNext(x -> apply(x.toString()))
            .map(x -> x + 1)
            .block();
        // 1 2 3
        System.out.println();

        // 0.2)
        System.out.println("0.2)");
        Flux.just("red", "white", "blue")
            .log()
            .map(String::toUpperCase)
            .subscribe(color -> System.out.println(color));
        // [main] INFO reactor.Flux.Array.1 - | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
        // [main] INFO reactor.Flux.Array.1 - | request(unbounded)
        // [main] INFO reactor.Flux.Array.1 - | onNext(red)
        // [main] INFO reactor.Flux.Array.1 - | onNext(white)
        // [main] INFO reactor.Flux.Array.1 - | onNext(blue)
        // [main] INFO reactor.Flux.Array.1 - | onComplete()
        // RED
        // WHITE
        // BLUE
        System.out.println();

        // 1.1) subscribe
        //    subscribe Consumer to the Mono
        //    i.e. consume all the elements in the sequence, handle errors and react to completion.
        System.out.println("1.1)");
        // RuntimeException is caught by Mono
        var monoError1 = Mono.just("")
            .map(reactor::methodThatThrowsExecption)
            .doOnError(ex -> System.out.println("doOnError"));
        monoError1.subscribe(
            (it)-> System.out.println("OnNext"),   // OnNext
            (ex) -> System.out.println("OnError"),  // OnError
            () -> System.out.println("OnComplete") // OnComplete
        );
        // in methodThatThrowsExecption: input=
        // doOnError
        // OnError
        System.out.println();

        var monoError2 = Mono.defer(() ->
                Mono.just(reactor.methodThatThrowsExecption(""))
            )
            .doOnError(ex -> System.out.println("doOnError"));
        monoError2.subscribe(
            (it)-> System.out.println("OnNext"),   // OnNext
            (ex) -> System.out.println("OnError"),  // OnError
            () -> System.out.println("OnComplete") // OnComplete
        );
        // in methodThatThrowsExecption: : input=
        // doOnError
        // OnError
        System.out.println();

        // note: this throws RuntimeException
        try {
            var monoError3 = Mono.just(reactor.methodThatThrowsExecption(""))
                .doOnError(ex -> System.out.println("doOnError"));
            monoError3.subscribe(
                (it)-> System.out.println("OnNext"),   // OnNext
                (ex) -> System.out.println("OnError"),  // OnError
                () -> System.out.println("OnComplete") // OnComplete
            );
        } catch (Exception ex) {
            System.out.println("throws RuntimeException");
        }
        // in methodThatThrowsExecption: input=
        // throws RuntimeException
        System.out.println();

        // 1.2) subscribe vs. block
        System.out.println("1.2)");
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

        // 1.3)
        System.out.println("1.3)");
        System.out.println("start of main: tread name: " + Thread.currentThread().getName());
        Mono.just("123")
            .map(JavaReactor::apply)
            .doOnNext(input -> System.out.println("  in mono: tread name: " + Thread.currentThread().getName()))
            .subscribeOn(Schedulers.boundedElastic())
            .doOnNext(input -> System.out.println("  in mono: tread name: " + Thread.currentThread().getName()))
            .subscribe();
        System.out.println("end of main: tread name: " + Thread.currentThread().getName());
        System.out.println("wait for async result: " + Thread.currentThread().getName());
        Thread.sleep(3000);
        System.out.println();
        // start of main: tread name: main
        // end of main: tread name: main
        // wait for async result: main
        //   in apply: tread name: boundedElastic-1, input: 123
        //   in mono: tread name: boundedElastic-1
        //   in mono: tread name: boundedElastic-1

        // 1.4)
        System.out.println("1.4)");
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

        // 2) basics
        System.out.println("2)");
        var empty = Mono.empty();
        System.out.println(empty.block());                                            // null
        System.out.println(empty.blockOptional());                                    // Optional.empty
        System.out.println(Mono.justOrEmpty(Optional.empty()).blockOptional()); // Optional.empty
        System.out.println();

        // 3.1) mono w/ blocking calls
        Function<String, String> blockingCall = (String input) -> {
            try {
                System.out.println("    in blockingCall: tread name: " + Thread.currentThread().getName());
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return input;
        };

        System.out.println("3.1)");
        System.out.println("start of main: tread name: " + Thread.currentThread().getName());
        Mono.just("123")
            .doOnNext(input -> System.out.println("  in mono: tread name: " + Thread.currentThread().getName()))
            .map(input -> blockingCall.apply(input))
            .doOnNext(input -> System.out.println("  in mono: tread name: " + Thread.currentThread().getName()))
            .block();
        System.out.println("end of main: tread name: " + Thread.currentThread().getName());
        // start of main: tread name: main
        //   in mono: tread name: main                <- always work on the main thread
        //     in blockingCall: tread name: main
        //   end of long blocking call
        //   in mono: tread name: main
        // end of main: tread name: main
        System.out.println();

        // 3.2)
        System.out.println("3.2)");
        System.out.println("start of main: tread name: " + Thread.currentThread().getName());
        Mono.just("123")
            .doOnNext(input -> System.out.println("c" + Thread.currentThread().getName()))
            .publishOn(Schedulers.boundedElastic())
            .map(input -> blockingCall.apply(input))
            .doOnNext(input -> System.out.println("  in mono: tread name: " + Thread.currentThread().getName()))
            .block();
        System.out.println("end of main: tread name: " + Thread.currentThread().getName());
        System.out.println();
        // start of main: tread name: main
        //   in mono: tread name: main                <- main thread
        //     in blockingCall: boundedElastic-1      <- publish on a separate thread
        //   in mono: tread name: boundedElastic-1    <- continue to work on the separate thread
        // end of main: tread name: main

        // 3.3)
        System.out.println("3.3)");
        System.out.println("start of main: tread name: " + Thread.currentThread().getName());
        Mono.just("123")
            .doOnNext(input -> System.out.println("  in mono: tread name: " + Thread.currentThread().getName()))
            .map(input -> blockingCall.apply(input))
            .subscribeOn(Schedulers.boundedElastic())
            .doOnNext(input -> System.out.println("  in mono: tread name: " + Thread.currentThread().getName()))
            .block();
        System.out.println("end of main: tread name: " + Thread.currentThread().getName());
        System.out.println();
        // start of main: tread name: main
        //   in mono: tread name: boundedElastic-1    <- main thread
        //     in blockingCall: boundedElastic-1      <- publish on a separate thread
        //   in mono: tread name: boundedElastic-1    <- continue to work on the separate thread
        // end of main: tread name: main

        // 3.4)
        System.out.println("3.4)");
        System.out.println("start of main: tread name: " + Thread.currentThread().getName());
        Mono.just("123")
            .doOnNext(input -> System.out.println("  in mono: tread name: " + Thread.currentThread().getName()))
            .publishOn(Schedulers.boundedElastic())
            .map(input -> blockingCall.apply(input))
            .subscribeOn(Schedulers.boundedElastic())
            .doOnNext(input -> System.out.println("  in mono: tread name: " + Thread.currentThread().getName()))
            .block();
        System.out.println("end of main: tread name: " + Thread.currentThread().getName());
        System.out.println();
        // start of main: tread name: main
        //   in mono: tread name: boundedElastic-1    <- main thread
        //     in blockingCall: boundedElastic-2      <- publish on a separate thread
        //   in mono: tread name: boundedElastic-2    <- continue to work on the separate thread
        // end of main: tread name: main

        // 4) Mono.fromFuture:
        System.out.println("4)");
        System.out.println("start: thread name: " + Thread.currentThread().getName());
        var future = new CompletableFuture<String>();
        var monoFuture = Mono.fromFuture(future).map(val -> {
            System.out.println("  in mono: thread name: " + Thread.currentThread().getName());
            return val + "1";
        });
        // the future is completed in a new thread called completer
        new Thread(() -> {
            try {
                System.out.println("  in thread: thread name: " + Thread.currentThread().getName());
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
            }
            future.complete("completed");
        }, "completer").start();
        monoFuture.block();
        System.out.println("end: thread name: " + Thread.currentThread().getName());
        // start: thread name: main
        //   in thread: thread name: completer
        //   in mono: thread name: completer
        // end: thread name: main

    }
}
