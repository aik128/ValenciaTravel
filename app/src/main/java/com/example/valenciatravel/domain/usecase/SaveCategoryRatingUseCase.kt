package com.example.valenciatravel.domain.usecase

import android.util.Log
import com.example.valenciatravel.domain.repository.CategoryRatingRepository
import javax.inject.Inject

class SaveCategoryRatingUseCase @Inject constructor(
    private val categoryRatingRepository: CategoryRatingRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) {
    suspend operator fun invoke(categoryId: Int, isLiked: Boolean): Result<Boolean> {
        return try {
            val currentUser = getCurrentUserUseCase() ?: return Result.failure(Exception("Пользователь не авторизован"))
            Log.d("SaveCategoryRatingUseCase", "сохранил в CategoryRatingRepository saveRating isLiked")
            categoryRatingRepository.saveRating(currentUser.id, categoryId, isLiked)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}