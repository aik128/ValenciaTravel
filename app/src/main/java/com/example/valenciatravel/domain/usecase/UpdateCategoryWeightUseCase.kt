package com.example.valenciatravel.domain.usecase

import android.util.Log
import com.example.valenciatravel.domain.repository.PreferencesRepository
import javax.inject.Inject

class UpdateCategoryWeightUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) {
    suspend operator fun invoke(categoryId: Int, weight: Int): Result<Boolean> {
        return try {
            val currentUser = getCurrentUserUseCase() ?: return Result.failure(Exception("Пользователь не авторизован"))
            preferencesRepository.updateCategoryWeight(currentUser.id, categoryId, weight)
            Log.d("Weight Change", "Категория '$categoryId' (ID: $categoryId) установлена в $weight (пользователь: ${currentUser.id})")
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}