import java.util.Arrays;
import java.util.List;

// Java & Scala List:
// 1) import scala.collection.JavaConversions._ or import scala.collection.JavaConverters._
//    scala.collection.mutable.Buffer <=> java.util.List (a two-way conversion is provided by the above imports)

public class ScalaListInJava {
    public static void main(String[] args) {
        // java.util.List
        // 1) convert the Java List to a Scala Buffer
        List<String> javaList = Arrays.asList("abc", "def", "gh");
        //scala.collection.mutable.Buffer<String> scalaBuffer = scala.collection.JavaConversions.asScalaBuffer(javaList);
        scala.collection.mutable.Buffer<String> scalaBuffer = scala.collection.JavaConverters.asScalaBuffer(javaList);
        // 1.1) buffer.toList(): convert a Scala Buffer to to a Scala List
        scala.collection.immutable.List<String> scalaList = scalaBuffer.toList();
        System.out.println(scalaList);                                                            // List(abc, def, gh)
        // 1.2) buffer.toSeq(): convert a Scala Buffer to a scala.collection.immutable.Seq
        scala.collection.immutable.Seq<String> stringSeq = scalaList.toSeq();
        System.out.println(stringSeq);                                                             // List(abc, def, gh)
    }
}
