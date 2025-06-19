package com.example.valenciatravel.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valenciatravel.domain.model.User
import com.example.valenciatravel.domain.usecase.LoginAsGuestUseCase
import com.example.valenciatravel.domain.usecase.LoginUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val loginAsGuestUseCase: LoginAsGuestUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState = _loginState.asStateFlow()

    fun login(login: String, password: String) {
        if (login.isBlank()) {
            _loginState.value = LoginState.Error("Логин не может быть пустым")
            return
        }

        if (password.isBlank()) {
            _loginState.value = LoginState.Error("Пароль не может быть пустым")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            loginUserUseCase(login, password)
                .onSuccess { user ->
                    _loginState.value = LoginState.Success(user)
                }
                .onFailure { error ->
                    _loginState.value = LoginState.Error(error.message ?: "Неизвестная ошибка")
                }
        }
    }

    fun loginAsGuest() {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            loginAsGuestUseCase()
                .onSuccess {
                    _loginState.value = LoginState.GuestSuccess
                }
                .onFailure { error ->
                    _loginState.value = LoginState.Error(error.message ?: "Неизвестная ошибка")
                }
        }
    }
}

sealed class LoginState {
    object Initial : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    object GuestSuccess : LoginState()
    data class Error(val message: String) : LoginState()
}