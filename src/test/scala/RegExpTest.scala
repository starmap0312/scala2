// Positive lookahead vs. Negative lookahead
// i.e. ?=abc  ==> Positive lookahead
//      ?!abc  ==> Negative lookahead
// ^        ==> indicates the position of string head
// $        ==> indicates the position of string end
// ?=abc    ==> indicates the ahead position matching "abc",     i.e. the position ahead of the matched char 'a'
//              asserts that what immediately follows the current position in the string is abc
// ?!abc    ==> indicates the ahead position not matching "abc", i.e. the position ahead of not matched pattern
//              asserts that what immediately follows the current position in the string is not abc

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

    // 1.3) Positive lookahead vs. Negative lookahead
    //      lookaheads do not consume any characters. it just checks if the pattern can be matched or not
    println("Positive lookahead")
    println("Jacks".replaceAll("Jack(?=s)", "Jack'"))   // Jack's: replace ahead position matching 'Jack(s)' with (Jack')
    println("123abc456".replaceAll("(?=abc)", "x"))     // 123xabc456: '3' is the ahead position matching abc

    println("Negative lookahead")
    println("abc".matches("a(?!b)c"))      // false
    println("ac".matches("a(?!b)c"))       // true
    println("abc".matches(".+(?!b).+"))    // true: DO NOT expect this to be false
    println("ac".matches(".+(?!b).+"))     // true
    // note:
    //   ".+(?!b).+": if (?!b) fails, the first .+ will try match a shorter string, and then the check is done again
    //   therefore, ".+(?!b).+" would match bbbabb with the first .+ matching bbb and the second one matching abb

    println("123abc456".replaceAll("(?!abc)", "x"))     // x1x2x3axbxcx4x5x6x: all ahead positions not matching abc

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

    val regex2 = "https://www.knowable.com/positivity/.+$".r
    println(regex2.findFirstIn("https://www.knowable.com/positivity/hello"))
    val regex3 = "^https?:\\/\\/www\\.knowable\\.com\\/positivity\\/.+$".r
    println(regex3.findFirstIn("https://www.knowable.com/positivity/hello"))

    // 2.3) unapply()
    val LogLineFormat = """(.+)\t(type\d+)""".r
    val LogLineFormat(ip, ctype) = "140.112.23.4\ttype5"
    println(ip)  // 140.112.23.4
    print(ctype) // type5
  }
}
