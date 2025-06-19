package com.example.valenciatravel.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valenciatravel.domain.model.User
import com.example.valenciatravel.domain.usecase.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Initial)
    val registerState = _registerState.asStateFlow()

    fun register(login: String, password: String, confirmPassword: String) {
        if (login.isBlank()) {
            _registerState.value = RegisterState.Error("Логин не может быть пустым")
            return
        }

        if (password.isBlank()) {
            _registerState.value = RegisterState.Error("Пароль не может быть пустым")
            return
        }

        if (password != confirmPassword) {
            _registerState.value = RegisterState.Error("Пароли не совпадают")
            return
        }

        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            registerUserUseCase(login, password)
                .onSuccess { user ->
                    _registerState.value = RegisterState.Success(user)
                }
                .onFailure { error ->
                    _registerState.value = RegisterState.Error(error.message ?: "Неизвестная ошибка")
                }
        }
    }
}

sealed class RegisterState {
    object Initial : RegisterState()
    object Loading : RegisterState()
    data class Success(val user: User) : RegisterState()
    data class Error(val message: String) : RegisterState()
}