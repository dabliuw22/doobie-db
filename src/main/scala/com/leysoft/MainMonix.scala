package com.leysoft

import akka.actor.ActorSystem
import cats.effect.ContextShift
import com.leysoft.application.DefaultUserService
import com.leysoft.domain.User
import com.leysoft.infrastructure.doobie.DoobieUserRepository
import com.leysoft.infrastructure.doobie.config.DoobieConfiguration
import monix.eval.Task
import monix.execution.{Ack, Scheduler}
import monix.reactive.Observable

import scala.concurrent.Future

object MainMonix extends App {
  val system = ActorSystem("doobie-monix-system")
  implicit val scheduler: Scheduler = Scheduler.computation()
  implicit val cs: ContextShift[Task] = Task.contextShift(scheduler)
  implicit val db: DoobieConfiguration[Task] = DoobieConfiguration[Task]
  val userRepository = DoobieUserRepository[Task]()
  val userService = DefaultUserService[Task](userRepository)
  //val newUser = userService.create(User(200, "username200")).runSyncUnsafe()
  //println(s"New User: $newUser")
  val usersTask = userService.all.onErrorFallbackTo(Task { List() })
  Observable.fromTask(usersTask)
    .subscribe { users =>
      println(s"Users: $users")
      Future { Ack.Continue }
    }
  val userTask = userService.get(1)
  Observable.fromTask(userTask)
    .onErrorHandleWith { _ => Observable.empty }
      .subscribe { user =>
        println(s"User: $user")
        Future { Ack.Stop }
      }
}
