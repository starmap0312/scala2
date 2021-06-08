package design_pattern

// https://github.com/josephguan/scala-design-patterns/tree/master/behavioral/strategy
// Strategy lets the algorithm vary independently from clients that use it
//    clients shouldn't know about the data structures of the algorithm it uses
// Strategy provides a way to configure a class with one of many algorithms (behaviors)

object SortStrategy {
  type Type[U] = List[U] => List[U] // an alias to the type of a sorting function

  // instead of define a SortStrategy interface with two concrete classes: BubbleSortStrategy & QuickSortStrategy,
  // we define two functions (methods) and the client configure itself with one of the sorting functions
  def bubbleSortStrategy[U](dataset: List[U])(implicit ord: Ordering[U]): List[U] = {
    println("sorting using bubble sort")
    dataset.sorted
  }

  def quickSortStrategy[U](dataset: List[U])(implicit ord: Ordering[U]): List[U] = {
    println("sorting using quick sort")
    dataset.sorted
  }
}

class Sorter[T](strategy: SortStrategy.Type[T]) {
  def sort(dataset: List[T])(implicit ord: Ordering[T]): List[T] = {
    strategy(dataset) // use its algorithm (strategy) to sort the dataset
  }
}

object StrategyApp extends App {
  val sorter = new Sorter(SortStrategy.bubbleSortStrategy[Int])
  val sortedList = sorter.sort(List(5, 4, 3, 2, 1)) // sorting using bubble sort
  println(sortedList) // List(1, 2, 3, 4, 5)

}
