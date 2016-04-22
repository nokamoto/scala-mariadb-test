import model.Person
import org.flywaydb.core.Flyway
import scalikejdbc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.util.Random

object Main {
  val name = 'default

  def add(name: String)(implicit session: DBSession): Future[Person] = {
    Future(blocking(Person.create(name)))
  }

  def get(id: Int)(implicit session: DBSession): Future[Option[Person]] = {
    Future(blocking(Person.find(id)))
  }

  def delete(id: Int)(implicit session: DBSession): Future[Int] = {
    Future(blocking(withSQL(deleteFrom(Person).where.eq(Person.column.id, id)).update().apply()))
  }

  implicit class Sync[A](future: Future[A]) {
    def sync(implicit c: ExecutionContext): A = Await.result(future, Duration.Inf)
  }

  def main(args: Array[String]): Unit = {
    val flyway = new Flyway
    val url = s"""jdbc:mariadb://${sys.props.getOrElse("hosts", "localhost")}"""
    val user = sys.props.getOrElse("username", "root")
    val password = sys.props.getOrElse("password", "root")
    val schema = sys.props.getOrElse("schema", "flyway")
    flyway.setDataSource(url, user, password)
    flyway.setSchemas(schema)

    println(s"$flyway: url=$url, user=$user, password=$password, schema=$schema")

    sys.addShutdownHook {
      flyway.clean()
      ConnectionPool.close(name)
    }

    println(s"flyway migrate: ${flyway.migrate()}")

    ConnectionPool.add(name = name, url = s"$url/$schema", user = user, password = password)

    while (true) {
      val who = Random.alphanumeric.take(10).mkString

      val person = NamedDB(name).futureLocalTx(add(who)(_)).sync
      println(s"$person")

      val found = NamedDB(name).futureLocalTx(get(person.id)(_)).sync
      require(found.contains(person))

      val deleted = NamedDB(name).futureLocalTx(delete(person.id)(_)).sync
      require(deleted == 1)

      Thread.sleep(1000)
    }
  }
}
