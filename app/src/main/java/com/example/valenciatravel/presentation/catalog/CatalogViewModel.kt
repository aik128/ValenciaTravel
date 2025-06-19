package com.example.valenciatravel.presentation.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valenciatravel.domain.repository.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CatalogState())
    val state = _state.asStateFlow()

    init {
        loadPlaces()
    }

    private fun loadPlaces() {
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

    fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }

        viewModelScope.launch {
            delay(300)
            if (_state.value.searchQuery == query) {
                performSearch()
            }
        }
    }

    private fun performSearch() {
        viewModelScope.launch {
            try {
                val currentState = _state.value
                val places = when {
                    currentState.searchQuery.isNotBlank() && currentState.selectedCategory != null -> {
                        placeRepository.searchPlacesByName(currentState.searchQuery)
                            .filter { it.category == currentState.selectedCategory }
                    }

                    currentState.searchQuery.isNotBlank() -> {
                        placeRepository.searchPlacesByName(currentState.searchQuery)
                    }

                    currentState.selectedCategory != null -> {
                        placeRepository.getPlacesByCategory(currentState.selectedCategory)
                    }

                    else -> {
                        placeRepository.getAllPlaces()
                    }
                }

                _state.update { it.copy(places = places) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Ошибка поиска: ${e.message}")
                }
            }
        }
    }

    fun selectCategory(category: String?) {
        _state.update {
            it.copy(
                selectedCategory = category,
                showCategoryDialog = false
            )
        }
        performSearch()
    }

    fun showCategoryDialog() {
        _state.update { it.copy(showCategoryDialog = true) }
    }

    fun hideCategoryDialog() {
        _state.update { it.copy(showCategoryDialog = false) }
    }

    fun clearFilters() {
        _state.update {
            it.copy(
                searchQuery = "",
                selectedCategory = null
            )
        }
        loadPlaces()
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}