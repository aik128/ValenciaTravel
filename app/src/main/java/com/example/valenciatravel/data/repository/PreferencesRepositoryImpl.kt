package com.example.valenciatravel.data.repository

import android.util.Log
import com.example.valenciatravel.data.local.dao.PreferencesDao
import com.example.valenciatravel.data.mapper.toDomain
import com.example.valenciatravel.data.mapper.toEntity
import com.example.valenciatravel.domain.model.Preferences
import com.example.valenciatravel.domain.repository.PreferencesRepository
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val preferencesDao: PreferencesDao
) : PreferencesRepository {

    override suspend fun getPreferences(userId: Long): Preferences? {
        return preferencesDao.getPreferencesByUserId(userId)?.toDomain()
    }

    override suspend fun savePreferences(preferences: Preferences) {
        preferencesDao.insertPreferences(preferences.toEntity())
    }

    override suspend fun updateCategoryWeight(userId: Long, categoryId: Int, weight: Int) {

        Log.d("PreferencesRepositoryImpl", "Обновление веса: userId=$userId, categoryId=$categoryId, weight=$weight")

        when (categoryId) {
            1 -> preferencesDao.updateC1(userId, weight)
            2 -> preferencesDao.updateC2(userId, weight)
            3 -> preferencesDao.updateC3(userId, weight)
            4 -> preferencesDao.updateC4(userId, weight)
            5 -> preferencesDao.updateC5(userId, weight)
            6 -> preferencesDao.updateC6(userId, weight)
            7 -> preferencesDao.updateC7(userId, weight)
            8 -> preferencesDao.updateC8(userId, weight)
            9 -> preferencesDao.updateC9(userId, weight)
            10 -> preferencesDao.updateC10(userId, weight)
        }
    }
}