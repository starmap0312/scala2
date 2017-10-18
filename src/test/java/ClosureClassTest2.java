
import org.junit.Test;
import scala.runtime.AbstractFunction0;
import scala.runtime.AbstractFunction1;

public class ClosureClassTest2 {

    @Test
    public void closureTest() {
        ClosureClass c = new ClosureClass();
        c.printResult(new AbstractFunction0() { // implement Scala's Function0 in Java
            public String apply() {
                return "hello world";
            }
        });
        c.printResult(new AbstractFunction1<String, String>() {
            public String apply(String arg) { // implement Scala's Function1 in Java
                return arg + " world";
            }
        });
    }

}
