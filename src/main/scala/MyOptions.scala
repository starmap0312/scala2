object MyOption {
  // static methods are defined here (also known as a companion object)

  // An MyOption factory which creates MySome(x) if the argument is not null, and MyNone if it is null
  def apply[A](value: A): MyOption[A] = {
    if (value == null) {
      MyNone
    } else {
      MySome(value)
    }
  }
}
// apply() provides a syntactic sugar, so we can write:
//   ex. val option = MyOption(null) = MyOption.apply(null) = MyNone
//   ex. val option = MyOption(2)    = MyOption.apply(2)    = MySome(2)

abstract class MyOption[+A] {
  // this superclass defines most common functions to be inherited by subclasses

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
  final def map[B](f: A => B): MyOption[B] = {
    if (isEmpty) {
      MyNone
    } else {
      MySome(f(this.get))
    }
  }

  // returns the result of applying $f to this $option's value if this $option is nonempty.
  // Otherwise return $none
  // Slightly different from map() in that $f is expected to return an $option (which could be $none)
  final def flatMap[B](f: A => MyOption[B]): MyOption[B] = {
    if (isEmpty) { // flatMap takes care of the case when MyNone calls it
      MyNone       //   it returns MyNone directly without applying function f
    } else {       // when MySome calls it
      f(this.get)  //   it applies function f to its encapsulated value and returns it
    }
  }

  // returns this $option if it is nonempty '''and''' applying the predicate $p to this $option's value returns true
  // Otherwise, return $none.
  final def filter(f: A => Boolean): MyOption[A] = if (isEmpty || f(this.get)) this else MyNone
}

// class MySome encapsulates value (a public, immutable field, whose value specified by constructor)
final case class MySome[+A](value: A) extends MyOption[A] {
  def isEmpty = false
  def get = value
}
// case class MySome[+A](value: A) is a syntactic sugar for:
//
// object MySome {
//   def apply(value: A) = new MySome(value)
// }
// class MySome[+A](value: A) { ... }
//
// moreover, apply() is a syntactic sugar for creating an instance, so we can write:
//   ex. val option = MySome(2) = MySome.apply(2) = new MySome(2)

case object MyNone extends MyOption[Nothing] {
  def isEmpty = true
  def get = throw new NoSuchElementException("MyNone.get")
}
// this defines a singleton MyNone object, so we can write:
//   ex. val option = MyNone
// the case keyword allows MyNone able to be matched as a case in pattern matching
//   ex. case MyNone => { ... }

object MyOptions {
  def main(args: Array[String]): Unit = {
    val option  = MyOption(null)
    val option0 = MyNone
    val option1 = MyOption(1)
    val option2: MyOption[Any] = MyOption[Int](2)
    // since we define the class MyOption as covariant: [+A]
    //   MyOption[T’] is now viewed as a subclass of MyOption[T] for any T' subclassing T
    //   so it is OK to up-cast MyOption[Int] to MyOption[Any], or
    //   pass an MyOption[Int] instance as parameter to function that takes MyOption[Any]
    // but we get compile error if we did not define the class as covariant [+A]
    //   then MyOption[Any] and MyOption[Int] has no relationship
    // or if we try to cast the other way around
    //   ex. val option2: MyOption[Int] = MyOption[Any](2)  // type mismatch
    println(option)                                  // MyNone
    println(option0)                                 // MyNone
    println(option1)                                 // MySome(1)
    println(option2)                                 // MySome(2)

    // 1) option.map()
    println(option0.map((x: Int) => x * 2))          // MyNone: we can MyNone.map() works as well
    println(option1.map((x: Int) => x * 2))          // MySome(2)
    // 2) option.flatMap()
    val option3 = option1.flatMap(
      (x: Int) => {
        if (x % 2 == 0) MySome(x)
        else MyNone
      }
    )
    println(option3)                                 // MyNone
  }
}
