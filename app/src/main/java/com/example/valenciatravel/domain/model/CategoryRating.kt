package com.example.valenciatravel.domain.model

data class CategoryRating(
    val id: Long = 0,
    val userId: Long,
    val categoryId: Int,
    val isLiked: Boolean,
)