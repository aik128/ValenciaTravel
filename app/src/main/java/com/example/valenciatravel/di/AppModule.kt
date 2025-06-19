package com.example.valenciatravel.di

import android.content.Context
import com.example.valenciatravel.data.local.UserPreferences
import com.example.valenciatravel.data.local.dao.PlaceDao
import com.example.valenciatravel.data.util.DataLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun provideDataLoader(
        @ApplicationContext context: Context,
        placeDao: PlaceDao
    ): DataLoader {
        return DataLoader(context, placeDao)
    }
}