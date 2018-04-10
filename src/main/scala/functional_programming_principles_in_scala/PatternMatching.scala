package functional_programming_principles_in_scala

// 1) pattern-matching decomposition:
//    main logic are centralized in one place, ex. superclass or another class
//    adding a new operation requires implementation in the superclass or another class
trait Expr {
  def eval: Int = this match {
    case Num(n)             => n
    case Sum(leftOp, rightOp)  => leftOp.eval + rightOp.eval
    case Prod(leftOp, rightOp) => leftOp.eval * rightOp.eval                  // adding a new subtype
  }
  def show: String = this match {                                             // adding a new operation method
    case Num(n)             => n.toString
    case Sum(leftOp, rightOp)  => s"(${leftOp.show} + ${rightOp.show})"
    case Prod(leftOp, rightOp) => s"(${leftOp.show} * ${rightOp.show})"       // adding a new subtype
  }
  def simplify: Int = this match {
    case Num(n)             => n
    case Sum(Prod(Num(n1), e1), Prod(Num(n2), e2)) if (n1 == n2) => {
      print((Prod(Num(n1), Sum(e1, e2))).show + " = ")
      (Prod(Num(n1), Sum(e1, e2))).simplify
    }
    case Sum(leftOp, rightOp)  => {
      print(Sum(leftOp, rightOp).show + " = ")
      leftOp.eval + rightOp.eval
    }
    case Prod(leftOp, rightOp) => leftOp.eval * rightOp.eval                  // adding a new subtype
  }
}
case class Num(n: Int) extends Expr
case class Sum(leftOp: Expr, rightOp: Expr) extends Expr
case class Prod(leftOp: Expr, rightOp: Expr) extends Expr                     // adding a new subtype

// 2) oop decomposition:
//    main logic are implemented in subclasses
//    adding a new operation requires implementation in all subclasses
trait Expression {
  def eval: Int
  def show: String
  def simplify: Int
}
case class Number(n: Int) extends Expression {
  override def eval: Int = n
  override def show: String = n.toString
  override def simplify: Int = n
}
case class Product(leftOp: Expression, rightOp: Expression) extends Expression {
  override def eval: Int = leftOp.eval * rightOp.eval
  override def show: String = s"(${leftOp.show} * ${rightOp.show})"
  override def simplify: Int = leftOp.eval * rightOp.eval
}
case class Summarize(leftOp: Expression, rightOp: Expression) extends Expression {
  override def eval: Int = leftOp.eval + rightOp.eval
  override def show: String = s"(${leftOp.show} + ${rightOp.show})"
  override def simplify: Int = this match {
    case Summarize(Product(Number(n1), e1), Product(Number(n2), e2)) if (n1 == n2) => {
      print(Product(Number(n1), Summarize(e1, e2)).show + " = ")
      Product(Number(n1), Summarize(e1, e2)).simplify
    }
    case Summarize(leftOp, rightOp) => {
      print(Summarize(leftOp, rightOp).show + " = ")
      leftOp.eval + rightOp.eval
    }
  }
}

object PatternMatching extends App {
  // 1) pattern matching decomposition
  val expr1 = Sum(Prod(Num(2), Num(3)), Prod(Num(2), Num(4)))
  val expr2 = Sum(Prod(Num(3), Num(2)), Prod(Num(2), Num(4)))
  println(expr1.eval)                                                     // 14
  println(expr1.show)                                                     // (2 * 3 + 2 * 4)
  println(expr1.simplify)                                                 // 2 * (3 + 4) = 14

  println(expr2.eval)                                                     // 14
  println(expr2.show)                                                     // (3 * 2 + 2 * 4)
  println(expr2.simplify)                                                 // (3 * 2 + 2 * 4) = 14

  // 2) oop decomposition
  val expr3 = Summarize(Product(Number(2), Number(3)), Product(Number(2), Number(4)))
  val expr4 = Summarize(Product(Number(3), Number(2)), Product(Number(2), Number(4)))
  println(expr3.eval)                                                     // 14
  println(expr3.show)                                                     // (2 * 3 + 2 * 4)
  println(expr3.simplify)                                                 // 2 * (3 + 4) = 14

  println(expr4.eval)                                                     // 14
  println(expr4.show)                                                     // (3 * 2 + 2 * 4)
  println(expr4.simplify)                                                 // (3 * 2 + 2 * 4) = 14
}
