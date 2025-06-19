package com.example.valenciatravel.domain.usecase

import com.example.valenciatravel.domain.repository.FavouriteRepository
import javax.inject.Inject

class AddPlaceToFavouritesUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) {
    suspend operator fun invoke(placeId: Long): Result<Boolean> {
        return try {
            val currentUser = getCurrentUserUseCase() ?: return Result.failure(Exception("Пользователь не авторизован"))
            favouriteRepository.addPlaceToFavourites(currentUser.id, placeId)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
