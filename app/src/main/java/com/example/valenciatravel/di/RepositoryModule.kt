package com.example.valenciatravel.di

import com.example.valenciatravel.data.local.UserPreferences
import com.example.valenciatravel.data.local.dao.CategoryRatingDao
import com.example.valenciatravel.data.local.dao.FavouriteDao
import com.example.valenciatravel.data.local.dao.PlaceDao
import com.example.valenciatravel.data.local.dao.PreferencesDao
import com.example.valenciatravel.data.local.dao.UserDao
import com.example.valenciatravel.data.repository.CategoryRatingRepositoryImpl
import com.example.valenciatravel.data.repository.FavouriteRepositoryImpl
import com.example.valenciatravel.data.repository.PlaceRepositoryImpl
import com.example.valenciatravel.data.repository.PreferencesRepositoryImpl
import com.example.valenciatravel.data.repository.UserRepositoryImpl
import com.example.valenciatravel.data.util.DataLoader
import com.example.valenciatravel.domain.repository.CategoryRatingRepository
import com.example.valenciatravel.domain.repository.FavouriteRepository
import com.example.valenciatravel.domain.repository.PlaceRepository
import com.example.valenciatravel.domain.repository.PreferencesRepository
import com.example.valenciatravel.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        userDao: UserDao,
        preferencesDao: PreferencesDao,
        userPreferences: UserPreferences
    ): UserRepository {
        return UserRepositoryImpl(userDao, preferencesDao, userPreferences)
    }

    @Provides
    @Singleton
    fun provideFavouriteRepository(
        favouriteDao: FavouriteDao,
        placeDao: PlaceDao
    ): FavouriteRepository {
        return FavouriteRepositoryImpl(favouriteDao, placeDao)
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(
        preferencesDao: PreferencesDao
    ): PreferencesRepository {
        return PreferencesRepositoryImpl(preferencesDao)
    }

    @Provides
    @Singleton
    fun provideCategoryRatingRepository(
        categoryRatingDao: CategoryRatingDao
    ): CategoryRatingRepository {
        return CategoryRatingRepositoryImpl(categoryRatingDao)
    }


    @Provides
    @Singleton
    fun providePlaceRepository(
        dataLoader: DataLoader,
        placeDao: PlaceDao
    ): PlaceRepository {
        return PlaceRepositoryImpl(placeDao, dataLoader)
    }
}
