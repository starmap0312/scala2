// https://alvinalexander.com/scala/scala-traits-mixins-constructor-order-overridden-methods/

// example 1
trait A { println("trait A is constructed") }
trait B { println("trait B is constructed") }
trait C { println("trait C is constructed") }

// example 2
trait Base {
  def hello = {
    println("hello from Base")
  }
}
trait A2 extends Base {
  override def hello  = {
    super.hello
    println("hello from A2")
  }
}
trait B2 extends Base {
  override def hello  = {
    super.hello
    println("hello from B2")
  }
}
trait C2 extends Base {
  override def hello = {
    super.hello
    println("hello from C2")
  }
}

// example 3
trait A3 extends Base {
  override def hello  = {
    println("hello from A3")
    super.hello
  }
}
trait B3 extends Base {
  override def hello  = {
    println("hello from B3")
    super.hello
  }
}
trait C3 extends Base {
  override def hello = {
    println("hello from C3")
    super.hello
  }
}

object TraitsMixins extends App {
  // example 1
  class D extends A with B with C {
    println("class D is constructed")
  }
  val d = new D
  // trait A is constructed
  // trait B is constructed
  // trait C is constructed
  // class D is constructed

  println

  // example 2
  class D2 extends A2 with B2 with C2 {
    override def hello = {
      super.hello
      println("hello from D2")
    }
  }
  val d2 = new D2
  d2.hello
  // hello from Base
  // hello from A2
  // hello from B2
  // hello from C2
  // hello from D2

  println

  // example 3
  class D3 extends A3 with B3 with C3 {
    override def hello = {
      println("hello from D3")
      super.hello
    }
  }
  val d3 = new D3
  d3.hello
  // hello from D3
  // hello from C3
  // hello from B3
  // hello from A3
  // hello from Base

}
