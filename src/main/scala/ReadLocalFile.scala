import scala.io.Source
import java.net.URL
import java.io.File
import sys.process._
import scala.language.postfixOps

object ReadLocalFile {
  def main(args: Array[String]): Unit = {
    // 1) scala.io.Source.fromFile([filepath]):
    //    it returns BufferedSource
    //    scala can be omitted as it is in the scope already
    val filepath = "README.md" // use absolute path or relative path of current working folder
    val bufferedSource = Source.fromFile(filepath)
    // 2) source.mkString & source.closed()
    //    read the file source as a String & close the file afterwards
    val lines1 = try bufferedSource.mkString finally bufferedSource.close()
    println(lines1) // scala2
    // 3) source.getLines():
    //    in case of large files, get an iterator and read the lines one by one
    val lines2 = Source.fromFile(filepath).getLines().mkString(sep="\n")
    println(lines2) // scala2

    // 4) scala.io.Source.fromURL([url])
    val source = Source.fromURL("http://example.com")
    println(source.mkString) // <!doctype html> ... </html>

    // 5) Download the contents of a URL to a file
    // def #> (f: File): ProcessBuilder = toFile(f, append = false)
    // def !!: String  (starts the process represented by this builder, blocks until it exits)
    val file = new File("/tmp/example.html")
    new URL("http://example.com") #> file !!
  }
}
