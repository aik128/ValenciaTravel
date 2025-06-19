package com.example.valenciatravel.data.mapper

import com.example.valenciatravel.data.local.entity.PreferencesEntity
import com.example.valenciatravel.domain.model.Preferences

fun PreferencesEntity.toDomain(): Preferences = Preferences(
    userId = userId,
    c1 = c1,
    c2 = c2,
    c3 = c3,
    c4 = c4,
    c5 = c5,
    c6 = c6,
    c7 = c7,
    c8 = c8,
    c9 = c9,
    c10 = c10
)

fun Preferences.toEntity(): PreferencesEntity = PreferencesEntity(
    userId = userId,
    c1 = c1,
    c2 = c2,
    c3 = c3,
    c4 = c4,
    c5 = c5,
    c6 = c6,
    c7 = c7,
    c8 = c8,
    c9 = c9,
    c10 = c10
)