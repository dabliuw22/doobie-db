package com.leysoft.application
import cats.Monad
import com.leysoft.domain.{ReactiveUserRepository, User}

final case class DefaultReactiveUserService[P[_]: Monad, Q[_]](userRepository: ReactiveUserRepository[P, Q]) extends ReactiveUserService[P, Q] {

  override def get(id: Long): P[User] = userRepository.findBy(id)

  override def all: P[User] = userRepository.findAll

  override def create(user: User): P[User] = userRepository.save(user)

  override def remove(id: Long): P[Unit] = userRepository.delete(id)
}
