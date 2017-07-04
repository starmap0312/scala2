class MyCompanion(str: String) {
  def data = str
}

object MyCompanion {
  def apply(str: String) = new MyCompanion(str)
}