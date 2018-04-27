package basics;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaOptional {

    public static void main(String[] args) {
        List<Optional> list1 = Arrays.asList(1, 2, 3).
            stream().
            map(
                (number) -> (number % 2 == 0)? Optional.of(number): Optional.empty()
            ).collect(Collectors.toList());
        List<Optional> list2 = Arrays.asList(1, 2, 3).
            stream().
            map(
                (number) -> (number % 2 == 0)? Optional.of(number): Optional.empty()
            ).filter(
                Optional::isPresent
            ).collect(Collectors.toList());
        System.out.println(list1); // [Optional.empty, Optional[2], Optional.empty]
        System.out.println(list2); // [Optional[2]]

        Optional opt1 = Arrays.asList(1, 2, 3).
            stream().
            map(
                (number) -> (number % 2 == 0)? Optional.of(number): Optional.empty()
            ).filter(
                Optional::isPresent
            ).
            findFirst().
            orElse(Optional.of(100));
        Optional opt2 = Arrays.asList(1, 2, 3).
            stream().
            map(
                (number) -> Optional.empty()
            ).filter(
                Optional::isPresent
            ).
            findFirst().
            orElse(Optional.of(100));
        Optional opt3 = (Optional) Arrays.asList(1, 2, 3).
            stream().
            map(
                (number) -> Optional.empty() // Stream(Optional)
            ).
            flatMap(                         // Stream(Stream) => Stream
                o -> o.isPresent()? Stream.of(o.get()): Stream.empty()
            ).
            findFirst().
            orElse(Optional.of(100));
        Optional<Integer> opt4 = (Optional) Arrays.asList(1, 2, 3).
            stream().
            map(
                (number) -> (number % 2 == 0)? Optional.of(number): Optional.empty() // Stream(Optional)
            ).
            flatMap(                         // Stream(Stream) => Stream
                o -> o.isPresent()? Stream.of(o.get()): Stream.empty()
            ).
            findFirst();
        System.out.println(opt1); // Optional[2]
        System.out.println(opt2); // Optional[100]
        System.out.println(opt3); // Optional[100]
        System.out.println(opt4.orElse(100)); // 2
    }
}
