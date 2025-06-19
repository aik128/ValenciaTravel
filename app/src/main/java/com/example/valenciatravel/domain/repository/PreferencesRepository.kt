package com.example.valenciatravel.domain.repository

import com.example.valenciatravel.domain.model.Preferences

interface PreferencesRepository {
    suspend fun getPreferences(userId: Long): Preferences?
    suspend fun savePreferences(preferences: Preferences)
    suspend fun updateCategoryWeight(userId: Long, categoryId: Int, weight: Int)
}