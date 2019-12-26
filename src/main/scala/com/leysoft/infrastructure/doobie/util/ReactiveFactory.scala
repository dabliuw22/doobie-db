package com.leysoft.infrastructure.doobie.util

import monix.reactive.Observable

trait ReactiveFactory[P[_]] {

  def list[A](list: List[A]): P[A]

  def zero(int: Int): P[Int]
}

object ReactiveFactory {

  def apply[P[_]](implicit factory: ReactiveFactory[P]): ReactiveFactory[P] = factory

  implicit val toObservable: ReactiveFactory[Observable] = new ReactiveFactory[Observable] {

    override def list[A](fa: List[A]): Observable[A] = Observable.fromIterable(fa)

    override def zero(int: Int): Observable[Int] = Observable.apply(int).filter { _ > 0 }
  }
}
