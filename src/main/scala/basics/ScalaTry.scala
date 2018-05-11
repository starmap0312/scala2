package basics

import scala.util.Try

object ScalaTry extends App {
  // 1) for-expression:
  val tryOp1 = for {
    x <- Try("success")
    y <- Try(throw new Exception)
  } yield {
    "value"
  }
  val tryOp2 = Try("success").flatMap {
    x => {
      Try(throw new Exception).map {
        y => "value"
      }
    }
  }
  println(tryOp1) // Failure(java.lang.Exception)
  println(tryOp2) // Failure(java.lang.Exception)

  val tryOp3 = for {
    x <- Try("success")
    y <- Try("success")
  } yield {
    throw new Exception
  }
  val tryOp4 = Try("success").flatMap {
    x => {
      Try("success").map {
        y => throw new Exception
      }
    }
  }
  println(tryOp3) // Failure(java.lang.Exception)
  println(tryOp4) // Failure(java.lang.Exception)

  // 1) for-expression with filter:
  val tryOp5 = for {
    x <- Try("success")
    y <- Try("filtered")
    if (y != "filtered")
  } yield {
    "value"
  }
  val tryOp6 = Try("success").flatMap {
    x => {
      Try("filtered").filter {
        y => y != "filtered"
      } map {
        y => "value"
      }
    }
  }
  println(tryOp5) // Failure(java.util.NoSuchElementException: Predicate does not hold for filtered)
  println(tryOp6) // Failure(java.util.NoSuchElementException: Predicate does not hold for filtered)

}
