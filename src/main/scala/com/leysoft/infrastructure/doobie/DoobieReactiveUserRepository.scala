package com.leysoft.infrastructure.doobie

import cats.Monad
import com.leysoft.domain.{ReactiveUserRepository, User}
import com.leysoft.infrastructure.doobie.util.ReactiveDoobieUtil

final case class DoobieReactiveUserRepository[P[_]: Monad, Q[_]]()(implicit db: ReactiveDoobieUtil[P, Q]) extends ReactiveUserRepository[P, Q] {
  import cats.syntax.functor._
  import cats.syntax.flatMap._
  import doobie.implicits._

  override def findAll: P[User] = db.readList(sql"SELECT * FROM users".query[User])

  override def findBy(id: Long): P[User] = db.read(sql"SELECT * FROM users WHERE id = $id".query[User])

  override def save(user: User): P[User] = db.write(sql"INSERT INTO users VALUES(${user.id}, ${user.name})".update)
    .map { _ => user }

  override def delete(id: Long): P[Unit] = db.write(sql"DELETE FROM users WHERE id = $id".update)
    .map(_ => ())
}
