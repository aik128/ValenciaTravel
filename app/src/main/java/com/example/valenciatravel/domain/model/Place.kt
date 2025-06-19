package com.example.valenciatravel.domain.model

data class Place(
    val id: Long = 0,
    val name: String,
    val category: String,
    val text: String,
    val sText: String,
    val latitude: Double,
    val longitude: Double,
    val imageLinks: List<String>,
)
