import java.util.Arrays;
import java.util.List;

public class ToScalaList {
    public static void main(String[] args) {
        // convert the Java List to a Scala Buffer
        // 1) a Scala Buffer has a toList() method: a Scala List
        // 2) a Scala List has a toSeq() method: it is a scala.collection.immutable.Seq

        List<String> list = Arrays.asList("abc", "def", "gh");
        scala.collection.mutable.Buffer<String> stringBuffer = scala.collection.JavaConversions.asScalaBuffer(list);
        scala.collection.immutable.List<String> stringList = stringBuffer.toList();
        scala.collection.immutable.Seq<String> stringSeq = stringList.toSeq();
        System.out.println(stringSeq);                                                             // List(abc, def, gh)
        System.out.println(scala.collection.JavaConversions.asScalaBuffer(list).toList().toSeq()); // List(abc, def, gh)
    }
}
