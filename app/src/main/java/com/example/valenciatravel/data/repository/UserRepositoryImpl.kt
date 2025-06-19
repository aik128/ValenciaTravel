package com.example.valenciatravel.data.repository

import com.example.valenciatravel.data.local.UserPreferences
import com.example.valenciatravel.data.local.dao.PreferencesDao
import com.example.valenciatravel.data.local.dao.UserDao
import com.example.valenciatravel.data.local.entity.PreferencesEntity
import com.example.valenciatravel.data.local.entity.UserEntity
import com.example.valenciatravel.data.mapper.toDomain
import com.example.valenciatravel.domain.model.User
import com.example.valenciatravel.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val preferencesDao: PreferencesDao,
    private val userPreferences: UserPreferences
) : UserRepository {

    override suspend fun registerUser(login: String, password: String): Result<User> {
        return try {
            val existingUser = userDao.getUserByLogin(login)
            if (existingUser != null) {
                return Result.failure(Exception("Пользователь с таким логином уже существует"))
            }

            val userEntity = UserEntity(login = login, password = password)
            val userId = userDao.insertUser(userEntity)

            val preferencesEntity = PreferencesEntity(userId = userId)
            preferencesDao.insertPreferences(preferencesEntity)

            userPreferences.saveCurrentUserId(userId)

            Result.success(User(id = userId, login = login, password = password))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginUser(login: String, password: String): Result<User> {
        return try {
            val user = userDao.getUserByLoginAndPassword(login, password)
                ?: return Result.failure(Exception("Неверный логин или пароль"))

            userPreferences.saveCurrentUserId(user.id)

            Result.success(user.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User? {
        val userId = userPreferences.getCurrentUserId()
        if (userId == -1L) return null

        return userDao.getUserById(userId)?.toDomain()
    }

    override suspend fun saveCurrentUser(userId: Long) {
        userPreferences.saveCurrentUserId(userId)
    }

    override suspend fun deleteUser(userId: Long): Result<Boolean> {
        return try {
            val user = userDao.getUserById(userId)
                ?: return Result.failure(Exception("Пользователь не найден"))

            userDao.deleteUser(user)

            if (userPreferences.getCurrentUserId() == userId) {
                userPreferences.clearCurrentUser()
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearCurrentUser() {
        userPreferences.clearCurrentUser()
    }

    override suspend fun loginAsGuest(): Result<Boolean> = try {
        userPreferences.setGuestMode(true)
        Result.success(true)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun isGuestMode(): Boolean {
        return userPreferences.isGuestMode()
    }
}