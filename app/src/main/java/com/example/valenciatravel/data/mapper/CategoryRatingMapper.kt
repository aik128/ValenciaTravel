package com.example.valenciatravel.data.mapper

import com.example.valenciatravel.data.local.entity.CategoryRatingEntity
import com.example.valenciatravel.domain.model.CategoryRating

fun CategoryRatingEntity.toDomain(): CategoryRating = CategoryRating(
    id = id,
    userId = userId,
    categoryId = categoryId,
    isLiked = isLiked
)

fun CategoryRating.toEntity(): CategoryRatingEntity = CategoryRatingEntity(
    id = id,
    userId = userId,
    categoryId = categoryId,
    isLiked = isLiked
)