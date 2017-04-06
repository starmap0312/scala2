// Either:
// 1) it represents a value of one of two possible types (a disjoint union)
// 2) an instance of Either is either an instance of Left or Right
// example:
//   an alternative to Option for dealing with possible missing values
//   i.e. None is replaced with a Left which contains some useful information
//          ex. error logs (not just Null object representing nothing)
//        Some is replaced with a Right which encapsulates a value (object)
// Option vs. Try vs. Either vs. List
//   Option[Int]        : None                             / Some(3)       ...... error handling by using Null objects
//   List               : Nil                              / List(1, 2, 3) ...... representing non-deterministic values
//   Try[Int]           : Failure(Exception("some error")) / Success(3)    ...... error handling by encapsulating exceptions
//   Either[String, Int]: Left("not an Int")               / Right(3)      ...... representing non-deterministic values
//  ex. put error information  in a Left  replacing None or Failure
//      put encapsulated value in a Right replacing Some or Success
//  ex.
//    val result: Either[String, Int] = {
//      try {                             // if num can be converted into an Int
//        Right(num.toInt)                //   wrap the converted value in a Right (representing Success/Some)
//      } catch {                         // if num cannot be converted into an Int
//        case e: Exception => Left(num)  //   wrap the original value in a Left   (representing Failure/None)
//      }
//    }
// Either is right-biased
// 1) if it is a Left , operations like map(), flatMap(), return the Left value unchanged
// 2) if it is a Right, it is assumed to be the default case to operate on
// ex. Left (1).map(_ * 2)                // Left (1): return Left unchanged
//     Right(1).map(_ * 2)                // Right(2): apply map to its value

object MyEither {

  def cond[X, Y](test: Boolean, right: => Y, left: => X): MyEither[X, Y] = {
    if (test) MyRight(right) else MyLeft(left)
  }

}

abstract class MyEither[+A, +B] {

  // returns true if this is a Left, false otherwise, i.e isEmpty() in Option
  def isLeft: Boolean

  // returns true if this is a Right, false otherwise
  def isRight: Boolean

  // returns the value if this is a Left
  // otherwise, throws java.util.NoSuchElementException
  def get: B = this match {
    case MyRight(b)  => b
    case MyLeft(_) => throw new NoSuchElementException("Either.get on Left")
  }

  // returns the value if this is a Right. Otherwise, returns the default argument if this is a Left
  def getOrElse[BB >: B](default: => BB): BB = this match {
    case MyRight(b) => b
    case MyLeft(_)  => default
  }

  // executes the given side-effecting function $f if this is a Right
  // ex. Right(12).foreach(x => println(x))      // prints 12 if it is a Right
  // ex. Left(12).foreach(x => println(x))       // doesn't print if it is a Left
  def foreach[U](f: B => U): Unit = this match {
    case MyRight(b) => f(b)
    case MyLeft(_)  =>
  }

  // the given function is applied if this is a Right
  def map[Y](f: B => Y): MyEither[A, Y] = this match {
    case MyRight(b) => MyRight(f(b))
    case MyLeft(a)  => this.asInstanceOf[MyEither[A, Y]]
  }

  // binds the given function across Right
  def flatMap[AA >: A, Y](f: B => MyEither[AA, Y]): MyEither[AA, Y] = this match {
    case MyRight(b) => f(b)
    case MyLeft(a)  => this.asInstanceOf[MyEither[AA, Y]]
  }

  // returns None if this is a Left or if the given predicate $p does not hold for the left value
  // otherwise, returns a Right
  def filter[X](p: B => Boolean): MyOption[MyEither[X, B]] = this match {
    case MyRight(b) => if(p(b)) MySome(MyRight(b)) else MyNone
    case MyLeft(_)  => MyNone
  }

  // returns a Some containing the Right value if it exists or
  // returns a None if this is a Left (dropping the additional information)
  def toOption: MyOption[B] = this match {
    case MyRight(b) => MySome(b)
    case MyLeft(_)  => MyNone
  }

  def toTry(implicit ev: A <:< Throwable): MyTry[B] = this match {
    case MyRight(b) => MySuccess(b)
    case MyLeft(a)  => MyFailure(a)
  }
}

final case class MyLeft[+A, +B](value: A) extends MyEither[A, B] {
  def isLeft  = true
  def isRight = false
}

final case class MyRight[+A, +B](value: B) extends MyEither[A, B] {
  def isLeft  = false
  def isRight = true
}

object MyEithers {
  def main(args: Array[String]): Unit = {
    // 1) cond()
    val str1 = "123"
    val str2 = "12345"
    val right = MyEither.cond(str1.length < 5, str1, "a long input string")
    val left = MyEither.cond(str2.length < 5, str2, "a long input string")
    println(right)                               // MyRight(123)
    println(left)                                // MyLeft(a long input string)
    // 2) getOrElse()
    println(right.getOrElse("default"))          // 123
    println(left.getOrElse("default"))           // default
    // 3) foreach()
    right.foreach(x => println(x))               // 123: the side-effect function is applied
    left.foreach(x => println(x))                // does print: the side-effect function is not applied
    // 4) map()
    println(right.map(_ + "4"))                  // MyRight(1234)
    println(left.map(_ + "6"))                   // MyLeft(a long input string)
    // 5) flatMap()
    println(right.flatMap(x => MyLeft("error"))) // MyLeft("error")
    println(left.flatMap(x => MyLeft("error")))  // MyLeft(a long input string)
    // 6) filter()
    println(right.filter(_.length < 5))          // MySome(MyRight(123))
    println(left.filter(_.length < 5))           // MyNone
    // 7) toOption()
    println(right.toOption)                      // MySome(123)
    println(left.toOption)                       // MyNone
    // 8) toOption()
    val either3 = MyRight("123")
    val either4 = MyLeft(new Exception("an exception"))
    println(either3.toTry)                       // MySuccess(123)
    println(either4.toTry)                       // MyFailure(java.lang.Exception: an exception)
  }
}
