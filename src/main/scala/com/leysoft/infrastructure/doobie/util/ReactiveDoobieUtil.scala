package com.leysoft.infrastructure.doobie.util

import cats.Monad
import cats.effect.{Async, ContextShift}
import com.leysoft.infrastructure.doobie.config.DoobieConfiguration
import doobie.free.connection.ConnectionIO
import doobie.util.query.Query0
import doobie.util.update.Update0

final case class ReactiveDoobieUtil[P[_]: Monad, Q[_]: Async: ContextShift]()(implicit db: DoobieConfiguration[Q],
                                                                              converter: NaturalTransformations[P, Q],
                                                                              factory: ReactiveFactory[P]) {
  import doobie.implicits._
  import cats.syntax.flatMap._
  import cats.syntax.functor._
  private val transactor = db.transactor

  def read[T](sqlStatement: Query0[T]): P[T] = converter.apply(sqlStatement.unique.transact(transactor))

  def readList[T](sqlStatement: Query0[T]): P[T] = converter.apply(sqlStatement.stream.compile.toList.transact(transactor))
    .flatMap { factory.list }

  def write(sqlStatement: Update0): P[Int] = converter.apply(sqlStatement.run.transact(transactor))
    .flatMap { factory.zero }
}
