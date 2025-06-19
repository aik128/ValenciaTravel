package com.example.valenciatravel.domain.usecase

import com.example.valenciatravel.domain.repository.FavouriteRepository
import javax.inject.Inject

class IsPlaceFavouriteUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) {
    suspend operator fun invoke(placeId: Long): Boolean {
        val currentUser = getCurrentUserUseCase() ?: return false
        return favouriteRepository.isPlaceFavourite(currentUser.id, placeId)
    }
}