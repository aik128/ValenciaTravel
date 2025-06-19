package com.example.valenciatravel.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "preferences",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class PreferencesEntity(
    @PrimaryKey
    val userId: Long,
    val c1: Int = 0,
    val c2: Int = 0,
    val c3: Int = 0,
    val c4: Int = 0,
    val c5: Int = 0,
    val c6: Int = 0,
    val c7: Int = 0,
    val c8: Int = 0,
    val c9: Int = 0,
    val c10: Int = 0
)
