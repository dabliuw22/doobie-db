package com.leysoft.domain

trait ReactiveUserRepository[P[_], Q[_]] {

  def findAll: P[User]

  def findBy(id: Long): P[User]

  def save(user: User): P[User]
}
