package com.example.valenciatravel.domain.usecase

import com.example.valenciatravel.domain.model.Preferences
import com.example.valenciatravel.domain.repository.PreferencesRepository
import javax.inject.Inject

class GetPreferencesUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) {
    suspend operator fun invoke(): Result<Preferences?> {
        return try {
            val currentUser = getCurrentUserUseCase() ?: return Result.failure(Exception("Пользователь не авторизован"))
            val preferences = preferencesRepository.getPreferences(currentUser.id)
            Result.success(preferences)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}