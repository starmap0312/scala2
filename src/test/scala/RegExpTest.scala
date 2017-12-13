
object RegExpTest {
  def main(args: Array[String]): Unit = {
    // 1) java.lang.String (also Scala String):
    // 1.1) [target: String].replaceAll([regex: String], [String replacement]): returns String
    //      it replaces all the matched patterns in the String with the replacement String
    //      it uses Pattern.compile(regex).matcher(this).replaceAll(replacement)
    val str = "abc456efg"
    println(str.replaceAll("456", "XXX"))      // abcXXXefg
    println(str.replaceAll("[0-9]", "X"))      // abcXXXefg

    // 1.2) [target: String].matches([regex: String]):
    //      it uses Pattern.matches(regex, this)
    println(str.matches("^[a-z]{1,3}[0-9]{3}[a-z]*$")) // true
    println(str.matches("^[a-z].*"))                   // true
    println(str.matches("^[0-9].*"))                   // false
    println("this is a good news".matches(".*(good|great) news.*"))  // true
    println("this is a good news".matches("(good|great) news"))      // false
    println("this is a great news".matches(".*(good|great) news.*")) // true

    // 2) scala.util.matching.Regex:
    // 2.1) regex.replaceAllIn([target: CharSequence], [replacement: String]): returns String
    //      it replace all the matched pattern in the target String with the replacement String
    //      it also uses pattern.matcher(target).replaceAll(replacement)
    val regex = "[0-9]{2}".r              // scala.util.matching.Regex = [0-9]
    println(regex.replaceAllIn(str, "X")) // abcX6efg
    // 2.2) regex.findFirstIn(target: CharSequence): returns Option([String])
    //      it returns an Optional first matched pattern in the target CharSequence
    //      it returns None if not found
    println(regex.findFirstIn(str))       // Some(45): because "45" is the first digit in "abc456efg"
  }
}
