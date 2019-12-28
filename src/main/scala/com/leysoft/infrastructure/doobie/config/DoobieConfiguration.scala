package com.leysoft.infrastructure.doobie.config

import cats.effect.{Async, ContextShift}
import doobie.Transactor
import doobie.util.transactor.Transactor.Aux

final case class DoobieConfiguration[P[_]: Async]()(implicit cf: ContextShift[P]) {

  val transactor: Aux[P, Unit] = Transactor.fromDriverManager[P](
    driver = "org.postgresql.Driver",
    url = "jdbc:postgresql://localhost:5432/doobie_db",
    user = "doobie",
    pass = "doobie"
  )
}
