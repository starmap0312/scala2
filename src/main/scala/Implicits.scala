import scala.collection.immutable.StringOps

object Implicits {
  def main(args: Array[String]): Unit = {
    // 1) Implicit Conversion:
    //    in Predef.scala, there is an implicit method defined for the conversion of String to StringOps
    //    implicit def augmentString(x: String): StringOps = new StringOps(x)
    // String does not have map() method, but we can still write the following
    println("abc".map(_.toInt))                  // Vector(97, 98, 99)
    // the above is converted to the following automatically
    println((new StringOps("abc")).map(_.toInt)) // Vector(97, 98, 99)
  }
}
