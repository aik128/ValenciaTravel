package com.example.valenciatravel.domain.usecase

import com.example.valenciatravel.domain.model.CategoryRating
import com.example.valenciatravel.domain.repository.CategoryRatingRepository
import javax.inject.Inject

class GetCategoryRatingsUseCase @Inject constructor(
    private val categoryRatingRepository: CategoryRatingRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) {
    suspend operator fun invoke(): Result<List<CategoryRating>> {
        return try {
            val currentUser = getCurrentUserUseCase() ?: return Result.failure(Exception("Пользователь не авторизован"))
            val ratings = categoryRatingRepository.getRatingsByUserId(currentUser.id)
            Result.success(ratings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
