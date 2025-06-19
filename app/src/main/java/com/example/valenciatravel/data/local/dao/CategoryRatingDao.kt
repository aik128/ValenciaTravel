package com.example.valenciatravel.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.valenciatravel.data.local.entity.CategoryRatingEntity

@Dao
interface CategoryRatingDao {
    @Query("SELECT * FROM category_ratings WHERE userId = :userId")
    suspend fun getRatingsByUserId(userId: Long): List<CategoryRatingEntity>

    @Query("SELECT * FROM category_ratings WHERE userId = :userId AND categoryId = :categoryId")
    suspend fun getRatingByUserIdAndCategoryId(userId: Long, categoryId: Int): CategoryRatingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRating(rating: CategoryRatingEntity): Long

    @Query("DELETE FROM category_ratings WHERE userId = :userId AND categoryId = :categoryId")
    suspend fun deleteRatingByUserIdAndCategoryId(userId: Long, categoryId: Int)

    @Transaction
    suspend fun upsertRating(rating: CategoryRatingEntity) {
        val existingRating = getRatingByUserIdAndCategoryId(rating.userId, rating.categoryId)
        if (existingRating != null) {
            deleteRatingByUserIdAndCategoryId(rating.userId, rating.categoryId)
        }
        insertRating(rating)
    }
}