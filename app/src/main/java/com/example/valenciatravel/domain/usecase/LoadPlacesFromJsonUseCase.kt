package com.example.valenciatravel.domain.usecase

import com.example.valenciatravel.domain.repository.PlaceRepository
import javax.inject.Inject

class LoadPlacesFromJsonUseCase @Inject constructor(
    private val placeRepository: PlaceRepository
) {
    suspend operator fun invoke() {
        placeRepository.loadPlacesFromJson()
    }
}