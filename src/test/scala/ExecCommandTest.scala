import sys.process._

object ExecCommandTest {
  def main(args: Array[String]): Unit = {
    // 1) ! and !!
    // def !  : Int    // returns the the exit code as an Int
    // def !! : String // returns the stdout as a String
    val rc = ("ls -al" !)      // ... drwxrwxr-x+ 65 root  admin  2210 Feb 21 01:14 Applications ...
    println(rc)                //
    val stdout = ("ls -al" !!) // ... drwxrwxr-x+ 65 root  admin  2210 Feb 21 01:14 Applications ...
    println(stdout)
  }
}
