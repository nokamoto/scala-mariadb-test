package model

import scalikejdbc._

case class Person(
  id: Int,
  name: String) {

  def save()(implicit session: DBSession): Person = Person.save(this)(session)

  def destroy()(implicit session: DBSession): Unit = Person.destroy(this)(session)

}


object Person extends SQLSyntaxSupport[Person] {

  override val schemaName = Some("flyway")

  override val tableName = "person"

  override val columns = Seq("id", "name")

  def apply(p: SyntaxProvider[Person])(rs: WrappedResultSet): Person = apply(p.resultName)(rs)
  def apply(p: ResultName[Person])(rs: WrappedResultSet): Person = new Person(
    id = rs.get(p.id),
    name = rs.get(p.name)
  )

  val p = Person.syntax("p")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession): Option[Person] = {
    withSQL {
      select.from(Person as p).where.eq(p.id, id)
    }.map(Person(p.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession): List[Person] = {
    withSQL(select.from(Person as p)).map(Person(p.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession): Long = {
    withSQL(select(sqls.count).from(Person as p)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession): Option[Person] = {
    withSQL {
      select.from(Person as p).where.append(where)
    }.map(Person(p.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession): List[Person] = {
    withSQL {
      select.from(Person as p).where.append(where)
    }.map(Person(p.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession): Long = {
    withSQL {
      select(sqls.count).from(Person as p).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    name: String)(implicit session: DBSession): Person = {
    val generatedKey = withSQL {
      insert.into(Person).columns(
        column.name
      ).values(
        name
      )
    }.updateAndReturnGeneratedKey.apply()

    Person(
      id = generatedKey.toInt,
      name = name)
  }

  def batchInsert(entities: Seq[Person])(implicit session: DBSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'name -> entity.name))
        SQL("""insert into person(
        name
      ) values (
        {name}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: Person)(implicit session: DBSession): Person = {
    withSQL {
      update(Person).set(
        column.id -> entity.id,
        column.name -> entity.name
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Person)(implicit session: DBSession): Unit = {
    withSQL { delete.from(Person).where.eq(column.id, entity.id) }.update.apply()
  }

}
