package com.example.valenciatravel.domain.usecase

import com.example.valenciatravel.domain.model.User
import com.example.valenciatravel.domain.repository.UserRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): User? {
        return userRepository.getCurrentUser()
    }
}