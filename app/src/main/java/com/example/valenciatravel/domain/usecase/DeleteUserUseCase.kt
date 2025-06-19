package com.example.valenciatravel.domain.usecase

import com.example.valenciatravel.domain.repository.UserRepository
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long): Result<Boolean> {
        return userRepository.deleteUser(userId)
    }
}