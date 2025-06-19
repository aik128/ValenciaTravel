package com.example.valenciatravel.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.valenciatravel.data.local.entity.FavouriteEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Dao
interface FavouriteDao {
    @Query("SELECT * FROM favourites WHERE userId = :userId")
    suspend fun getFavouritesByUserId(userId: Long): FavouriteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourites(favourite: FavouriteEntity)

    @Query("DELETE FROM favourites WHERE userId = :userId")
    suspend fun deleteFavouritesByUserId(userId: Long)

    @Transaction
    suspend fun addPlaceToFavourites(userId: Long, placeId: Long) {
        val favourites = getFavouritesByUserId(userId)
        val placeIds = if (favourites != null) {
            val currentIds = Gson().fromJson<List<Long>>(
                favourites.placeIds,
                object : TypeToken<List<Long>>() {}.type
            ).toMutableList()

            if (!currentIds.contains(placeId)) {
                currentIds.add(placeId)
            }
            currentIds
        } else {
            listOf(placeId)
        }

        insertFavourites(
            FavouriteEntity(
                userId = userId,
                placeIds = Gson().toJson(placeIds)
            )
        )
    }

    @Transaction
    suspend fun removePlaceFromFavourites(userId: Long, placeId: Long) {
        val favourites = getFavouritesByUserId(userId) ?: return

        val currentIds = Gson().fromJson<List<Long>>(
            favourites.placeIds,
            object : TypeToken<List<Long>>() {}.type
        ).toMutableList()

        if (currentIds.contains(placeId)) {
            currentIds.remove(placeId)

            insertFavourites(
                FavouriteEntity(
                    userId = userId,
                    placeIds = Gson().toJson(currentIds)
                )
            )
        }
    }

    @Transaction
    suspend fun isPlaceFavourite(userId: Long, placeId: Long): Boolean {
        val favourites = getFavouritesByUserId(userId) ?: return false

        val placeIds = Gson().fromJson<List<Long>>(
            favourites.placeIds,
            object : TypeToken<List<Long>>() {}.type
        )

        return placeIds.contains(placeId)
    }
}