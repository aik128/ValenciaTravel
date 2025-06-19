package com.example.valenciatravel.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.valenciatravel.data.local.entity.PlaceEntity

@Dao
interface PlaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceEntity): Long

    @Query("SELECT * FROM places")
    suspend fun getAllPlaces(): List<PlaceEntity>

    @Query("SELECT * FROM places WHERE category = :category")
    suspend fun getPlacesByCategory(category: String): List<PlaceEntity>

    @Query("SELECT * FROM places WHERE id = :placeId")
    suspend fun getPlaceById(placeId: Long): PlaceEntity?

    @Query("SELECT * FROM places WHERE id IN (:placeIds)")
    suspend fun getPlacesByIds(placeIds: List<Long>): List<PlaceEntity>

    @Query("SELECT * FROM places WHERE name LIKE '%' || :query || '%'")
    suspend fun searchPlacesByName(query: String): List<PlaceEntity>

}