package com.example.valenciatravel.data.repository

import com.example.valenciatravel.data.local.dao.PlaceDao
import com.example.valenciatravel.data.mapper.toDomain
import com.example.valenciatravel.data.mapper.toEntity
import com.example.valenciatravel.data.util.DataLoader
import com.example.valenciatravel.domain.model.Place
import com.example.valenciatravel.domain.repository.PlaceRepository
import javax.inject.Inject

class PlaceRepositoryImpl @Inject constructor(
    private val placeDao: PlaceDao,
    private val dataLoader: DataLoader
) : PlaceRepository {

    override suspend fun getAllPlaces(): List<Place> {
        return placeDao.getAllPlaces().map { it.toDomain() }
    }

    override suspend fun getPlacesByCategory(category: String): List<Place> {
        return placeDao.getPlacesByCategory(category).map { it.toDomain() }
    }

    override suspend fun getPlaceById(id: Long): Place? {
        return placeDao.getPlaceById(id)?.toDomain()
    }

    override suspend fun getPlacesByIds(ids: List<Long>): List<Place> {
        return placeDao.getPlacesByIds(ids).map { it.toDomain() }
    }

    override suspend fun loadPlacesFromJson() {
        dataLoader.loadPlacesFromJson()
    }

    override suspend fun insertPlace(place: Place): Long {
        return placeDao.insertPlace(place.toEntity())
    }

    override suspend fun searchPlacesByName(query: String): List<Place> {
        return placeDao.searchPlacesByName(query).map { it.toDomain() }
    }
}