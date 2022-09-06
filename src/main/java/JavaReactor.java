import reactor.core.publisher.Mono;

public class JavaReactor {

    String methodThatThrowsExecption(String input) {
        if (input.isEmpty()) throw new RuntimeException("input is empty");
        return input;
    }

    public static void main(String[] args) throws InterruptedException {

        JavaReactor reactor = new JavaReactor();

        var mono = Mono.just("")
            .map(reactor::methodThatThrowsExecption);
        mono.subscribe(
            (it)-> System.out.println("OnNext"),   // OnNext
            (e) -> System.out.println("OnError"),  // OnError
            () -> System.out.println("OnComplete") // OnComplete
        );
        // OnError: RuntimeException is caught by Mono
    }
}
