package functional_program_design_in_scala

case class Book(title: String, authors: List[String])

object QueriesWithFor extends App {
  // for-notation vs. database query-languages
  // 1) representation of a mini-database: books
  val books: List[Book] = List(
    Book(title = "Book1: W X", authors = List("Alice", "Bob")),
    Book(title = "Book2: X Y", authors = List("Bob",   "Cathy")),
    Book(title = "Book3: Y Z", authors = List("Cathy", "Don"))
  )
  // note: books might not be a List, but a database stored on a server
  //       as long as the books interface defines methods: flatMap, withFilter, map,
  //       we can use the for-notation for querying the database
  // 2) use for-notation to query book titles:
  //    ex. find the titles of books whose authors contain Bob
  //        SELECT title FROM books WHERE ("Bob" IN authors)
  val titles1 = for (book <- books if book.authors contains "Bob") yield book.title
  println(titles1) // List(Book1: W X, Book2: X Y)

  //    ex. find all the book titles that contain word Y
  //        SELECT title FROM books WHERE ("Y" IN title)
  val titles2 = for (book <- books if (book.title indexOf "Y") >= 0) yield book.title
  println(titles2) // List(Book2: X Y, Book3: Y Z)

  //    ex. find all the book authors in the database that have written at least 2 books
  val authors = for {
    b1 <- books
    b2 <- books
    if b1.title < b2.title // this is better than filter with (b1 != b2)
    a1 <- b1.authors
    a2 <- b2.authors
    if a1 == a2
  } yield a1
  println(authors.distinct) // List(Bob, Cathy)
  // use .distinct() to remove duplicates, or define Books as Set[Book]
}
