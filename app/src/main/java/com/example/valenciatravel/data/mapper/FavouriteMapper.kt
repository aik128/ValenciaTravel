package com.example.valenciatravel.data.mapper

import com.example.valenciatravel.data.local.entity.FavouriteEntity
import com.example.valenciatravel.domain.model.Favourite
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun FavouriteEntity.toDomain(): Favourite = Favourite(
    userId = userId,
    placeIds = Gson().fromJson(placeIds, object : TypeToken<List<Long>>() {}.type)
)

fun Favourite.toEntity(): FavouriteEntity = FavouriteEntity(
    userId = userId,
    placeIds = Gson().toJson(placeIds)
)