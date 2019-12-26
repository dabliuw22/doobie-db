package com.leysoft

import akka.actor.ActorSystem
import cats.effect.ContextShift
import com.leysoft.application.DefaultReactiveUserService
import com.leysoft.domain.User
import com.leysoft.infrastructure.doobie.DoobieReactiveUserRepository
import com.leysoft.infrastructure.doobie.config.DoobieConfiguration
import com.leysoft.infrastructure.doobie.util.ReactiveDoobieUtil
import com.typesafe.scalalogging.Logger
import monix.eval.Task
import monix.execution.{Ack, Scheduler}
import monix.reactive.Observable
import org.slf4j.LoggerFactory

import scala.concurrent.Future

object MainMonixReactive extends App {
  val logger = Logger(LoggerFactory.getLogger(MainMonixReactive.getClass))
  val system = ActorSystem("doobie-monix-reactive-system")
  implicit val scheduler: Scheduler = Scheduler.computation()
  implicit val cs: ContextShift[Task] = Task.contextShift(scheduler)
  implicit val db: DoobieConfiguration[Task] = DoobieConfiguration[Task]
  implicit val dbUtil: ReactiveDoobieUtil[Observable, Task] = ReactiveDoobieUtil[Observable, Task]
  val userRepository = DoobieReactiveUserRepository[Observable, Task]()
  val userService = DefaultReactiveUserService[Observable, Task](userRepository)
  val errorHandler: Throwable => Observable[User] = _ => Observable.empty

  val newUser = userService.create(User(16, "username16"))
    .doOnError { error => Task(logger.error(s"Error: ${error.getMessage}")) }
    .onErrorHandleWith { errorHandler }
    .subscribe { newUser =>
      logger.info(s"New User: $newUser")
      Future { Ack.Stop }
    }

  val usersTask = userService.all
    .onErrorHandleWith { errorHandler }
    .subscribe { users =>
      logger.info(s"Users: $users")
      Future { Ack.Continue }
    }

  val userTask = userService.get(1)
    .onErrorHandleWith { errorHandler }
    .subscribe { user =>
      logger.info(s"User: $user")
      Future { Ack.Stop }
    }
}
