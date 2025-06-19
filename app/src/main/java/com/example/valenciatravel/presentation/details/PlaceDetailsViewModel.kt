package com.example.valenciatravel.presentation.details

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valenciatravel.data.local.UserPreferences
import com.example.valenciatravel.data.util.CategoryMapper
import com.example.valenciatravel.domain.model.Place
import com.example.valenciatravel.domain.repository.FavouriteRepository
import com.example.valenciatravel.domain.repository.PlaceRepository
import com.example.valenciatravel.domain.usecase.OpenRouteInMapsUseCase
import com.example.valenciatravel.domain.usecase.UpdateCategoryWeightUseCase
import com.example.valenciatravel.domain.usecase.UpdateCategoryWeightWithLogicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlaceDetailsState(
    val isLoading: Boolean = false,
    val place: Place? = null,
    val error: String? = null,
    val isFavorite: Boolean = false
)

@HiltViewModel
class PlaceDetailsViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val favouriteRepository: FavouriteRepository,
    private val userPreferences: UserPreferences,
    private val updateCategoryWeightUseCase: UpdateCategoryWeightUseCase,
    private val updateCategoryWeightWithLogicUseCase: UpdateCategoryWeightWithLogicUseCase,
    private val openRouteInMapsUseCase: OpenRouteInMapsUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(PlaceDetailsState())
    val state = _state.asStateFlow()

    private val currentUserId: Long = userPreferences.getCurrentUserId()
    internal val isUserAuthorized: Boolean = currentUserId != -1L && !userPreferences.isGuestMode()


    private val _selectedRating = MutableStateFlow(0)
    val selectedRating = _selectedRating.asStateFlow()

    private fun saveRatingToPrefs(placeId: Long, rating: Int) {
        val currentUserId = userPreferences.getCurrentUserId()
        val prefs = context.getSharedPreferences("place_ratings", Context.MODE_PRIVATE)
        prefs.edit().putInt("rating_${currentUserId}_$placeId", rating).apply()
    }

    private fun getRatingFromPrefs(placeId: Long): Int {
        val currentUserId = userPreferences.getCurrentUserId()
        val prefs = context.getSharedPreferences("place_ratings", Context.MODE_PRIVATE)
        val rating = prefs.getInt("rating_${currentUserId}_$placeId", 0)
        return rating
    }

    fun loadPlaceDetails(placeId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val place = placeRepository.getPlaceById(placeId)

                val isFavorite = if (isUserAuthorized) {
                    favouriteRepository.isPlaceFavourite(currentUserId, placeId)
                } else {
                    false
                }

                val savedRating = getRatingFromPrefs(placeId)
                _selectedRating.value = savedRating

                _state.update {
                    it.copy(
                        isLoading = false,
                        place = place,
                        isFavorite = isFavorite,
                        error = null
                    )
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Неизвестная ошибка"
                    )
                }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {

            val currentPlace = _state.value.place ?: return@launch

            if (!isUserAuthorized) {
                _state.update {
                    it.copy(error = "Необходимо войти в аккаунт для добавления в избранное")
                }
                return@launch
            }

            val currentFavoriteState = _state.value.isFavorite

            try {
                if (currentFavoriteState) {
                    favouriteRepository.removePlaceFromFavourites(currentUserId, currentPlace.id)
                } else {
                    favouriteRepository.addPlaceToFavourites(currentUserId, currentPlace.id)
                }

                _state.update { it.copy(isFavorite = !currentFavoriteState) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.message ?: "Ошибка при работе с избранным")
                }
            }
        }
    }

    fun ratePlace(rating: Int) {
        viewModelScope.launch {
            val currentPlace = _state.value.place ?: return@launch

            if (!isUserAuthorized) {
                _state.update {
                    it.copy(error = "Необходимо войти в аккаунт для оценки места")
                }
                return@launch
            }

            try {
                val categoryId = CategoryMapper.getCategoryId(currentPlace.category)

                if (categoryId == 0) {
                    _state.update {
                        it.copy(error = "Неизвестная категория места: ${currentPlace.category}")
                    }
                    return@launch
                }

                _selectedRating.value = rating
                saveRatingToPrefs(currentPlace.id, rating)

                updateCategoryWeightWithLogicUseCase.ratePlaceFromDetails(categoryId, rating).fold(
                    onSuccess = {
                        _state.update { it.copy(error = null) }
                    },
                    onFailure = { exception ->
                        _state.update {
                            it.copy(error = exception.message ?: "Ошибка при оценке места")
                        }
                    }
                )

            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.message ?: "Ошибка при оценке места")
                }
            }
        }
    }

    private var pendingRouteCoordinates: Pair<Double, Double>? = null
    var shouldRequestLocationPermission by mutableStateOf(false)
        private set

    fun requestRouteWithPermission(latitude: Double, longitude: Double) {
        if (hasLocationPermission()) {
            openRouteWithPermission(latitude, longitude, true)
        } else {
            pendingRouteCoordinates = Pair(latitude, longitude)
            shouldRequestLocationPermission = true
        }
    }

    fun onLocationPermissionGranted() {
        pendingRouteCoordinates?.let { (lat, lng) ->
            openRouteWithPermission(lat, lng, true)
            pendingRouteCoordinates = null
        }
    }

    fun onLocationPermissionDenied() {
        pendingRouteCoordinates?.let { (lat, lng) ->
            openRouteWithPermission(lat, lng, false)
            pendingRouteCoordinates = null
        }
    }

    fun onLocationPermissionRequested() {
        shouldRequestLocationPermission = false
    }

    private fun openRouteWithPermission(
        latitude: Double,
        longitude: Double,
        hasPermission: Boolean
    ) {
        viewModelScope.launch {
            openRouteInMapsUseCase(latitude, longitude, hasPermission).fold(
                onSuccess = {
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(error = "Не удалось открыть маршрут: ${exception.message}")
                    }
                }
            )
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}