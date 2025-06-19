package com.example.valenciatravel.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valenciatravel.data.local.UserPreferences
import com.example.valenciatravel.domain.model.User
import com.example.valenciatravel.domain.repository.FavouriteRepository
import com.example.valenciatravel.domain.repository.UserRepository
import com.example.valenciatravel.domain.usecase.DeleteUserUseCase
import com.example.valenciatravel.domain.usecase.DownloadOfflineMapUseCase
import com.example.valenciatravel.domain.usecase.GetCurrentUserUseCase
import com.example.valenciatravel.domain.usecase.IsUserAuthorizedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val userRepository: UserRepository,
    private val downloadOfflineMapUseCase: DownloadOfflineMapUseCase,
    private val userPreferences: UserPreferences,
    private val favouriteRepository: FavouriteRepository,
    private val isUserAuthorizedUseCase: IsUserAuthorizedUseCase
) : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    private val _userState = MutableStateFlow<User?>(null)
    val userState = _userState.asStateFlow()

    private val _deleteState = MutableStateFlow<DeleteUserState>(DeleteUserState.Initial)
    val deleteState: StateFlow<DeleteUserState> = _deleteState.asStateFlow()

    private val _favoriteCount = MutableStateFlow(0)
    val favoriteCount = _favoriteCount.asStateFlow()

    fun downloadOfflineMap() {
        viewModelScope.launch {
            downloadOfflineMapUseCase().fold(
                onSuccess = { instructions ->
                    _message.value = instructions
                },
                onFailure = { exception ->
                    _message.value = exception.message ?: "Не удалось открыть Google Maps"
                }
            )
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    fun isUserAuthorized(): Boolean {
        return isUserAuthorizedUseCase()
    }


    private fun loadFavoriteCount() {
        viewModelScope.launch {
            try {
                val currentUserId = userPreferences.getCurrentUserId()
                if (currentUserId != -1L) {
                    val favoriteIds = favouriteRepository.getFavouritePlaceIds(currentUserId)
                    _favoriteCount.value = favoriteIds.size
                }
            } catch (e: Exception) {
            }
        }
    }

    fun refreshFavoriteCount() {
        loadFavoriteCount()
    }

    init {
        loadCurrentUser()
        loadFavoriteCount()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _userState.value = getCurrentUserUseCase()
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _deleteState.value = DeleteUserState.Loading

            val currentUser = _userState.value
            if (currentUser != null) {
                deleteUserUseCase(currentUser.id)
                    .onSuccess {
                        _deleteState.value = DeleteUserState.Success
                    }
                    .onFailure { error ->
                        _deleteState.value = DeleteUserState.Error(error.message ?: "Неизвестная ошибка")
                    }
            } else {
                _deleteState.value = DeleteUserState.Error("Пользователь не найден")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.clearCurrentUser()
            _userState.value = null
            _deleteState.value = DeleteUserState.Success
        }
    }
}

sealed class DeleteUserState {
    object Initial : DeleteUserState()
    object Loading : DeleteUserState()
    object Success : DeleteUserState()
    data class Error(val message: String) : DeleteUserState()
}