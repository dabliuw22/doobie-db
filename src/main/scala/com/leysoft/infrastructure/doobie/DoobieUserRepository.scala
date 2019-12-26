package com.leysoft.infrastructure.doobie

import cats.Monad
import doobie.implicits._
import com.leysoft.domain.{User, UserRepository}
import com.leysoft.infrastructure.doobie.util.DoobieUtil

final case class DoobieUserRepository[P[_]: Monad]()(implicit db: DoobieUtil[P]) extends UserRepository[P] {
  import cats.syntax.functor._
  import cats.syntax.flatMap._

  override def findAll: P[List[User]] = db.execute(sql"SELECT * FROM users".query[User].stream.compile.toList)

  override def findBy(id: Long): P[User] = db.execute(sql"SELECT * FROM users WHERE id = $id".query[User].unique)

  override def save(user: User): P[User] = db.execute(sql"INSERT INTO users VALUES(${user.id}, ${user.name})".update.run)
    .map { _ => user }
}
