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

        val ratingChange = when (placesInCategory) {
            in 1..2 -> when (rating) {
                5 -> 12
                4 -> 6
                3 -> 0
                2 -> -6
                1 -> -12
                else -> 0
            }
            3 -> when (rating) {
                5 -> 8
                4 -> 4
                3 -> 0
                2 -> -4
                1 -> -8
                else -> 0
            }
            in 4..5 -> when (rating) {
                5 -> 5
                4 -> 2
                3 -> 0
                2 -> -2
                1 -> -5
                else -> 0
            }
            else -> when (rating) {
                5 -> 3
                4 -> 1
                3 -> 0
                2 -> -1
                1 -> -3
                else -> 0
            }
        }

        val newWeight = currentWeight + ratingChange

        logging(
            categoryId,
            currentWeight,
            adjustedChange = ratingChange,
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

        val likeChange = when (placesInCategory) {
            in 1..2 -> 7
            3 -> 5
            in 4..5 -> 3
            else -> 2
        }

        val newWeight = currentWeight + likeChange

        logging(
            categoryId,
            currentWeight,
            adjustedChange = likeChange,
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

        val dislikeChange = when (placesInCategory) {
            in 1..2 -> -7
            3 -> -5
            in 4..5 -> -3
            else -> -2
        }

        val newWeight = currentWeight + dislikeChange

        logging(
            categoryId,
            currentWeight,
            adjustedChange = dislikeChange,
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

        val newWeight = currentWeight - 8

        logging(
            categoryId,
            currentWeight,
            newWeight = newWeight,
        )
        return updateCategoryWeightUseCase(categoryId, newWeight)
    }

    fun logging(categoryId: Int, currentWeight: Int, placesInCategory: Int = -1, adjustedChange: Int = -1, newWeight: Int) {
        Log.d("WeightLogic", "category=$categoryId, current=$currentWeight, amount=$placesInCategory, change=$adjustedChange, new=$newWeight")
    }
}