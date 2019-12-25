package com.leysoft.domain

trait UserRepository[P[_]] {

  def findAll: P[List[User]]

  def findBy(id: Long): P[User]

  def save(user: User): P[User]
}
