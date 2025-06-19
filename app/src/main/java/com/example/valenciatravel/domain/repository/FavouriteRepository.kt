package com.example.valenciatravel.domain.repository

import com.example.valenciatravel.domain.model.Place

interface FavouriteRepository {
    suspend fun getFavouritePlaceIds(userId: Long): List<Long>
    suspend fun addPlaceToFavourites(userId: Long, placeId: Long)
    suspend fun removePlaceFromFavourites(userId: Long, placeId: Long)
    suspend fun isPlaceFavourite(userId: Long, placeId: Long): Boolean
    suspend fun getFavouritePlaces(userId: Long): List<Place>
}