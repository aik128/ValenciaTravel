package com.example.valenciatravel.domain.usecase

import android.util.Log
import javax.inject.Inject

class UpdateCategoryWeightWithLogicUseCase @Inject constructor(
    private val updateCategoryWeightUseCase: UpdateCategoryWeightUseCase,
    private val getUserCategoryWeightsUseCase: GetUserCategoryWeightsUseCase,
    private val getCategoryPlaceCountUseCase: GetCategoryPlaceCountUseCase
) {

    suspend fun ratePlaceFromDetails(categoryId: Int, rating: Int): Result<Boolean> {
        val currentWeights = getUserCategoryWeightsUseCase()
        val categoryPlaceCounts = getCategoryPlaceCountUseCase()

        val currentWeight = currentWeights[categoryId] ?: 0
        val placesInCategory = categoryPlaceCounts[categoryId] ?: 1

        val ratingChange = when (rating) {
            1 -> -4
            2 -> -2
            3 -> 0
            4 -> 3
            5 -> 5
            else -> 0
        }

        val adjustedChange = ratingChange.toDouble() / placesInCategory
        val newWeight = currentWeight + adjustedChange.toInt()

        logging(
            categoryId,
            currentWeight,
            adjustedChange = adjustedChange.toInt(),
            placesInCategory = placesInCategory,
            newWeight = newWeight,
        )

        return updateCategoryWeightUseCase(categoryId, newWeight)
    }

    suspend fun likeFromFeed(categoryId: Int): Result<Boolean> {
        val currentWeights = getUserCategoryWeightsUseCase()
        val categoryPlaceCounts = getCategoryPlaceCountUseCase()

        val currentWeight = currentWeights[categoryId] ?: 0
        val placesInCategory = categoryPlaceCounts[categoryId] ?: 1

        val adjustedChange = 2.0 / placesInCategory
        val newWeight = currentWeight + adjustedChange.toInt()

        logging(
            categoryId,
            currentWeight,
            adjustedChange = adjustedChange.toInt(),
            placesInCategory = placesInCategory,
            newWeight = newWeight,
        )

        return updateCategoryWeightUseCase(categoryId, newWeight)
    }

    suspend fun dislikeFromFeed(categoryId: Int): Result<Boolean> {
        val currentWeights = getUserCategoryWeightsUseCase()
        val categoryPlaceCounts = getCategoryPlaceCountUseCase()

        val currentWeight = currentWeights[categoryId] ?: 0
        val placesInCategory = categoryPlaceCounts[categoryId] ?: 1

        val adjustedChange = -3.0 / placesInCategory
        val newWeight = currentWeight + adjustedChange.toInt()

        logging(
            categoryId,
            currentWeight,
            adjustedChange = adjustedChange.toInt(),
            placesInCategory = placesInCategory,
            newWeight = newWeight,
        )

        return updateCategoryWeightUseCase(categoryId, newWeight)
    }

    suspend fun likeFromPreferences(categoryId: Int): Result<Boolean> {
        val currentWeights = getUserCategoryWeightsUseCase()
        val currentWeight = currentWeights[categoryId] ?: 0
        val newWeight = currentWeight + 10

        logging(
            categoryId,
            currentWeight,
            newWeight = newWeight,
        )
        return updateCategoryWeightUseCase(categoryId, newWeight)
    }

    suspend fun dislikeFromPreferences(categoryId: Int): Result<Boolean> {
        val currentWeights = getUserCategoryWeightsUseCase()
        val currentWeight = currentWeights[categoryId] ?: 0
        val newWeight = currentWeight - 10

        logging(
            categoryId,
            currentWeight,
            newWeight = newWeight,
        )
        return updateCategoryWeightUseCase(categoryId, newWeight)
    }

    fun logging(categoryId: Int, currentWeight: Int, placesInCategory: Int = -1, adjustedChange: Int = -1, newWeight: Int) {
        Log.d("Weight Logic", "category=$categoryId, current=$currentWeight, amount=$placesInCategory, change=$adjustedChange, new=$newWeight")
    }
}