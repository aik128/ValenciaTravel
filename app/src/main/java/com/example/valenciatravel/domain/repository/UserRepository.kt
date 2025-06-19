package com.example.valenciatravel.domain.repository

import com.example.valenciatravel.domain.model.User

interface UserRepository {
    suspend fun registerUser(login: String, password: String): Result<User>
    suspend fun loginUser(login: String, password: String): Result<User>
    suspend fun getCurrentUser(): User?
    suspend fun saveCurrentUser(userId: Long)
    suspend fun deleteUser(userId: Long): Result<Boolean>
    suspend fun clearCurrentUser()
    suspend fun loginAsGuest(): Result<Boolean>
    suspend fun isGuestMode(): Boolean
}