package com.leysoft.infrastructure.doobie.util

import cats.~>
import monix.eval.Task
import monix.reactive.Observable

import scala.concurrent.Future

trait NaturalTransformations[P[_], T[_]] extends (T ~> P) {

  def apply[A](fa: T[A]): P[A]
}

object NaturalTransformations {

  def apply[P[_], T[_]](implicit converter: NaturalTransformations[P, T]) = converter

  implicit val taskToObservable: NaturalTransformations[Observable, Task] = new NaturalTransformations[Observable, Task] {
    override def apply[A](fa: Task[A]): Observable[A] = Observable.fromTask(fa)
  }

  implicit val futureToObservable: NaturalTransformations[Observable, Future] = new NaturalTransformations[Observable, Future] {
    override def apply[A](fa: Future[A]): Observable[A] = Observable.fromFuture(fa)
  }
}