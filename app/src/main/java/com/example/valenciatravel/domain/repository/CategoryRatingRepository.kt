package com.example.valenciatravel.domain.repository

import com.example.valenciatravel.domain.model.CategoryRating

interface CategoryRatingRepository {
    suspend fun getRatingsByUserId(userId: Long): List<CategoryRating>
    suspend fun saveRating(userId: Long, categoryId: Int, isLiked: Boolean)
    suspend fun getRatingByUserIdAndCategoryId(userId: Long, categoryId: Int): CategoryRating?
}