package com.example.valenciatravel.data.repository

import com.example.valenciatravel.data.local.dao.CategoryRatingDao
import com.example.valenciatravel.data.local.entity.CategoryRatingEntity
import com.example.valenciatravel.data.mapper.toDomain
import com.example.valenciatravel.domain.model.CategoryRating
import com.example.valenciatravel.domain.repository.CategoryRatingRepository
import javax.inject.Inject

class CategoryRatingRepositoryImpl @Inject constructor(
    private val categoryRatingDao: CategoryRatingDao
) : CategoryRatingRepository {

    override suspend fun getRatingsByUserId(userId: Long): List<CategoryRating> {
        return categoryRatingDao.getRatingsByUserId(userId).map { it.toDomain() }
    }

    override suspend fun saveRating(userId: Long, categoryId: Int, isLiked: Boolean) {
        val rating = CategoryRatingEntity(
            userId = userId,
            categoryId = categoryId,
            isLiked = isLiked
        )
        categoryRatingDao.upsertRating(rating)
    }

    override suspend fun getRatingByUserIdAndCategoryId(userId: Long, categoryId: Int): CategoryRating? {
        return categoryRatingDao.getRatingByUserIdAndCategoryId(userId, categoryId)?.toDomain()
    }
}