import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaOptional {

    protected static Optional<Integer> optionalMethod(Integer number) {
        return (number % 2 == 0)? Optional.of(number): Optional.empty();
    }

    public static void main(String[] args) {
        // 0)
        // https://www.geeksforgeeks.org/optional-or-method-in-java-with-examples/
        Integer num1 = Optional.of(10).orElse(20);
        System.out.println(num1); // 10

        Integer num2 = Optional.ofNullable((Integer) null).orElse(20);
        System.out.println(num2); // 20

        Integer num3 = Optional.ofNullable((Integer) null).flatMap(x -> Optional.ofNullable((Integer) null)).or(() -> Optional.of(30)).get();
        System.out.println(num3); // 30

        Optional<Integer> num4 = Optional.ofNullable((Integer) null).flatMap(x -> Optional.ofNullable((Integer) null)).or(() -> Optional.ofNullable((Integer) null));
        System.out.println(num4); // Optional.empty

        // 1) map() -> filter():
        List<Optional> list1 = Arrays.asList(1, 2, 3).
            stream().
            map(number -> optionalMethod(number)).
            collect(Collectors.toList());
        List<Optional> list2 = Arrays.asList(1, 2, 3).
            stream().
            map(number -> optionalMethod(number)).
            filter(Optional::isPresent).
            collect(Collectors.toList());
        System.out.println(list1); // [1, 2, 3] -> [Optional.empty, Optional[2], Optional.empty]
        System.out.println(list2); // [1, 2, 3] -> [Optional[2]]

        // 2) map() -> filter() -> findFirst() -> orElse():
        Optional opt1 = Arrays.asList(1, 2, 3).
            stream().
            map(number -> optionalMethod(number)).
            filter(Optional::isPresent). // Stream([Optional[2]])
            findFirst().                 // find first element if exists (Optional.of(value)); otherwise, return Optional.empty()
            orElse(Optional.of(100));
        Optional opt2 = (Optional) Arrays.asList(1, 2, 3).
            stream().
            map((number) -> Optional.empty()). // Stream(Optional.empty(), Optional.empty(), Optional.empty())
            flatMap(opt -> opt.isPresent()? Stream.of(opt.get()): Stream.empty()). // Stream([])
            findFirst().
            orElse(Optional.of(100));
        System.out.println(opt1); // Optional[2]
        System.out.println(opt2); // Optional[100]

        // 3) map() -> flatMap() -> findFirst() -> orElse():
        Integer num = Arrays.asList(1, 2, 3).
            stream().                              // Stream(1, 2, 3)
            map(number -> optionalMethod(number)). // Stream(Optional, Optional, Optional)
            flatMap(opt -> opt.isPresent()? Stream.of(opt.get()): Stream.empty()). // Stream([value])
            findFirst().       // Optional([value])
            orElse(100); // get [value] orElse [default]
        System.out.println(num);  // 2
    }
}
