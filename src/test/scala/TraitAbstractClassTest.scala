import scala.collection.mutable.ArrayBuffer

// Ref: https://docs.scala-lang.org/tour/self-types.html
//
// 1) trait
// 1.1) traits are similar to Java 8 interfaces
//      they are used to share interfaces and fields between classes
// 1.2) traits allow multiple inheritance
// 1.3) traits cannot have constructor parameters
//      this restriction is to avoid issues introduced by multiple inheritance
// 1.4) trait methods can be partially implemented
//      but they can only be directly used by Java if all methods remain unimplemented
// example1:
trait Iterator[A] {
  def hasNext: Boolean
  def next(): A
}

class IntIterator(upperbound: Int) extends Iterator[Int] {
  private var current = 0
  override def hasNext: Boolean = current < upperbound
  override def next(): Int =  {
    if (hasNext) {
      val t = current
      current += 1
      t
    } else 0
  }
}

// example2:
trait Animal {
  val name: String
}
class Cat(val name: String) extends Animal
class Dog(val name: String) extends Animal
// trait Animal has an abstract field name which gets implemented in subclasses constructors

// 2) abstract class
//    two main reasons to use an abstract class in Scala
//    i) you need a base class that requires constructor arguments
//   ii) the code need to be called from Java
// example1:
abstract class Animal2(x: String) {
  def name: String = x
}
class Cat2(x: String) extends Animal2(x)
class Dog2(x: String) extends Animal2(x)

// example2:
trait Buffer[T] {
  val element: T              // no implementation
}
abstract class SeqBuffer[T] extends Buffer[Seq[T]] {
  def length = element.length // partially implemented
}

object TraitAbstractClassTest {
  def main(args: Array[String]): Unit = {
    // 1) traits
    // example1:
    val iterator = new IntIterator(10)
    println(iterator.next())  // 0
    println(iterator.next())  // 1

    // example2:
    val dog = new Dog("Harry")
    val cat = new Cat("Sally")
    val animals = ArrayBuffer.empty[Animal]
    animals.append(dog)
    animals.append(cat)
    animals.foreach(animal => println(animal.name))  // Harry Sally

    // 2) abstract class
    // example1:
    val dog2 = new Dog2("Harry")
    val cat2 = new Cat2("Sally")
    val animals2 = ArrayBuffer.empty[Animal2]
    animals2.append(dog2)
    animals2.append(cat2)
    animals2.foreach(animal => println(animal.name))  // Harry Sally

    // example2:
    // traits with type members are often used in combination with anonymous class instantiations
    def newIntSeqBuf(e1: Int, e2: Int): SeqBuffer[Int] = {
      new SeqBuffer[Int] { // a concrete anonymous class which needs to implement all methods
        val element = List(e1, e2)
      }
    }
    val buf = newIntSeqBuf(7, 8)
    println(buf.length)  // 2
    println(buf.element) // List(7, 8)
  }
}
