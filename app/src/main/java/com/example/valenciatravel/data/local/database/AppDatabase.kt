package com.example.valenciatravel.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.valenciatravel.data.local.dao.CategoryRatingDao
import com.example.valenciatravel.data.local.dao.FavouriteDao
import com.example.valenciatravel.data.local.dao.PlaceDao
import com.example.valenciatravel.data.local.dao.PreferencesDao
import com.example.valenciatravel.data.local.dao.UserDao
import com.example.valenciatravel.data.local.entity.CategoryRatingEntity
import com.example.valenciatravel.data.local.entity.FavouriteEntity
import com.example.valenciatravel.data.local.entity.PlaceEntity
import com.example.valenciatravel.data.local.entity.PreferencesEntity
import com.example.valenciatravel.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        PreferencesEntity::class,
        PlaceEntity::class,
        FavouriteEntity::class,
        CategoryRatingEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun preferencesDao(): PreferencesDao
    abstract fun placeDao(): PlaceDao
    abstract fun favouriteDao(): FavouriteDao
    abstract fun categoryRatingDao(): CategoryRatingDao

    companion object {
        const val DATABASE_NAME = "guide_database"
    }
}