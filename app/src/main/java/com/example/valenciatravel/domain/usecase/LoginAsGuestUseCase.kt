package com.example.valenciatravel.domain.usecase

import com.example.valenciatravel.domain.repository.UserRepository
import javax.inject.Inject

class LoginAsGuestUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Boolean> {
        return userRepository.loginAsGuest()
    }
}