package com.example.valenciatravel.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valenciatravel.data.local.UserPreferences
import com.example.valenciatravel.domain.model.Place
import com.example.valenciatravel.domain.repository.FavouriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favouriteRepository: FavouriteRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesState())
    val state = _state.asStateFlow()

    fun loadFavorites() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val currentUserId = userPreferences.getCurrentUserId()
                if (currentUserId == -1L) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            places = emptyList(),
                            error = null
                        )
                    }
                    return@launch
                }

                val favoritePlaces = favouriteRepository.getFavouritePlaces(currentUserId)
                _state.update {
                    it.copy(
                        isLoading = false,
                        places = favoritePlaces,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка загрузки избранного"
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

data class FavoritesState(
    val isLoading: Boolean = false,
    val places: List<Place> = emptyList(),
    val error: String? = null
)