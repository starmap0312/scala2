
object RegExpTest {
  def main(args: Array[String]): Unit = {
    // 1) replace regular expression patterns in Java String (also Scala String)
    // 1.1) Java string.replaceAll([String regex], [String replacement]):
    //      it returns Pattern.compile(regex).matcher(this).replaceAll(replacement)
    val str = "abc123efg"
    println(str.replaceAll("123", "XXX")) // abcXXXefg
    println(str.replaceAll("[0-9]", "X")) // abcXXXefg
    // 1.2) Scala regex.replaceAllIn([target: CharSequence], [replacement: String])
    //      it returns pattern.matcher(target).replaceAll(replacement)
    val regex = "[0-9]".r                 // scala.util.matching.Regex = [0-9]
    println(regex.replaceAllIn(str, "X")) // abcXXXefg
    // 1.3) Scala regex.findFirstIn(source: CharSequence)
    //      it returns an optional first matching string of this regex found in the given source CharSequence
    //      it returns None if not found
    println(regex.findFirstIn(str))       // Some(1)
  }
}
