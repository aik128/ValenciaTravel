package com.example.valenciatravel.domain.usecase


import com.example.valenciatravel.data.util.CategoryMapper
import com.example.valenciatravel.domain.repository.PlaceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCategoryPlaceCountUseCase @Inject constructor(
    private val placeRepository: PlaceRepository
) {
    private var categoryCountCache: Map<Int, Int>? = null

    suspend operator fun invoke(): Map<Int, Int> {
        if (categoryCountCache != null) {
            return categoryCountCache!!
        }

        return try {
            val allPlaces = placeRepository.getAllPlaces()
            val counts = mutableMapOf<Int, Int>()

            allPlaces.forEach { place ->
                val categoryId = CategoryMapper.getCategoryId(place.category)
                if (categoryId != 0) {
                    counts[categoryId] = (counts[categoryId] ?: 0) + 1
                }
            }

            categoryCountCache = counts
            counts
        } catch (e: Exception) {
            emptyMap()
        }
    }

}