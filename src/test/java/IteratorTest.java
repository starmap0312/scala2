import java.util.*;

public class IteratorTest {
    public static void main(String[] args) {
        // 1) Arrays.asList():
        //    it returns a list, backed by the original array
        //    so changes you make to the list are also reflected in the array you pass in
        //    we cannot add or remove elements to arrays, use ArrayList or LinkedList instead
        //    i.e. we will get UnsupportedOperationException when calling iterator.remove() to remove elements
        List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5);
        Iterator<Integer> iterator1 = list1.iterator();
        while (iterator1.hasNext()) {
            Integer num = iterator1.next();
            //if (num % 2 == 0) iterator.remove(); // UnsupportedOperationException
        }

        // 2) ArrayList
        List<Integer> list2 = new ArrayList(Arrays.asList(1, 2, 3, 4, 5));
        Iterator<Integer> iterator2 = list2.iterator();
        while (iterator2.hasNext()) {
            Integer num = iterator2.next();
            if (num % 2 == 0) iterator2.remove(); // UnsupportedOperationException
        }
        list2.forEach(System.out::println);       // 1 3 5

        // 2) LinkedList
        List<Integer> list3 = new LinkedList(Arrays.asList(1, 2, 3, 4, 5));
        Iterator<Integer> iterator3 = list3.iterator();
        while (iterator3.hasNext()) {
            Integer num = iterator3.next();
            if (num % 2 == 0) iterator3.remove(); // UnsupportedOperationException
        }
        list3.forEach(System.out::println);       // 1 3 5

    }
}
