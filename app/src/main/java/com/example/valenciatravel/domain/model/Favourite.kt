package com.example.valenciatravel.domain.model

data class Favourite(
    val userId: Long,
    val placeIds: List<Long>
)