import java.text.BreakIterator
import java.util.Locale

object BreakIteratorTest {

  def main(args: Array[String]): Unit = {
    // Split string into sentences
    val iterator = BreakIterator.getSentenceInstance(Locale.US)
    val source = "This is a test. This is a T.L.A. test. Now with a Dr. in it."
    iterator.setText(source)
    var start = iterator.first
    var end = iterator.next
    while (end != BreakIterator.DONE) {
      println(s"($start, $end): ${source.substring(start, end)}")
      start = end
      end = iterator.next
    }
    // (0, 16): This is a test.
    // (16, 39): This is a T.L.A. test.
    // (39, 60): Now with a Dr. in it.
  }
}
