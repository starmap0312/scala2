import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Logger;

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
        // Create a Logger
        Logger log = Logger.getLogger(JavaReactor.class.getName());

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
            .subscribe(value ->
                System.out.println("Consumed: " + value + ", thread name: " + Thread.currentThread().getName())
            );
        // [main] INFO reactor.Flux.Array.1 - | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
        // [main] INFO reactor.Flux.Array.1 - | request(unbounded)
        // [main] INFO reactor.Flux.Array.1 - | onNext(red)
        // [main] INFO reactor.Flux.Array.1 - | onNext(white)
        // [main] INFO reactor.Flux.Array.1 - | onNext(blue)
        // [main] INFO reactor.Flux.Array.1 - | onComplete()
        // Consumed: RED, thread name: main
        // Consumed: WHITE, thread name: main
        // Consumed: BLUE, thread name: main
        System.out.println();
        Thread.sleep(2000);

        // 0.3)
        System.out.println("0.3)");
        Flux.just("red", "white", "blue")
            .log()
            .flatMap(value -> Mono.just(value.toUpperCase()).log().subscribeOn(Schedulers.boundedElastic()))
            .subscribe(value ->
                System.out.println("Consumed: " + value + ", thread name: " + Thread.currentThread().getName())
            );
        // [main] INFO reactor.Flux.Array.2 - | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
        // [main] INFO reactor.Flux.Array.2 - | request(256)
        // [main] INFO reactor.Flux.Array.2 - | onNext(red)
        // [main] INFO reactor.Flux.Array.2 - | onNext(white)
        //   [boundedElastic-1] INFO reactor.Mono.Just.3 - | onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
        //   [boundedElastic-1] INFO reactor.Mono.Just.3 - | request(32)
        //   [boundedElastic-1] INFO reactor.Mono.Just.3 - | onNext(RED)
        // [main] INFO reactor.Flux.Array.2 - | onNext(blue)
        //   [boundedElastic-1] INFO reactor.Mono.Just.3 - | onComplete()
        //   [boundedElastic-2] INFO reactor.Mono.Just.4 - | onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
        //   [boundedElastic-1] INFO reactor.Flux.Array.2 - | request(1)
        //   [boundedElastic-2] INFO reactor.Mono.Just.4 - | request(32)
        //   [boundedElastic-2] INFO reactor.Mono.Just.4 - | onNext(WHITE)
        // [main] INFO reactor.Flux.Array.2 - | onComplete()
        //   [boundedElastic-3] INFO reactor.Mono.Just.5 - | onSubscribe([Synchronous Fuseable] Operators.ScalarSubscription)
        //   [boundedElastic-2] INFO reactor.Mono.Just.4 - | onComplete()
        //   [boundedElastic-3] INFO reactor.Mono.Just.5 - | request(32)
        //   [boundedElastic-3] INFO reactor.Mono.Just.5 - | onNext(BLUE)
        //   [boundedElastic-3] INFO reactor.Mono.Just.5 - | onComplete()
        // Consumed: RED, thread name: boundedElastic-1
        // Consumed: WHITE, thread name: boundedElastic-2
        // Consumed: BLUE, thread name: boundedElastic-3
        System.out.println();
        Thread.sleep(2000);

        // 0.4)
        System.out.println("0.4)");
        Flux.just("red", "white", "blue")
            .log()
            .map(String::toUpperCase)
            .subscribeOn(Schedulers.newParallel("sub"))
            .publishOn(Schedulers.newParallel("pub"))
            .subscribe(value ->
                System.out.println("Consumed: " + value + ", thread name: " + Thread.currentThread().getName())
            );
        // Consumed: RED, thread name: pub-1
        // Consumed: WHITE, thread name: pub-1
        // Consumed: BLUE, thread name: pub-1
        // [sub-2] INFO reactor.Flux.Array.6 - | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
        // [sub-2] INFO reactor.Flux.Array.6 - | request(256)
        // [sub-2] INFO reactor.Flux.Array.6 - | onNext(red)
        // [sub-2] INFO reactor.Flux.Array.6 - | onNext(white)
        // [sub-2] INFO reactor.Flux.Array.6 - | onNext(blue)
        // [sub-2] INFO reactor.Flux.Array.6 - | onComplete()
        System.out.println();
        Thread.sleep(2000);

        // 0.5)
        System.out.println("0.5)");
        Flux.just("red", "white", "blue")
            .log()
            .map(String::toUpperCase)
            .publishOn(Schedulers.newParallel("pub"))
            .subscribe(value ->
                System.out.println("Consumed: " + value + ", thread name: " + Thread.currentThread().getName())
            );
        // Consumed: RED, thread name: pub-3
        // Consumed: WHITE, thread name: pub-3
        // Consumed: BLUE, thread name: pub-3
        // [main] INFO reactor.Flux.Array.7 - | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
        // [main] INFO reactor.Flux.Array.7 - | request(256)
        // [main] INFO reactor.Flux.Array.7 - | onNext(red)
        // [main] INFO reactor.Flux.Array.7 - | onNext(white)
        // [main] INFO reactor.Flux.Array.7 - | onNext(blue)
        // [main] INFO reactor.Flux.Array.7 - | onComplete()
        System.out.println();
        Thread.sleep(2000);

        // 0.6)
        System.out.println("0.6)");
        Flux.fromIterable(List.of("A", "B"))
            .map(e -> apply(e))
            .subscribe(e -> System.out.println(Thread.currentThread().getName() + " from first list, got " + e));

        Flux.fromIterable(List.of("C", "D"))
            .map(url -> apply(url))
            .subscribe(e -> System.out.println(Thread.currentThread().getName() + " from second list, got " + e));
        //   in apply: tread name: main, input: A
        // main from first list, got A
        //   in apply: tread name: main, input: B
        // main from first list, got B
        //   in apply: tread name: main, input: C
        // main from second list, got C
        //   in apply: tread name: main, input: D
        // main from second list, got D
        System.out.println();

        // 0.7)
        System.out.println("0.7)");
        Flux.fromIterable(List.of("A", "B"))
            .map(e -> apply(e))
            .publishOn(Schedulers.boundedElastic())
            .map(e -> apply(e))
            .subscribe(e -> System.out.println(Thread.currentThread().getName() + " from first list, got " + e));

        Flux.fromIterable(List.of("C", "D"))
            .map(e -> apply(e))
            .publishOn(Schedulers.boundedElastic())
            .map(url -> apply(url))
            .subscribe(e -> System.out.println(Thread.currentThread().getName() + " from second list, got " + e));
        // note: the above two Flux DO NOT block each other:
        //   in apply: tread name: main, input: A               (A)          <- main
        //   in apply: tread name: main, input: B                  (B)       <- main
        //   in apply: tread name: boundedElastic-3, input: A   (A)
        //   in apply: tread name: main, input: C                     (C)    <- main
        // boundedElastic-3 from first list, got A              (A)
        //   in apply: tread name: boundedElastic-3, input: B      (B)
        //   in apply: tread name: main, input: D                        (D) <- main
        //   in apply: tread name: boundedElastic-2, input: C         (C)
        // boundedElastic-3 from first list, got B                 (B)
        // boundedElastic-2 from second list, got C                   (C)
        //   in apply: tread name: boundedElastic-2, input: D            (D)
        // boundedElastic-2 from second list, got D                      (D)
        System.out.println();

        Thread.sleep(3000);

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
