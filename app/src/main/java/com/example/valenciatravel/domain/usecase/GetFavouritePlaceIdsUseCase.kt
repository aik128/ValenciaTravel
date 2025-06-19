package com.example.valenciatravel.domain.usecase

import com.example.valenciatravel.domain.repository.FavouriteRepository
import javax.inject.Inject

class GetFavouritePlaceIdsUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository
) {
    suspend operator fun invoke(userId: Long): List<Long> {
        return favouriteRepository.getFavouritePlaceIds(userId)
    }
}