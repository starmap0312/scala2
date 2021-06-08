package design_pattern

// https://github.com/josephguan/scala-design-patterns/tree/master/behavioral/strategy
// Strategy lets the algorithm vary independently from clients that use it
//    clients shouldn't know about the data structures of the algorithm it uses
// Strategy provides a way to configure a class with one of many algorithms (behaviors)

// strategy interface
//   instead of defining  two concrete classes, we define two functions
object SortStrategy {
  // in functional programing, a strategy is actually a function
  type Type[U] = List[U] => List[U]

  // concrete strategies
  def bubbleSortStrategy[U](dataset: List[U])(implicit ord: Ordering[U]): List[U] = {
    println("sorting using bubble sort")
    dataset.sorted
  }

  def quickSortStrategy[U](dataset: List[U])(implicit ord: Ordering[U]): List[U] = {
    println("sorting using quick sort")
    dataset.sorted
  }
}

// client
//   the client configure itself with one of the sorting functions
class Sorter[T](strategy: SortStrategy.Type[T]) {

  def sort(dataset: List[T])(implicit ord: Ordering[T]): List[T] = {
    strategy(dataset) // apply the algorithm (strategy) to the dataset
  }
}

object StrategyApp extends App {
  val sorter = new Sorter(SortStrategy.bubbleSortStrategy[Int])
  val sortedList = sorter.sort(List(5, 4, 3, 2, 1)) // sorting using bubble sort
  println(sortedList) // List(1, 2, 3, 4, 5)

}
