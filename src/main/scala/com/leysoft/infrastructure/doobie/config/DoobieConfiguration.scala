package com.leysoft.infrastructure.doobie.config

import cats.effect.{Async, Blocker, ContextShift, Resource}
import doobie.Transactor
import doobie.hikari._
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor.Aux

final case class DoobieConfiguration[P[_]: Async]()(implicit cf: ContextShift[P]) {

  private val driver = "org.postgresql.Driver"

  private val url = "jdbc:postgresql://localhost:5432/doobie_db"

  private val user = "doobie"

  private val password = "doobie"

  lazy val transactor: Aux[P, Unit] = Transactor.fromDriverManager[P](
    driver = driver,
    url = url,
    user = user,
    pass = password
  )

  lazy val hikariTransactor: Resource[P, HikariTransactor[P]] = for {
    context <- ExecutionContexts.fixedThreadPool[P](10)
    blocker <- Blocker[P]
    hikari <- HikariTransactor.newHikariTransactor[P](
      driverClassName = driver,
      url = url,
      user = user,
      pass = password,
      connectEC = context,
      blocker = blocker
    )
  } yield hikari
}
