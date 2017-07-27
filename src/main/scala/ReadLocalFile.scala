
object ReadLocalFile {
  def main(args: Array[String]): Unit = {
    // 1) scala.io.Source.fromFile([filepath]):
    //    it returns BufferedSource
    //    scala can be omitted as it is in the scope already
    val filepath = "README.md" // use absolute path or relative path of current working folder
    val bufferedSource = scala.io.Source.fromFile(filepath)
    // 2) source.mkString & source.closed()
    //    read the file source as a String & close the file afterwards
    val lines1 = try bufferedSource.mkString finally bufferedSource.close()
    println(lines1) // scala2
    // 3) source.getLines():
    //    in case of large files, get an iterator and read the lines one by one
    val lines2 = io.Source.fromFile(filepath).getLines().mkString(sep="\n")
    println(lines2) // scala2
  }
}
