package com.example.valenciatravel.data.mapper

import com.example.valenciatravel.data.local.entity.PlaceEntity
import com.example.valenciatravel.domain.model.Place
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun PlaceEntity.toDomain(): Place = Place(
    id = id,
    name = name,
    category = category,
    text = text,
    sText = sText,
    latitude = latitude,
    longitude = longitude,
    imageLinks = Gson().fromJson(imageLinks, object : TypeToken<List<String>>() {}.type)
)

fun Place.toEntity(): PlaceEntity = PlaceEntity(
    id = id,
    name = name,
    category = category,
    text = text,
    sText = sText,
    latitude = latitude,
    longitude = longitude,
    imageLinks = Gson().toJson(imageLinks)
)