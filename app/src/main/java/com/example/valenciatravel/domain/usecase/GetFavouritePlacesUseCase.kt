package com.example.valenciatravel.domain.usecase

import com.example.valenciatravel.domain.model.Place
import com.example.valenciatravel.domain.repository.FavouriteRepository
import javax.inject.Inject

class GetFavouritePlacesUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) {
    suspend operator fun invoke(): Result<List<Place>> {
        return try {
            val currentUser = getCurrentUserUseCase() ?: return Result.failure(Exception("Пользователь не авторизован"))
            val places = favouriteRepository.getFavouritePlaces(currentUser.id)
            Result.success(places)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}