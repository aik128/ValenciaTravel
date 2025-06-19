package com.example.valenciatravel.data.repository

import com.example.valenciatravel.data.local.dao.FavouriteDao
import com.example.valenciatravel.data.local.dao.PlaceDao
import com.example.valenciatravel.data.mapper.toDomain
import com.example.valenciatravel.domain.model.Place
import com.example.valenciatravel.domain.repository.FavouriteRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class FavouriteRepositoryImpl @Inject constructor(
    private val favouriteDao: FavouriteDao,
    private val placeDao: PlaceDao
) : FavouriteRepository {

    override suspend fun getFavouritePlaceIds(userId: Long): List<Long> {
        val favourites = favouriteDao.getFavouritesByUserId(userId)
        return if (favourites != null) {
            Gson().fromJson(favourites.placeIds, object : TypeToken<List<Long>>() {}.type)
        } else {
            emptyList()
        }
    }

    override suspend fun addPlaceToFavourites(userId: Long, placeId: Long) {
        favouriteDao.addPlaceToFavourites(userId, placeId)
    }

    override suspend fun removePlaceFromFavourites(userId: Long, placeId: Long) {
        favouriteDao.removePlaceFromFavourites(userId, placeId)
    }

    override suspend fun isPlaceFavourite(userId: Long, placeId: Long): Boolean {
        return favouriteDao.isPlaceFavourite(userId, placeId)
    }

    override suspend fun getFavouritePlaces(userId: Long): List<Place> {
        val placeIds = getFavouritePlaceIds(userId)
        if (placeIds.isEmpty()) return emptyList()

        return placeDao.getPlacesByIds(placeIds).map { it.toDomain() }
    }
}