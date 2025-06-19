package com.example.valenciatravel.data.local

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveCurrentUserId(userId: Long) {
        sharedPreferences.edit().putLong(CURRENT_USER_ID, userId).apply()
        sharedPreferences.edit().putBoolean(IS_GUEST_MODE, false).apply()
        Log.d("UserPreferences", "Saved current user ID: $userId")
    }

    fun getCurrentUserId(): Long {
        return sharedPreferences.getLong(CURRENT_USER_ID, -1)
    }

    fun clearCurrentUser() {
        sharedPreferences.edit().remove(CURRENT_USER_ID).apply()
        sharedPreferences.edit().putBoolean(IS_GUEST_MODE, false).apply()
        Log.d("UserPreferences", "Cleared current user ID")
    }

    fun setGuestMode(isGuest: Boolean) {
        sharedPreferences.edit().putBoolean(IS_GUEST_MODE, isGuest).apply()
        if (isGuest) {
            sharedPreferences.edit().remove(CURRENT_USER_ID).apply()
        }
        Log.d("UserPreferences", "Guest mode set to: $isGuest")
    }

    fun isGuestMode(): Boolean {
        return sharedPreferences.getBoolean(IS_GUEST_MODE, false)
    }

    fun saveShownPlaceIds(shownIds: String) {
        sharedPreferences.edit().putString(SHOWN_PLACE_IDS, shownIds).apply()
        Log.d("UserPreferences", "Saved shown place IDs: $shownIds")
    }

    fun getShownPlaceIds(): String {
        return sharedPreferences.getString(SHOWN_PLACE_IDS, "") ?: ""
    }

    fun clearShownPlaceIds() {
        sharedPreferences.edit().remove(SHOWN_PLACE_IDS).apply()
        Log.d("UserPreferences", "Cleared shown place IDs")
    }

    companion object {
        private const val CURRENT_USER_ID = "current_user_id"
        private const val IS_GUEST_MODE = "is_guest_mode"
        private const val SHOWN_PLACE_IDS = "shown_place_ids"
    }
}