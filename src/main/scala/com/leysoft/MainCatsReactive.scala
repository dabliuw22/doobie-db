package com.leysoft

import akka.actor.ActorSystem
import cats.effect.{ContextShift, IO}
import com.leysoft.application.DefaultReactiveUserService
import com.leysoft.domain.User
import com.leysoft.infrastructure.doobie.DoobieReactiveUserRepository
import com.leysoft.infrastructure.doobie.config.DoobieConfiguration
import com.leysoft.infrastructure.doobie.util.{ReactiveDoobieUtil, SimpleReactiveDoobieUtil}
import com.typesafe.scalalogging.Logger
import monix.eval.Task
import monix.execution.{Ack, Scheduler}
import monix.reactive.Observable
import org.slf4j.LoggerFactory

import scala.concurrent.Future

object MainCatsReactive extends App {
  val logger = Logger(LoggerFactory.getLogger(MainMonixReactive.getClass))
  val system = ActorSystem("doobie-monix-reactive-system")
  import com.leysoft.infrastructure.doobie.util.NaturalTransformations._ // for (IO ~> Observable) instance
  implicit val scheduler: Scheduler = Scheduler.computation()
  implicit val cs: ContextShift[IO] = IO.contextShift(system.dispatcher)
  implicit val db: DoobieConfiguration[IO] = DoobieConfiguration[IO]
  implicit val dbUtil: ReactiveDoobieUtil[Observable, IO] = SimpleReactiveDoobieUtil[Observable, IO]
  val userRepository = DoobieReactiveUserRepository[Observable, IO]
  val userService = DefaultReactiveUserService[Observable, IO](userRepository)
  val errorHandler: Throwable => Observable[User] = _ => Observable.empty

  userService.all.executeAsync
    .doOnError { error => Task(logger.error(s"Error: ${error.getMessage}")) }
    .onErrorHandleWith { errorHandler }
    .subscribe { users =>
      logger.info(s"Users: $users")
      Future(Ack.Continue)
    }
  userService.get(1)
    .doOnError { error => Task(logger.error(s"Error: ${error.getMessage}")) }
    .onErrorHandleWith { errorHandler }
    .subscribe { user =>
      logger.info(s"User: $user")
      Future(Ack.Stop)
    }
}
