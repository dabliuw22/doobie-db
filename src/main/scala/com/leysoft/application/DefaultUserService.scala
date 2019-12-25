package com.leysoft.application

import com.leysoft.domain.{User, UserRepository}

final case class DefaultUserService[P[_]](userRepository: UserRepository[P]) extends UserService[P] {

  override def get(id: Long): P[User] = userRepository.findBy(id)

  override def all: P[List[User]] = userRepository.findAll

  override def create(user: User): P[User] = userRepository.save(user)
}
