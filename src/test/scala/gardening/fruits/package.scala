package gardening

package object fruits {
  val planted = List(apple, banana)
  def showFruit(fruit: Fruit) {
    println(fruit.name +"s are "+ fruit.color)
  }
}
