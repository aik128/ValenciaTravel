package com.example.valenciatravel.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val text: String,
    val sText: String,
    val latitude: Double,
    val longitude: Double,
    val imageLinks: String,

)