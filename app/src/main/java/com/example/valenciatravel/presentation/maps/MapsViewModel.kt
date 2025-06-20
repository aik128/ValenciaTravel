package com.example.valenciatravel.presentation.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valenciatravel.domain.model.Place
import com.example.valenciatravel.domain.repository.PlaceRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(MapsState())
    val state = _state.asStateFlow()

    val mapCenter = LatLng(39.4700, -0.3500)


    var shouldRequestLocationPermission by mutableStateOf(false)
        private set

    init {
        loadPlaces()
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

        _state.update { it.copy(hasLocationPermission = hasPermission) }

        if (hasPermission) {
            getCurrentLocation()
        }
        else {
            requestLocationPermission()
        }
    }

    fun requestLocationPermission() {
        shouldRequestLocationPermission = true
    }

    fun onLocationPermissionGranted() {
        _state.update { it.copy(hasLocationPermission = true) }
        getCurrentLocation()
        shouldRequestLocationPermission = false
    }

    fun onLocationPermissionDenied() {
        _state.update { it.copy(hasLocationPermission = false) }
        shouldRequestLocationPermission = false
    }

    fun onLocationPermissionRequested() {
        shouldRequestLocationPermission = false
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (!_state.value.hasLocationPermission) return

        viewModelScope.launch {
            try {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

                val providers = listOf(
                    LocationManager.GPS_PROVIDER,
                    LocationManager.NETWORK_PROVIDER
                )

                for (provider in providers) {
                    if (locationManager.isProviderEnabled(provider)) {
                        val location = locationManager.getLastKnownLocation(provider)
                        if (location != null) {
                            _state.update {
                                it.copy(userLocation = LatLng(location.latitude, location.longitude))
                            }
                            break
                        }
                    }
                }
            } catch (e: Exception) {
                Log.i("User's location", "No permissions")
            }
        }
    }


    internal fun loadPlaces() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val places = placeRepository.getAllPlaces()
                _state.update {
                    it.copy(
                        isLoading = false,
                        places = places,
                        error = null
                    )
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка загрузки мест"
                    )
                }
            }
        }
    }

    fun selectPlace(place: Place) {
        _state.update {
            it.copy(
                selectedPlace = place,
                showPlacePopup = true
            )
        }
    }

    fun closePlacePopup() {
        _state.update {
            it.copy(
                selectedPlace = null,
                showPlacePopup = false
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

}

data class MapsState(
    val isLoading: Boolean = false,
    val places: List<Place> = emptyList(),
    val selectedPlace: Place? = null,
    val error: String? = null,
    val showPlacePopup: Boolean = false,
    val userLocation: LatLng? = null,
    val hasLocationPermission: Boolean = false
)