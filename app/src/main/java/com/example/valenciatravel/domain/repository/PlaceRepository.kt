package com.example.valenciatravel.domain.repository

import com.example.valenciatravel.domain.model.Place

interface PlaceRepository {
    suspend fun insertPlace(place: Place): Long
    suspend fun getAllPlaces(): List<Place>
    suspend fun getPlacesByCategory(category: String): List<Place>
    suspend fun getPlaceById(id: Long): Place?
    suspend fun getPlacesByIds(ids: List<Long>): List<Place>
    suspend fun loadPlacesFromJson()
    suspend fun searchPlacesByName(query: String): List<Place>
}