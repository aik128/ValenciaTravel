package com.example.valenciatravel.presentation.preferences

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valenciatravel.data.util.CategoryMapper
import com.example.valenciatravel.domain.model.Preferences
import com.example.valenciatravel.domain.usecase.GetCategoryRatingsUseCase
import com.example.valenciatravel.domain.usecase.GetPreferencesUseCase
import com.example.valenciatravel.domain.usecase.SaveCategoryRatingUseCase
import com.example.valenciatravel.domain.usecase.UpdateCategoryWeightWithLogicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val getPreferencesUseCase: GetPreferencesUseCase,
    private val updateCategoryWeightWithLogicUseCase: UpdateCategoryWeightWithLogicUseCase,
    private val getCategoryRatingsUseCase: GetCategoryRatingsUseCase,
    private val saveCategoryRatingUseCase: SaveCategoryRatingUseCase
) : ViewModel() {

    private val _preferencesState = MutableStateFlow<PreferencesState>(PreferencesState.Loading)
    val preferencesState = _preferencesState.asStateFlow()

    fun getCategoryName(categoryId: Int): String {
        return CategoryMapper.getCategoryName(categoryId)
    }


    private val _categoryWeights = MutableStateFlow<Map<Int, Int>>(emptyMap())

    private val _likedCategories = MutableStateFlow<Set<Int>>(emptySet())
    val likedCategories = _likedCategories.asStateFlow()

    private val _dislikedCategories = MutableStateFlow<Set<Int>>(emptySet())
    val dislikedCategories = _dislikedCategories.asStateFlow()

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Initial)
    val saveState = _saveState.asStateFlow()

    init {
        loadPreferences()
        loadCategoryRatings()
    }

    fun loadPreferences() {
        viewModelScope.launch {
            _preferencesState.value = PreferencesState.Loading

            getPreferencesUseCase()
                .onSuccess { preferences ->
                    if (preferences != null) {
                        _preferencesState.value = PreferencesState.Success(preferences)

                        val weights = mutableMapOf<Int, Int>()
                        weights[1] = preferences.c1
                        weights[2] = preferences.c2
                        weights[3] = preferences.c3
                        weights[4] = preferences.c4
                        weights[5] = preferences.c5
                        weights[6] = preferences.c6
                        weights[7] = preferences.c7
                        weights[8] = preferences.c8
                        weights[9] = preferences.c9
                        weights[10] = preferences.c10
                        _categoryWeights.value = weights
                    } else {
                        _preferencesState.value = PreferencesState.Error("Предпочтения не найдены")
                    }
                }
                .onFailure { error ->
                    _preferencesState.value =
                        PreferencesState.Error(error.message ?: "Неизвестная ошибка")
                }

        }
    }

    fun loadCategoryRatings() {
        viewModelScope.launch {
            getCategoryRatingsUseCase()
                .onSuccess { ratings ->
                    val liked = mutableSetOf<Int>()
                    val disliked = mutableSetOf<Int>()

                    ratings.forEach { rating ->
                        if (rating.isLiked) {
                            liked.add(rating.categoryId)
                        } else {
                            disliked.add(rating.categoryId)
                        }
                    }

                    _likedCategories.value = liked
                    _dislikedCategories.value = disliked
                }
        }
    }

    fun likeCategory(categoryId: Int) {
        viewModelScope.launch {
            val currentLiked = _likedCategories.value.toMutableSet()
            val currentDisliked = _dislikedCategories.value.toMutableSet()

            if (currentLiked.contains(categoryId)) {
                currentLiked.remove(categoryId)
                updateCategoryWeightWithLogicUseCase.dislikeFromPreferences(categoryId)
            } else {
                currentDisliked.remove(categoryId)
                currentLiked.add(categoryId)
                updateCategoryWeightWithLogicUseCase.likeFromPreferences(categoryId)
            }

            _likedCategories.value = currentLiked
            _dislikedCategories.value = currentDisliked
        }
    }

    fun dislikeCategory(categoryId: Int) {
        viewModelScope.launch {
            val currentLiked = _likedCategories.value.toMutableSet()
            val currentDisliked = _dislikedCategories.value.toMutableSet()

            if (currentDisliked.contains(categoryId)) {
                currentDisliked.remove(categoryId)
                updateCategoryWeightWithLogicUseCase.likeFromPreferences(categoryId)
            } else {
                currentLiked.remove(categoryId)
                currentDisliked.add(categoryId)
                updateCategoryWeightWithLogicUseCase.dislikeFromPreferences(categoryId)
            }

            _likedCategories.value = currentLiked
            _dislikedCategories.value = currentDisliked
        }
    }

    fun savePreferences() {
        viewModelScope.launch {
            _saveState.value = SaveState.Loading

            try {
                val liked = _likedCategories.value
                val disliked = _dislikedCategories.value

                Log.d("savePreferences", "liked: $liked, disliked: $disliked")

                for (categoryId in liked) {
                    saveCategoryRatingUseCase(categoryId, true)
                }

                for (categoryId in disliked) {
                    saveCategoryRatingUseCase(categoryId, false)
                }

                _saveState.value = SaveState.Success
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }
}

sealed class SaveState {
    object Initial : SaveState()
    object Loading : SaveState()
    object Success : SaveState()
    data class Error(val message: String) : SaveState()
}

sealed class PreferencesState {
    object Loading : PreferencesState()
    data class Success(val preferences: Preferences) : PreferencesState()
    data class Error(val message: String) : PreferencesState()
}


