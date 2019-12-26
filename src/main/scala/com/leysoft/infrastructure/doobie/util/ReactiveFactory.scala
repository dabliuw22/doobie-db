package com.leysoft.infrastructure.doobie.util

import monix.reactive.Observable

trait ReactiveFactory[P[_]] {

  def apply[A](list: List[A]): P[A]
}

object ReactiveFactory {

  def apply[P[_]](implicit factory: ReactiveFactory[P]): ReactiveFactory[P] = factory

  implicit val listToOnservable: ReactiveFactory[Observable] = new ReactiveFactory[Observable] {
    override def apply[A](fa: List[A]): Observable[A] = Observable.fromIterable(fa)
  }
}
