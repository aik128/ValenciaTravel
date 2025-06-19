package com.example.valenciatravel.data.mapper

import com.example.valenciatravel.data.local.entity.UserEntity
import com.example.valenciatravel.domain.model.User

fun UserEntity.toDomain(): User = User(
    id = id,
    login = login,
    password = password
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    login = login,
    password = password
)