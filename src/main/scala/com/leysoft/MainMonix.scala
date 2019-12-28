package com.leysoft

import akka.actor.ActorSystem
import cats.effect.ContextShift
import com.leysoft.application.DefaultUserService
import com.leysoft.domain.User
import com.leysoft.infrastructure.doobie.DoobieUserRepository
import com.leysoft.infrastructure.doobie.config.DoobieConfiguration
import com.leysoft.infrastructure.doobie.util.DoobieUtil
import com.typesafe.scalalogging.Logger
import monix.eval.Task
import monix.execution.{Ack, Scheduler}
import monix.reactive.Observable
import org.slf4j.LoggerFactory

import scala.concurrent.Future

object MainMonix extends App {
  val logger = Logger(LoggerFactory.getLogger(MainMonix.getClass))
  val system = ActorSystem("doobie-monix-system")
  implicit val scheduler: Scheduler = Scheduler.computation()
  implicit val cs: ContextShift[Task] = Task.contextShift(scheduler)
  implicit val db: DoobieConfiguration[Task] = DoobieConfiguration[Task]
  implicit val dbUtil: DoobieUtil[Task] = DoobieUtil[Task]
  val userRepository = DoobieUserRepository[Task]
  val userService = DefaultUserService[Task](userRepository)
  //val newUser = userService.create(User(200, "username200")).runSyncUnsafe()
  //println(s"New User: $newUser")
  val usersTask = userService.all.onErrorFallbackTo(Task { List() })
  Observable.fromTask(usersTask)
    .subscribe { users =>
      logger.info(s"Users: $users")
      Future(Ack.Continue)
    }
  val userTask = userService.get(1)
  Observable.fromTask(userTask)
    .onErrorHandleWith { _ => Observable.empty }
      .subscribe { user =>
        logger.info(s"User: $user")
        Future(Ack.Stop)
      }
}
