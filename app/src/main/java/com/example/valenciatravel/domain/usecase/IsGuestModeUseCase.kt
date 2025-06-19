package com.example.valenciatravel.domain.usecase

import com.example.valenciatravel.domain.repository.UserRepository
import javax.inject.Inject

class IsGuestModeUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Boolean {
        return userRepository.isGuestMode()
    }
}