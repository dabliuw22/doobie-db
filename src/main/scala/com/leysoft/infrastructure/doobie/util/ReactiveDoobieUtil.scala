package com.leysoft.infrastructure.doobie.util

import cats.Monad
import cats.effect.{Async, ContextShift}
import com.leysoft.infrastructure.doobie.config.DoobieConfiguration
import com.typesafe.scalalogging.Logger
import doobie.util.query.Query0
import doobie.util.update.Update0
import org.slf4j.LoggerFactory

final case class ReactiveDoobieUtil[P[_]: Monad, Q[_]: Async: ContextShift]()(implicit db: DoobieConfiguration[Q],
                                                                              converter: NaturalTransformations[P, Q],
                                                                              factory: ReactiveFactory[P]) {
  import doobie.implicits._
  import cats.syntax.flatMap._
  import cats.syntax.functor._

  private val looger = Logger(LoggerFactory.getLogger(ReactiveDoobieUtil.getClass))

  def read[T](sqlStatement: Query0[T]): P[T] = {
    looger.info(s"READ: ${sqlStatement.sql}")
    converter.apply(sqlStatement.unique.transact(db.transactor))
  }

  def readList[T](sqlStatement: Query0[T]): P[T] = {
    looger.info(s"READ_LIST: ${sqlStatement.sql}")
    converter.apply(sqlStatement.stream.compile.toList.transact(db.transactor))
      .flatMap { factory.list }
  }

  def write(sqlStatement: Update0): P[Int] = {
    looger.info(s"WRITE: ${sqlStatement.sql}")
    converter.apply(sqlStatement.run.transact(db.transactor))
      .flatMap { factory.zero }
  }
}
