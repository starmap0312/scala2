package design_pattern.other

import scala.collection.mutable

// https://github.com/josephguan/scala-design-patterns/tree/master/persistence/repository
// the pattern is similar to Data Access Object pattern but uses the repository term to make it easier to understand

// domain object (model object)
//   it defines objects managed by the repository
case class Account(id: Int, name: String, age: Int)

trait Repository[T, ID] {

  // add or update
  def save(entity: T): Unit
  def delete(entity: T): Unit
  def deleteById(id: ID): Unit
  def findById(id: ID): Option[T]
  def findAll(spec: Specification[T] = null): List[T]
}


// abstract repository (Dao interface)
//   it defines the standard operations of a repository (to be performed on a model object)
trait AccountRepository extends Repository[Account, Int]

// concrete repositories (concrete Dao implementations)
//   it is responsible to get data (model objects) from a data source which can be in-memory, database, xml or other storage mechanism
// 1) in-memory
class AccountRepositoryInMemory extends AccountRepository {

  val repository: mutable.HashMap[Int, Account] = new mutable.HashMap[Int, Account]()

  override def save(entity: Account): Unit = {
    // add or update
    repository.put(entity.id, entity)
  }

  override def delete(entity: Account): Unit = {
    repository.remove(entity.id)
  }

  override def deleteById(id: Int): Unit = {
    repository.remove(id)
  }

  override def findById(id: Int): Option[Account] = {
    repository.get(id)
  }

  override def findAll(spec: Specification[Account] = null): List[Account] = {
    if (spec == null) repository.values.toList
    else repository.values.filter(x => spec.specified(x)).toList
  }
}


// 2) in-database
class AccountRepositoryInDatabase extends AccountRepository {

  override def save(entity: Account): Unit = {
    // add or update
    val upsert =
      s"""INSERT INTO table_account (id, name, age) VALUES(${entity.id}, ${entity.name}, ${entity.age})
         |ON DUPLICATE KEY UPDATE name=${entity.name}, age=${entity.age}"""
    execute(upsert)
  }

  override def delete(entity: Account): Unit = {
    val del = s"""DELETE FROM table_account WHERE id = ${entity.id}"""
    execute(del)

  }

  override def deleteById(id: Int): Unit = {
    val del = s"""DELETE FROM table_account WHERE id = $id"""
    execute(del)
  }

  override def findById(id: Int): Option[Account] = {
    val sql = s"""SELECT id, name, age FROM table_account WHERE id = $id"""
    val result = query(sql)
    // convert query result to Account object
    Some(Account(id, "dummy", 0))
  }

  override def findAll(spec: Specification[Account] = null): List[Account] = {
    val sql = s"""SELECT id, name, age FROM table_account WHERE ${spec.toSqlClauses()}"""
    val result = query(sql)
    // convert query result to Account object
    List(Account(1, "dummy", 0))
  }

  // dummy query
  private def query(sql: String): List[List[String]] = Nil

  // dummy execute sql
  private def execute(sql: String): Unit = {}

}

trait Specification[T] {

  def specified(entity: T): Boolean
  def toSqlClauses(): String
}

// abstract specification
//   it defines operations that is able to tell if a candidate object matches some criteria
trait AccountSpecification extends Specification[Account]

object AccountSpecification {

  // concrete specifications
  //   it provides different kind of specifications
  class AgeBetweenSpec(from: Int, to: Int) extends AccountSpecification {
    override def specified(entity: Account): Boolean = {
      entity.age >= from && entity.age <= to
    }

    override def toSqlClauses(): String = {
      s"""age >= $from and age <= $to"""
    }
  }

  class NameEqualSpec(name: String) extends AccountSpecification {
    override def specified(entity: Account): Boolean = {
      entity.name.equalsIgnoreCase(name)
    }

    override def toSqlClauses(): String = {
      s"""name = $name"""
    }
  }

}

object RepositoryApp extends App {
  val repository = new AccountRepositoryInMemory()

  val abby = Account(1, "abby", 10)
  val bobby = Account(2, "bobby", 11)
  val cathy = Account(3, "cathy", 12)

  // add
  repository.save(abby)
  repository.save(bobby)
  repository.save(cathy)
  println(repository.findAll()) // List(Account(1,abby,10), Account(2,bobby,11), Account(3,cathy,12))

  // update
  val bobby2 = bobby.copy(age = 15)
  repository.save(bobby2)
  println(repository.findAll()) // List(Account(1,abby,10), Account(2,bobby,15), Account(3,cathy,12))

  // delete
  repository.delete(cathy)
  repository.deleteById(bobby2.id)
  println(repository.findAll()) // List(Account(1,abby,10))

  // restore
  repository.save(bobby)
  repository.save(cathy)

  // find by id
  println(repository.findById(2)) // Some(Account(2,bobby,11))

  // find by age
  println(repository.findAll(new AccountSpecification.AgeBetweenSpec(5,11))) // List(Account(1,abby,10), Account(2,bobby,11))

  // find by name
  println(repository.findAll(new AccountSpecification.NameEqualSpec("Bobby"))) // List(Account(2,bobby,11))
}
