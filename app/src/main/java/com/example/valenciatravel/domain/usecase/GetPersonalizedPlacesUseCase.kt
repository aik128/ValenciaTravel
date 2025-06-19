package com.example.valenciatravel.domain.usecase

import com.example.valenciatravel.data.local.UserPreferences
import com.example.valenciatravel.data.util.CategoryMapper
import com.example.valenciatravel.domain.model.Place
import com.example.valenciatravel.domain.repository.PlaceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPersonalizedPlacesUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val getUserCategoryWeightsUseCase: GetUserCategoryWeightsUseCase,
    private val userPreferences: UserPreferences
) {
    companion object {
        private const val EXPLORATION_RATE = 0.2f
        private const val MIN_WEIGHT_THRESHOLD = -10
    }

    suspend operator fun invoke(): List<Place> {
        return try {
            val allPlaces = placeRepository.getAllPlaces()
            val categoryWeights = getUserCategoryWeightsUseCase()
            val shownPlaceIds = getShownPlaceIds()

            val availablePlaces = allPlaces

            if (availablePlaces.isEmpty()) {
                clearShownPlaces()
                return personalizeOrder(allPlaces, categoryWeights)
            }

            return personalizeOrder(availablePlaces, categoryWeights)

        } catch (e: Exception) {
            placeRepository.getAllPlaces().shuffled()
        }
    }

    private fun personalizeOrder(places: List<Place>, categoryWeights: Map<Int, Int>): List<Place> {
        val placesByCategory = places.groupBy { place ->
            CategoryMapper.getCategoryId(place.category)
        }

        val weightedPlaces = mutableListOf<Pair<Place, Double>>()

        placesByCategory.forEach { (categoryId, categoryPlaces) ->
            val categoryWeight = categoryWeights[categoryId] ?: 0

            if (categoryWeight < MIN_WEIGHT_THRESHOLD) return@forEach

            categoryPlaces.forEach { place ->
                val score = calculatePlaceScore(place, categoryWeight)
                weightedPlaces.add(place to score)
            }
        }

        return weightedPlaces
            .sortedByDescending { (_, score) ->
                if (Math.random() < EXPLORATION_RATE) {
                    score + (Math.random() - 0.5) * 10
                } else {
                    score
                }
            }
            .map { it.first }
    }

    private fun calculatePlaceScore(place: Place, categoryWeight: Int): Double {
        var score = categoryWeight.toDouble()

        if (categoryWeight > 0) {
            score *= 1.2
        }

        score += (Math.random() - 0.5) * 2

        return score
    }

    private fun getShownPlaceIds(): Set<Long> {
        val shownIds = userPreferences.getShownPlaceIds()
        return if (shownIds.isBlank()) {
            emptySet()
        } else {
            shownIds.split(",").mapNotNull { it.toLongOrNull() }.toSet()
        }
    }

    private fun clearShownPlaces() {
        userPreferences.clearShownPlaceIds()
    }

    fun markPlaceAsShown(placeId: Long) {
        val currentShown = getShownPlaceIds().toMutableSet()
        currentShown.add(placeId)
        val shownIdsString = currentShown.joinToString(",")
        userPreferences.saveShownPlaceIds(shownIdsString)
    }
}