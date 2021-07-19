package design_pattern.other

import scala.collection.mutable

// https://github.com/josephguan/scala-design-patterns/tree/master/persistence/data-access-object
// the pattern allows you to separate low level data accessing operations (in-memory or database operations) from high level business services
//   it provides an abstract interface for the data accessing (insert, update, findById, etc.)

// model object
//   it is a simple cass class (POJO) containing get/set methods to store data retrieved using DAO class
case class Student(id: Int, name: String, age: Int)

trait DaoBase[T] {

  def insert(obj: T): Unit
  def update(obj: T): Unit
  def findById(id: Int): Option[T]
}

// Dao interface
//   it defines the operations to be performed on a model object
trait StudentDao extends DaoBase[Student]

// concrete Dao classes
//   it implements above the operations to be performed on a model object
//   it is responsible to get data (model objects) from a data source which can be in-memory, database, xml or other storage mechanism
class InMemoryStudentDao extends StudentDao {

  val students = mutable.HashMap[Int, Student]() // store the model objects in a hashmap (in-memory)

  override def insert(obj: Student): Unit = {
    if (students.get(obj.id).isEmpty) {
      students.put(obj.id, obj)
    } else {
      throw new Exception("Record exists!")
    }
  }

  override def update(obj: Student): Unit = {
    students.update(obj.id, obj)
  }

  override def findById(id: Int): Option[Student] = {
    students.get(id)
  }

}

// other persistence data sources:
//   ex. class MysqlStudentDao extends StudentDao
//   ex. class PostgreStudentDao extends StudentDao

object DataAccessObjectApp extends App {
  val database = new InMemoryStudentDao()

  database.insert(Student(1, "abby", 11))
  database.insert(Student(2, "bobby", 12))

  println(database.findById(1).get) // Student(1,abby,11)
  println(database.findById(2).get) // Student(2,bobby,12)
  database.update(Student(2, "bobby", 15))
  println(database.findById(2).get) // Student(2,bobby,15)
}
