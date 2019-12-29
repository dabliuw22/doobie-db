package com.leysoft.infrastructure.doobie.util

import cats.~>
import cats.Monad
import cats.effect.{Async, ContextShift}
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.leysoft.infrastructure.doobie.config.DoobieConfiguration
import com.typesafe.scalalogging.Logger
import doobie.implicits._
import doobie.util.query.Query0
import doobie.util.update.Update0
import org.slf4j.LoggerFactory

trait ReactiveDoobieUtil[P[_], Q[_]] {

  def read[T](sqlStatement: Query0[T]): P[T]

  def readList[T](sqlStatement: Query0[T]): P[T]

  def write(sqlStatement: Update0): P[Int]
}

final case class SimpleReactiveDoobieUtil[P[_]: Monad, Q[_]: Async: ContextShift]()(implicit db: DoobieConfiguration[Q],
                                                                              converter: Q ~> P,
                                                                              factory: ReactiveFactory[P]) extends ReactiveDoobieUtil[P, Q] {

  private val looger = Logger(LoggerFactory.getLogger(SimpleReactiveDoobieUtil.getClass))

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

final case class HikariReactiveDoobieUtil[P[_]: Monad, Q[_]: Async: ContextShift]()(implicit db: DoobieConfiguration[Q],
                                                                                    converter: Q ~> P,
                                                                                    factory: ReactiveFactory[P]) extends ReactiveDoobieUtil[P, Q] {

  private val looger = Logger(LoggerFactory.getLogger(SimpleReactiveDoobieUtil.getClass))

  private val resource = db.hikariTransactor

  def read[T](sqlStatement: Query0[T]): P[T] = {
    looger.info(s"READ: ${sqlStatement.sql}")
    converter.apply(resource.use { hikari => sqlStatement.unique.transact(hikari) })
  }

  def readList[T](sqlStatement: Query0[T]): P[T] = {
    looger.info(s"READ_LIST: ${sqlStatement.sql}")
    converter.apply(resource.use { hikari => sqlStatement.stream.compile.toList.transact(hikari) })
      .flatMap { factory.list }
  }

  def write(sqlStatement: Update0): P[Int] = {
    looger.info(s"WRITE: ${sqlStatement.sql}")
    converter.apply(resource.use { hikari => sqlStatement.run.transact(hikari) })
      .flatMap { factory.zero }
  }
}