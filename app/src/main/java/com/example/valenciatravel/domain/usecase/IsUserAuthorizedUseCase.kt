package com.example.valenciatravel.domain.usecase

import com.example.valenciatravel.data.local.UserPreferences
import javax.inject.Inject

class IsUserAuthorizedUseCase @Inject constructor(
    private val userPreferences: UserPreferences
) {
    operator fun invoke(): Boolean {
        val userId = userPreferences.getCurrentUserId()
        val isGuest = userPreferences.isGuestMode()
        return userId != -1L && !isGuest
    }
}