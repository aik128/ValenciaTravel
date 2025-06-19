package com.example.valenciatravel.domain.usecase

import com.example.valenciatravel.data.local.UserPreferences
import javax.inject.Inject

class ClearShownUseCase @Inject constructor(
    private val userPreferences: UserPreferences
) {
    operator fun invoke() {
        userPreferences.clearShownPlaceIds()
    }
}