package com.leysoft.infrastructure.doobie.util

import cats.effect.{Async, ContextShift}
import com.leysoft.infrastructure.doobie.config.DoobieConfiguration
import doobie.free.connection.ConnectionIO

final case class DoobieUtil[P[_]: Async: ContextShift]()(implicit db: DoobieConfiguration[P]) {
  import doobie.implicits._

  private val transactor = db.transactor

  def execute[T](sqlStatement: ConnectionIO[T]): P[T] = sqlStatement.transact(transactor)
}
