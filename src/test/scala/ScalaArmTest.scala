import java.io.FileInputStream

import resource._

object ScalaArmTest {
  def main(args: Array[String]): Unit = {
    // managed([Resource]):
    //   ensure opening closing of resources within blocks of code using the managed method
    //   managed() creates a ManagedResource container for any type with a Resource type class implementation
    // 1) Imperative Style:
    val buffer = new Array[Byte](10)
    for(input <- managed(new FileInputStream("/etc/passwd"))) {
      // Code that uses the input as a FileInputStream
      input.read(buffer)
    }
    println(buffer.mkString)

    // 2) Monadic style
    //    a monadic like container ManagedResource
    val first_ten_bytes = managed(new FileInputStream("/etc/passwd")) map {
      input => {
        val buffer = new Array[Byte](10)
        input.read(buffer)
        buffer
      }
    }
    println(first_ten_bytes.opt.get.mkString)
  }
}
