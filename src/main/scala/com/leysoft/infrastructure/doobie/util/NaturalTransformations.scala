package com.leysoft.infrastructure.doobie.util

import cats.~>
import monix.eval.Task
import monix.reactive.Observable

import scala.concurrent.Future

object NaturalTransformations {

  implicit val taskToObservable: Task ~> Observable = new (Task ~> Observable) {
    override def apply[A](fa: Task[A]): Observable[A] = Observable.fromTask(fa)
  }

  implicit val futureToObservable: Future ~> Observable = new (Future ~> Observable) {
    override def apply[A](fa: Future[A]): Observable[A] = Observable.fromFuture(fa)
  }
}