import java.io.FileInputStream

import resource._

object ManagedResourceTest {
  def main(args: Array[String]): Unit = {
    // managed([Resource]):
    //   ensure opening closing of resources within blocks of code using the managed method
    //   managed() creates a ManagedResource container for any type with a Resource type class implementation
    // 1) Imperative Style:
    val buffer = new Array[Byte](10)
    for(fileInputStream1 <- managed(new FileInputStream("/etc/passwd"))) {
      // Code that uses the input as a FileInputStream
      fileInputStream1.read(buffer)
    }
    println(buffer.mkString)

    // 2) Monadic style
    //    a monadic like container ManagedResource
    val managedResource = managed(new FileInputStream("/etc/passwd")) map {
      fileInputStream => {
        val buffer = new Array[Byte](10)
        fileInputStream.read(buffer)
        buffer
      }
    }
    println(managedResource.opt.get.mkString)
  }
}
