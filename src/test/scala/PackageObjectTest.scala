// 1) standalone objects:
//    before Scala 2.8, the only things you could put in a package were classes, traits, and standalone objects
// 2) package objects:
//    after Scala 2.8, any kind of definition that you can put inside a class can also be put at the top level of a package
//    if you have some helper method you'd like to be in scope for an entire package:
//     you can put it at the top level of the package (i.e. define a package object)

//import gardening.fruits._

object PackageObjectTest {
  def main(args: Array[String]): Unit = {
    // 1) standalone objects: ex. apple, banana
    println("%s: %s".format(gardening.fruits.apple.name, gardening.fruits.apple.color))   // Apple: green
    println("%s: %s".format(gardening.fruits.banana.name, gardening.fruits.banana.color)) // Banana: yellow

    // 2) package objects: ex. planted, showFruit()
    for (fruit: gardening.fruits.Fruit <- gardening.fruits.planted) {
      gardening.fruits.showFruit(fruit)
    } // Apples are green
      // Bananas are yellow
  }
}
