package com.leysoft.infrastructure.doobie

import cats.Monad
import com.leysoft.domain.{ReactiveUserRepository, User}
import com.leysoft.infrastructure.doobie.config.DoobieConfiguration
import com.leysoft.infrastructure.doobie.util.{NaturalTransformations, ReactiveFactory}

final case class DoobieReactiveUserRepository[P[_]: Monad, T[_]]()(implicit db: DoobieConfiguration[T],
                                                                          converter: NaturalTransformations[P, T],
                                                                          factory: ReactiveFactory[P]) extends ReactiveUserRepository[P, T] {
  import cats.syntax.functor._
  import cats.syntax.flatMap._
  import doobie.implicits._

  override def findAll: P[User] = converter
    .apply(db.execute(sql"SELECT * FROM users".query[User].stream.compile.toList))
    .flatMap { factory.apply }

  override def findBy(id: Long): P[User] = converter
    .apply(db.execute(sql"SELECT * FROM users WHERE id = $id".query[User].unique))

  override def save(user: User): P[User] = converter
    .apply(db.execute(sql"INSERT INTO users VALUES(${user.id}, ${user.name})".update.run))
    .map { _ => user }
}
