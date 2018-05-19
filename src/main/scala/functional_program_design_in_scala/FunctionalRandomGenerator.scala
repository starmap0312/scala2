package functional_program_design_in_scala

import java.util.Random

trait Generator[+T] { self => // make self an alias to this
  def generate: T

  def map[S](f: T => S): Generator[S] = new Generator[S] {
    override def generate = f(self.generate) // alternative: f(Generator.this.generate)
    // note: self refers to the this of the outer Generator
    //       otherwise, if we write this.generate, it means the this of the inner Generator
  }

  def flatMap[S](f: T => Generator[S]): Generator[S] = new Generator[S] {
    override def generate = f(self.generate).generate
  }

}

object FunctionalRandomGenerator extends App {
  // the for-expression is NOT tied to collection
  // it can be used as long as some interpretation of flatMap, withFilter, map are defined
  // example: an implementation of integers generator
  //          it works like an infinite collection of integers
  def single[T](x: T) = new Generator[T] {
    override def generate = x
  }

  def integers = new Generator[Int] {
    val rand = new Random
    override def generate = rand.nextInt
  }
  println(integers.generate) // ex. 1374390074

  // an implementation of pairs generator
  val pairs1 = new Generator[(Int, Int)] {
    override def generate = (integers.generate, integers.generate)
  }
  println(pairs1.generate)   // ex. (-1057362160,-866800771)

  // in order to streamlining Generator, we need to define the flatMap, withFilter, map methods
  def booleans1 = for (x <- integers) yield x > 0
  val booleans2 = integers map { x => x > 0 }
  val booleans3 = new Generator[Boolean] {
    override def generate  = ((x: Int) => x > 0)(integers.generate)
  }
  val booleans4 = new Generator[Boolean] {
    override def generate = (integers.generate > 0)
  }
  println(booleans1.generate)   // ex. false
  println(booleans2.generate)   // ex. false
  println(booleans3.generate)   // ex. false
  println(booleans4.generate)   // ex. false

  // as flatMap and map methods are defined, you can use for-notation to get an Generator[Int, Int] instance
  val pairs2 = for {
    x <- integers
    y <- integers
  } yield (x, y)
  val pairs3 = integers flatMap {
    x => integers map {
      y => (x, y)
    }
  }
  println(pairs2.generate)   // ex. (-1246479723,2011046313)
  println(pairs3.generate)   // ex. (1414437408,-936093373)

  // define a method to create pair generator, i.e. Generator[(T, U)]
  def createPairs1[T, U](t: Generator[T], u: Generator[U]): Generator[(T, U)] = t flatMap {
    x => u map {
      y => (x, y)
    }
  }
  def createPairs2[T, U](t: Generator[T], u: Generator[U]): Generator[(T, U)] = for {
    x <- t
    y <- u
  } yield (x, y)
  val pairs4 = createPairs1(integers, integers)
  val pairs5 = createPairs1(integers, booleans1)
  val pairs6 = createPairs2(integers, integers)
  val pairs7 = createPairs2(integers, booleans1)
  println(pairs4.generate)   // ex. (-1019290664,833513234)
  println(pairs5.generate)   // ex. (-84139608,true)
  println(pairs6.generate)   // ex. (-1019290664,833513234)
  println(pairs7.generate)   // ex. (-84139608,true)

  def createIntegersWithRange(low: Int, high: Int) = for (x <- integers) yield {
    low + Math.abs(x) % (high - low)
  }
  val integersWithRange = createIntegersWithRange(1, 10)
  println(integersWithRange.generate)   // ex. 8

  def oneOf[T](xs: T*): Generator[T] = for {
    idx <- createIntegersWithRange(0, xs.length)
  } yield xs(idx)
  val colors = oneOf("red", "blue", "green")
  println(colors.generate)   // ex. red

  // example: a list generator
  def lists: Generator[List[Int]] = for {
    isEmpty <- booleans1
    list <- if (isEmpty) emptyLists else nonEmptyLists
  } yield list
  def emptyLists = single(Nil)
  def nonEmptyLists = for {
    head <- integers
    tail <- lists
  } yield head :: tail
  println(lists.generate) // ex. List(1073049293, -600148441, -208121030)
}
