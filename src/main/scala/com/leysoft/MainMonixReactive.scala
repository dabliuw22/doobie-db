package com.leysoft

import akka.actor.ActorSystem
import cats.effect.ContextShift
import com.leysoft.application.DefaultReactiveUserService
import com.leysoft.domain.User
import com.leysoft.infrastructure.doobie.DoobieReactiveUserRepository
import com.leysoft.infrastructure.doobie.config.DoobieConfiguration
import monix.eval.Task
import monix.execution.{Ack, Scheduler}
import monix.reactive.Observable

import scala.concurrent.Future

object MainMonixReactive extends App {
  val system = ActorSystem("doobie-monix-reactive-system")
  implicit val scheduler: Scheduler = Scheduler.computation()
  implicit val cs: ContextShift[Task] = Task.contextShift(scheduler)
  implicit val db: DoobieConfiguration[Task] = DoobieConfiguration[Task]
  val userRepository = DoobieReactiveUserRepository[Observable, Task]()
  val userService = DefaultReactiveUserService[Observable, Task](userRepository)
  val errorHandler: Throwable => Observable[User] = _ => Observable.empty
  /*
  val newUser = userService.create(User(200, "username200"))
    .subscribe { newUser =>
      println(s"New User: $newUser")
      Future { Ack.Stop }
    }
  */
  val usersTask = userService.all
    .onErrorHandleWith { errorHandler }
    .subscribe { users =>
      println(s"Users: $users")
      Future { Ack.Continue }
    }
  val userTask = userService.get(1)
    .onErrorHandleWith { errorHandler }
    .subscribe { user =>
      println(s"User: $user")
      Future { Ack.Stop }
    }
}
