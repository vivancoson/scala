package slick

import scala.slick.driver.H2Driver.simple._

object Store {
  private val db = Database.forURL("jdbc:h2:mem:slick", driver = "org.h2.Driver")
  // WARNING: Old session slick. New dyna session fails on ddl.create, though. Must use ddl script.
  private implicit var session = db.createSession()
  private val users = TableQuery[Users]
  private val tasks = TableQuery[Tasks]

  def open() = {
    (users.ddl ++ tasks.ddl).create
  }

  def close() = {
    (users.ddl ++ tasks.ddl).drop
    session.close()
  }

  def createUser(user: User): User = {
    session.withTransaction {
      users.insert(user)
    }
    user
  }

  def createTask(user: User, task: String): Task = {
    val created = Task(0, user.name, task)
    session.withTransaction {
      tasks.insert(created)
    }
    created
  }

  def deleteTask(id: Int): Boolean = {
    session.withTransaction {
      val deleted: Int = tasks.filter(_.id === id).delete
      if (deleted == 1) true else false
    }
  }

  def getUsers: List[User] = {
    users.list
  }

  def getUserByName(name: String): Option[User] = {
    users.filter(_.name === name).firstOption
  }

  def getUserTasksByName(name: String): Map[User, List[Task]] = {
    val query = for {
      (u, t) <- users leftJoin tasks on(_.name === _.userName)
    } yield (u, t)
    val list: List[(User, Task)] = query.list
    val key: User = list.head._1
    val values: List[Task] = list.map(_._2).toList
    val map: Map[User, List[Task]] = Map(key -> values)
    map
  }
}