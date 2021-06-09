import java.io.FileInputStream

import scala.util.Using

object ManagedResourceTest {
  def main(args: Array[String]): Unit = {
    // managed([Resource]):
    //   ensure opening closing of resources within blocks of code using the managed method
    //   managed() creates a ManagedResource container for any type with a Resource type class implementation
    // 1) Imperative Style:
    val buffer = new Array[Byte](10)
    Using.resource(new FileInputStream("/etc/passwd")) { fileInputStream1 =>
      // Code that uses the input as a FileInputStream
      fileInputStream1.read(buffer)
    }
    println(buffer.mkString)

    // 2) Monadic style
    //    a monadic like container ManagedResource
    val managedResource: Array[Byte] = Using.resource(new FileInputStream("/etc/passwd")) {
      fileInputStream => {
        val buffer = new Array[Byte](10)
        fileInputStream.read(buffer)
        buffer
      }
    }
    println(managedResource.mkString)
  }
}
