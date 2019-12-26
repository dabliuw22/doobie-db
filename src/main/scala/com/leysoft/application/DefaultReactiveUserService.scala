package com.leysoft.application
import cats.Monad
import com.leysoft.domain.{User, UserRepository}
import com.leysoft.util.{NaturalTransformations, ReactiveFactory}

final case class DefaultReactiveUserService[P[_]: Monad, T[_]](userRepository: UserRepository[T])(implicit val converter: NaturalTransformations[P, T], val factory: ReactiveFactory[P]) extends ReactiveUserService[P, T] {
  import cats.syntax.flatMap._

  override def get(id: Long): P[User] = converter.apply(userRepository.findBy(id))

  override def all: P[User] = converter.apply(userRepository.findAll)
    .flatMap { list => factory.apply(list) }

  override def create(user: User): P[User] = converter.apply(userRepository.save(user))
}
