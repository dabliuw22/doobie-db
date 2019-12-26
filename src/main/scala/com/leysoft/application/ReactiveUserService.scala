package com.leysoft.application

import com.leysoft.domain.User

trait ReactiveUserService[P[_], T[_]] {

  def get(id: Long): P[User]

  def all: P[User]

  def create(user: User): P[User]
}
