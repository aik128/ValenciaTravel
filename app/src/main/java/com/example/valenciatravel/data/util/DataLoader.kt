package com.example.valenciatravel.data.util

import android.content.Context
import android.util.Log
import com.example.valenciatravel.data.local.dao.PlaceDao
import com.example.valenciatravel.data.local.entity.PlaceEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject


class DataLoader @Inject constructor(
    private val context: Context,
    private val placeDao: PlaceDao
) {
    suspend fun loadPlacesFromJson() {
        try {
            val existingPlaces = placeDao.getAllPlaces()
            if (existingPlaces.isNotEmpty()) {
                Log.d("DataLoader", "Places already loaded, skipping")
                return
            }

            val jsonString =
                context.assets.open("places.json").bufferedReader().use { it.readText() }

            val type = object : TypeToken<List<PlaceJson>>() {}.type
            val placesJson: List<PlaceJson> = Gson().fromJson(jsonString, type)

            val placeEntities = placesJson.map { it.toEntity() }
            placeEntities.forEach { placeEntity ->
                placeDao.insertPlace(placeEntity)
                Log.d("DataLoader", "Inserted place: ${placeEntity.name}")
            }

            Log.d("DataLoader", "Successfully loaded ${placesJson.size} places from JSON")
        } catch (e: Exception) {
            Log.e("DataLoader", "Error loading places from JSON", e)
        }
    }

    private data class PlaceJson(
        val name: String,
        val category: String,
        val text: String,
        val sText: String,
        val latitude: Double,
        val longitude: Double,
        val imageLinks: List<String>
    ) {
        fun toEntity(): PlaceEntity {
            return PlaceEntity(
                name = name,
                category = category,
                text = text,
                sText = sText,
                latitude = latitude,
                longitude = longitude,
                imageLinks = Gson().toJson(imageLinks)
            )
        }
    }
}