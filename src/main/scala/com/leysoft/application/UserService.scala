package com.leysoft.application

import com.leysoft.domain.User

trait UserService[P[_]] {

  def get(id: Long): P[User]

  def all: P[List[User]]

  def create(user: User): P[User]
}
