package gardening.fruits

case class Fruit(name: String, color: String)

// define two Fruit objects in a package gardening.fruits
object apple extends Fruit("Apple", "green")
object banana extends Fruit("Banana", "yellow")

