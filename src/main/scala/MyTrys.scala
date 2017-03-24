// 1) MyTry type represents a computation that may either result in an exception, or return a successfully computed value
//    it's similar to, but semantically different from the Either type
// 2) it is used to pipeline or chain operations,
//    a) catching exceptions along the way
//    b) the flatMap() and map() combinators essentially either
//       pass off their successfully completed value, wrapped in the MySuccess subtype for further operation in the chain, or
//       wrap the exception in the MyFailure type to be passed on down the chain
//    c) Combinators such as recover and recoverWith are designed to provide some type of default behavior in the case of failure

object MyTry {
  def apply[T](expr: => T): MyTry[T] = { // this method uses the by-name parameter
    try {
      MySuccess(expr)
    } catch {                          // try to catch any exception thrown and wrap it in a MyFailure object
      case e: Throwable => MyFailure(e)
    }
  }
}
// a syntactic sugar to create an MyTry instance
// ex. val try = MyTry({...})

abstract class MyTry[+T] {
  // the abstract superclass

  // returns true if the MyTry is a  MyFailure, false otherwise
  def isFailure: Boolean

  // returns true if the MyTry is a  MySuccess, false otherwise
  def isSuccess: Boolean

  // returns the value if this is a MySuccess or throws the exception if this is a MyFailure
  def get: T

  // returns the value if this is a MySuccess or the given default argument if this is a MyFailure
  def getOrElse[U >: T](default: => U): U

  // maps the given function to the value if this is a MySuccess or returns this if this is a MyFailure
  def map[U](f: T => U): MyTry[U]

  // returns the given function applied to the value if this is a MySuccess or returns this if this is a MyFailure
  def flatMap[U](f: T => MyTry[U]): MyTry[U]

  // Converts this to a MyFailure if the predicate $f is not satisfied
  def filter(f: T => Boolean): MyTry[T]

  // applies the given function $pf if this is a MyFailure, otherwise returns this if this is a MySuccess
  // i.e. the map() for the exception
  def recover[U >: T](pf: PartialFunction[Throwable, U]): MyTry[U]

  // applies the given function $pf if this is a MyFailure, otherwise returns this if this is a MySuccess
  // i.e. the flatMap() for the exception
  def recoverWith[U >: T](pf: PartialFunction[Throwable, MyTry[U]]): MyTry[U]

  // returns None if this is a MyFailure or a Some containing the value if this is a MySuccess
  def toOption: Option[T]
}

final case class MyFailure[+T](exception: Throwable) extends MyTry[T] {
  // a concrete subclass

  def isFailure: Boolean = true
  def isSuccess: Boolean = false
  def get: T = throw exception
  def getOrElse[U >: T](default: => U): U = default
  def map[U](f: T => U): MyTry[U] = {
    this.asInstanceOf[MyTry[U]]
  }
  def flatMap[U](f: T => MyTry[U]): MyTry[U] = {
    this.asInstanceOf[MyTry[U]]
  }
  def filter(p: T => Boolean): MyTry[T] = {
    this
  }
  def recover[U >: T](pf: PartialFunction[Throwable, U]): MyTry[U] = {            // the map() for the exception
    try {
      if (pf.isDefinedAt(exception)) {        // the exception got a chance to be mapped to a MySuccess value if it is defined at pf
        MySuccess(pf(exception))
      } else {
        this
      }
    } catch {
      case e: Throwable => MyFailure(e)
    }
  }
  def recoverWith[U >: T](pf: PartialFunction[Throwable, MyTry[U]]): MyTry[U] = { // the flatMap() for the exception
    try {
      if (pf.isDefinedAt(exception)) {
        pf(exception)
      } else {
        this
      }
    } catch {
      case e: Throwable => MyFailure(e)
    }
  }
  def toOption: Option[T] = None
}

final case class MySuccess[+T](value: T) extends MyTry[T] {
  def isFailure: Boolean = false
  def isSuccess: Boolean = true
  def get = value
  def getOrElse[U >: T](default: => U): U = get
  def map[U](f: T => U): MyTry[U] = {
    MyTry[U](f(value))
  }
  def flatMap[U](f: T => MyTry[U]): MyTry[U] = {
    try {
      f(value)
    } catch {
      case e: Throwable => MyFailure(e)
    }
  }
  def filter(p: T => Boolean): MyTry[T] = {
    try {
      if (p(value)) {
        this
      } else {
        MyFailure(new NoSuchElementException("Predicate does not hold for " + value))
      }
    } catch {
      case e: Throwable => MyFailure(e)
    }
  }
  def recover[U >: T](pf: PartialFunction[Throwable, U]): MyTry[U] = {            // the map() for the exception
    this
  }
  def recoverWith[U >: T](pf: PartialFunction[Throwable, MyTry[U]]): MyTry[U] = { // the flatMap() for the exception
    this
  }
  def toOption: Option[T] = Some(value)
}

object MyTrys {
  def main(args: Array[String]): Unit = {
    val mytry1 = MyTry(
      throw new Exception("Boom!") // wrap any exception thrown and wrap it in a MyFailure(exception) instance
    )
    val mytry2 = MyTry(            // wrap any value evaluated in a MySuccess(3) instance
      1 + 2
    )
    val mytry3 = mytry2.flatMap(
      (x: Int) => MyTry(x * 2)     // this computes a new value 6 which is wrapped in MySuccess(6)
    ).flatMap(
      (x: Int) => MyTry(x / 0)     // this throws an ArithmeticException which is wrapped in MyFailure(ArithmeticException)
    )
    println(mytry1)
    println(mytry2)
    println(mytry3)
  }
}
