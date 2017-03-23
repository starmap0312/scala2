object MyOption {             // static methods are defined here (also known as a companion object)
  // An MyOption factory which creates MySome(x) if the argument is not null, and MyNone if it is null
  def apply[A](value: A): MyOption[A] = if (value == null) MyNone else MySome(value)
}

abstract class MyOption[+A] { // most common functions to be inherited by subclasses are defined here
  // returns true if the option is $none, false otherwise.
  // implemented in concrete classes: MySome/MyNone
  def isEmpty: Boolean

  // returns the option's value if the option is nonempty, throws exception otherwise (unwraps the option's value)
  // implemented in concrete classes: MySome/MyNone
  def get: A

  // returns the option's value if the option is nonempty, otherwise return the result of evaluating "default"
  // the default can be of a supertype of A, ex. A is String and B is Any
  final def getOrElse[B >: A](default: => B): B = if (isEmpty) default else this.get

  // returns a $some containing the result of applying $f to this $option's value if this $option is nonempty
  // Otherwise return $none
  // f: A => B, the function maps value of type A to any type B
  final def map[B](f: A => B): MyOption[B] = if (isEmpty) MyNone else MySome(f(this.get))

  // returns the result of applying $f to this $option's value if this $option is nonempty.
  // Otherwise return $none
  // Slightly different from map() in that $f is expected to return an $option (which could be $none)
  final def flatMap[B](f: A => MyOption[B]): MyOption[B] = if (isEmpty) MyNone else f(this.get)

  // returns this $option if it is nonempty '''and''' applying the predicate $p to this $option's value returns true
  // Otherwise, return $none.
  final def filter(f: A => Boolean): MyOption[A] = if (isEmpty || f(this.get)) this else MyNone
}

final case class MySome[+A](value: A) extends MyOption[A] {
  def isEmpty = false
  def get = value
}

case object MyNone extends MyOption[Nothing] {
  def isEmpty = true
  def get = throw new NoSuchElementException("MyNone.get")
}

object MyOptions {
  def main(args: Array[String]): Unit = {
    println(MyOption(1).map((x: Int) => x * 2))      // MySome(2)
    println(                                         // MyNone
      MyOption(1).flatMap(
        (x: Int) => {
          if (x % 2 == 0) MySome(x)
          else MyNone
        }
      )
    )

    // since we define the class MyOption as covariant: [+A]
    //   MyOption[T’] is a subclass of MyOption[T] for T' subclassing T
    //   ex. it is OK to up-cast MyOption[Int] to MyOption[Any], but not the other way around
    val option1: MyOption[Any] = MyOption[Int](2)
    //val option2: MyOption[Int] = MyOption[Any](2)  // type mismatch
    println(option1)                                 // MySome(2)
  }
}
