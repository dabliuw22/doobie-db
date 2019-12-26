package com.leysoft.domain

trait ReactiveUserRepository[P[_], T[_]] {

  def findAll: P[User]

  def findBy(id: Long): P[User]

  def save(user: User): P[User]
}
