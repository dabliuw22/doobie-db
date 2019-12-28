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
  import com.leysoft.infrastructure.doobie.util.NaturalTransformations._ // for (Task ~> Observable) instance
  implicit val scheduler: Scheduler = Scheduler.computation()
  implicit val cs: ContextShift[Task] = Task.contextShift(Scheduler.io())
  implicit val db: DoobieConfiguration[Task] = DoobieConfiguration[Task]
  implicit val dbUtil: ReactiveDoobieUtil[Observable, Task] = ReactiveDoobieUtil[Observable, Task]
  val userRepository = DoobieReactiveUserRepository[Observable, Task]
  val userService = DefaultReactiveUserService[Observable, Task](userRepository)
  val errorHandler: Throwable => Observable[User] = _ => Observable.empty

  userService.create(User(16, "username16"))
    .doOnError { error => Task(logger.error(s"Error: ${error.getMessage}")) }
    .onErrorHandleWith { errorHandler }
    .subscribe { newUser =>
      logger.info(s"New User: $newUser")
      Future(Ack.Stop)
    }

  userService.remove(16)
    .doOnError { error => Task(logger.error(s"Error: ${error.getMessage}")) }
    .onErrorHandleWith { errorHandler }
    .subscribe { _ =>
      logger.info("Deleted...")
      Future(Ack.Stop)
    }

  userService.all.executeAsync
    .doOnError { error => Task(logger.error(s"Error: ${error.getMessage}")) }
    .onErrorHandleWith { errorHandler }
    .observeOn(Scheduler.io())
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

  Observable.zip2(
    userService.get(1).executeOn(Scheduler.computation())
      .map { user1 => logger.info(s"User1: $user1"); user1 },
    userService.get(2).executeOn(Scheduler.io())
      .map { user2 => logger.info(s"User2: $user2"); user2 })
    .map(tuple => tuple._1.id match {
      case 1 => tuple._1
      case 2 => tuple._2
    }).subscribe()
}
