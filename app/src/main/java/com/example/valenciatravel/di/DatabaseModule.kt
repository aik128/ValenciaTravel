package com.example.valenciatravel.di

import android.content.Context
import androidx.room.Room
import com.example.valenciatravel.data.local.dao.CategoryRatingDao
import com.example.valenciatravel.data.local.dao.FavouriteDao
import com.example.valenciatravel.data.local.dao.PlaceDao
import com.example.valenciatravel.data.local.dao.PreferencesDao
import com.example.valenciatravel.data.local.dao.UserDao
import com.example.valenciatravel.data.local.database.AppDatabase
import com.example.valenciatravel.data.local.database.MIGRATION_1_2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).addMigrations(MIGRATION_1_2).build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun providePreferencesDao(database: AppDatabase): PreferencesDao {
        return database.preferencesDao()
    }

    @Provides
    @Singleton
    fun providePlaceDao(database: AppDatabase): PlaceDao {
        return database.placeDao()
    }

    @Provides
    @Singleton
    fun provideFavouriteDao(database: AppDatabase): FavouriteDao {
        return database.favouriteDao()
    }

    @Provides
    @Singleton
    fun provideCategoryRatingDao(database: AppDatabase): CategoryRatingDao {
        return database.categoryRatingDao()
    }
}