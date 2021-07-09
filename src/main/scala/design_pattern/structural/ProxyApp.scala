package design_pattern.structural

// https://github.com/josephguan/scala-design-patterns/tree/master/structural/proxy
// the pattern provides a surrogate or placeholder for another object to control access to it
// the pattern is the same as the decorator pattern and it is used in specific scenarios
//   ex. a remote proxy that is responsible for encoding a request and its arguments and for sending the encoded request to the real subject in a different address space
//       i.e. a local representative for a remote object
//   ex. a virtual proxy that caches additional information about the real subject so that they can postpone accessing it
//       i.e. it creates expensive objects only on demand, for example, a image proxy for the real images
//   ex. a protection proxy that checks if the caller has the access permissions required to perform a request
//       i.e. it useful when the real objects need different access rights
//   ex. a smart reference (replacement) that performs additional actions when the real object is accessed
//       for example, counting the number of references to the real object so that it can be freed automatically when there are no more references
//       for example, loading a persistent object into memory when it is first referenced
//       for example, checking if the real object is locked before it is accessed to ensure that no other object can change it

// subject interface (decoratee interface)
trait Computer {
  def run(command: String): String
}

// concrete subject (concrete decoratees)
class LinuxComputer extends Computer {
  override def run(command: String): String = s"running '$command' in linux"
}

// proxy (which contains a concrete subject & acts like a subject) (decorator)
//   it maintains a reference to a real subject and provides an interface identical to the real subject so that a proxy can by substituted for it
//   it controls access to the real subject and may be responsible for creating and deleting it
class SecurityShell(realComputer: Computer) extends Computer {
  override def run(command: String): String = {
    if (command == "shutdown") "shutdown is prohibited"
    else realComputer.run(command)
  }
}

object ProxyApp extends App {
  val computer = new LinuxComputer()
  val ssh = new SecurityShell(computer)
  println(ssh.run("shutdown")) // shutdown is prohibited
  println(ssh.run("cd /root")) // running 'cd /root' in linux
}
