package com.example.valenciatravel.domain.usecase

import com.example.valenciatravel.domain.model.User
import com.example.valenciatravel.domain.repository.UserRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(login: String, password: String): Result<User> {
        return userRepository.registerUser(login, password)
    }
}