package com.example.valenciatravel.presentation.feed


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valenciatravel.data.local.UserPreferences
import com.example.valenciatravel.data.util.CategoryMapper
import com.example.valenciatravel.domain.model.Place
import com.example.valenciatravel.domain.repository.FavouriteRepository
import com.example.valenciatravel.domain.usecase.GetPersonalizedPlacesUseCase
import com.example.valenciatravel.domain.usecase.IsGuestModeUseCase
import com.example.valenciatravel.domain.usecase.UpdateCategoryWeightWithLogicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getPersonalizedPlacesUseCase: GetPersonalizedPlacesUseCase,
    private val isGuestModeUseCase: IsGuestModeUseCase,
    private val updateCategoryWeightWithLogicUseCase: UpdateCategoryWeightWithLogicUseCase,
    private val favouriteRepository: FavouriteRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(FeedState())
    val state = _state.asStateFlow()

    private val _currentPlaceStates = MutableStateFlow<Map<Long, PlaceInteractionState>>(emptyMap())
    val currentPlaceStates = _currentPlaceStates.asStateFlow()

    init {
        loadPersonalizedPlaces()
    }

    private fun loadPersonalizedPlaces() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val personalizedPlaces = getPersonalizedPlacesUseCase()
                _state.update {
                    it.copy(
                        isLoading = false,
                        places = personalizedPlaces,
                        error = null
                    )
                }

                loadPlaceStates(personalizedPlaces)

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка загрузки рекомендаций"
                    )
                }
            }
        }
    }

    private fun loadPlaceStates(places: List<Place>) {
        viewModelScope.launch {
            val userId = userPreferences.getCurrentUserId()
            if (userId == -1L) return@launch

            val states = mutableMapOf<Long, PlaceInteractionState>()

            places.forEach { place ->
                try {
                    val isFavorite = favouriteRepository.isPlaceFavourite(userId, place.id)
                    states[place.id] = PlaceInteractionState(
                        isLiked = false,
                        isDisliked = false,
                        isFavorite = isFavorite
                    )
                } catch (e: Exception) {
                    states[place.id] = PlaceInteractionState()
                }
            }

            _currentPlaceStates.value = states
        }
    }

    suspend fun isUserAuthorized(): Boolean {
        return !isGuestModeUseCase()
    }

    fun nextPlace() {
        val currentState = _state.value
        if (currentState.currentIndex < currentState.places.size - 1) {
            val currentPlace = currentState.places[currentState.currentIndex]
            viewModelScope.launch {
                getPersonalizedPlacesUseCase.markPlaceAsShown(currentPlace.id)
            }

            _state.update { it.copy(currentIndex = currentState.currentIndex + 1) }
        }
    }

    fun previousPlace() {
        val currentState = _state.value
        if (currentState.currentIndex > 0) {
            _state.update { it.copy(currentIndex = currentState.currentIndex - 1) }
        }
    }

    fun likeCurrentPlace() {
        viewModelScope.launch {
            val currentPlace = getCurrentPlace() ?: return@launch
            val categoryId = CategoryMapper.getCategoryId(currentPlace.category)

            if (categoryId == 0) return@launch

            val currentStates = _currentPlaceStates.value.toMutableMap()
            val currentPlaceState = currentStates[currentPlace.id] ?: PlaceInteractionState()

            if (currentPlaceState.isLiked) {
                currentStates[currentPlace.id] = currentPlaceState.copy(isLiked = false)
            } else {
                currentStates[currentPlace.id] = currentPlaceState.copy(
                    isLiked = true,
                    isDisliked = false
                )

                updateCategoryWeightWithLogicUseCase.likeFromFeed(categoryId)
            }

            _currentPlaceStates.value = currentStates
        }
    }

    fun dislikeCurrentPlace() {
        viewModelScope.launch {
            val currentPlace = getCurrentPlace() ?: return@launch
            val categoryId = CategoryMapper.getCategoryId(currentPlace.category)

            if (categoryId == 0) return@launch

            val currentStates = _currentPlaceStates.value.toMutableMap()
            val currentPlaceState = currentStates[currentPlace.id] ?: PlaceInteractionState()

            if (currentPlaceState.isDisliked) {
                currentStates[currentPlace.id] = currentPlaceState.copy(isDisliked = false)
            } else {
                currentStates[currentPlace.id] = currentPlaceState.copy(
                    isDisliked = true,
                    isLiked = false
                )

                updateCategoryWeightWithLogicUseCase.dislikeFromFeed(categoryId)
            }

            _currentPlaceStates.value = currentStates
        }
    }

    fun toggleFavoriteCurrentPlace() {
        viewModelScope.launch {
            val currentPlace = getCurrentPlace() ?: return@launch
            val userId = userPreferences.getCurrentUserId()

            if (userId == -1L) {
                _state.update { it.copy(error = "Необходимо войти в аккаунт") }
                return@launch
            }

            val currentStates = _currentPlaceStates.value.toMutableMap()
            val currentPlaceState = currentStates[currentPlace.id] ?: PlaceInteractionState()

            try {
                if (currentPlaceState.isFavorite) {
                    favouriteRepository.removePlaceFromFavourites(userId, currentPlace.id)
                    currentStates[currentPlace.id] = currentPlaceState.copy(isFavorite = false)
                } else {
                    favouriteRepository.addPlaceToFavourites(userId, currentPlace.id)
                    currentStates[currentPlace.id] = currentPlaceState.copy(isFavorite = true)
                }

                _currentPlaceStates.value = currentStates

            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.message ?: "Ошибка при работе с избранным")
                }
            }
        }
    }

    fun getCurrentPlaceState(): PlaceInteractionState? {
        val currentPlace = getCurrentPlace() ?: return null
        return _currentPlaceStates.value[currentPlace.id]
    }

    private fun getCurrentPlace(): Place? {
        val currentState = _state.value
        return if (currentState.places.isNotEmpty() && currentState.currentIndex < currentState.places.size) {
            currentState.places[currentState.currentIndex]
        } else null
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }


    data class PlaceInteractionState(
        val isLiked: Boolean = false,
        val isDisliked: Boolean = false,
        val isFavorite: Boolean = false
    )

    data class FeedState(
        val isLoading: Boolean = false,
        val places: List<Place> = emptyList(),
        val currentIndex: Int = 0,
        val error: String? = null
    )

}


