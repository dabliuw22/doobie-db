package com.leysoft

import akka.actor.ActorSystem
import cats.effect.{ContextShift, IO}
import com.leysoft.application.DefaultUserService
import com.leysoft.domain.User
import com.leysoft.infrastructure.doobie.DoobieUserRepository
import com.leysoft.infrastructure.doobie.config.DoobieConfiguration
import com.leysoft.infrastructure.doobie.util.DoobieUtil
import com.typesafe.scalalogging.Logger
import monix.eval.Task
import monix.execution.Scheduler
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext

object MainCats extends App {
  val logger = Logger(LoggerFactory.getLogger(MainCats.getClass))
  val system = ActorSystem("doobie-cats-system")
  val i = Task.contextShift(Scheduler.global)
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val db: DoobieConfiguration[IO] = DoobieConfiguration[IO]
  implicit val dbUtil: DoobieUtil[IO] = DoobieUtil[IO]
  val userRepository = DoobieUserRepository[IO]()
  val userService = DefaultUserService[IO](userRepository)
  // val newUser = userService.create(User(100, "username100")).unsafeRunSync()
  // println(s"New User: $newUser")
  val users = userService.all.unsafeRunAsync {
    case Right(users) => logger.info(s"Users: $users")
    case Left(error) => logger.error(s"Error: $error")
  }
  val user = userService.get(1).unsafeRunSync()
  logger.info(s"User: $user")
}
