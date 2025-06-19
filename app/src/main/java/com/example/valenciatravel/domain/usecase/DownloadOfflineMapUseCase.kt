package com.example.valenciatravel.domain.usecase

import android.content.Context
import android.content.Intent
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DownloadOfflineMapUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(
        centerLat: Double = 39.4699,
        centerLng: Double = -0.3763,
        zoomLevel: Int = 10
    ): Result<String> {
        return try {
            val mapsUri = Uri.parse("geo:$centerLat,$centerLng?z=$zoomLevel")
            val intent = Intent(Intent.ACTION_VIEW, mapsUri).apply {
                setPackage("com.google.android.apps.maps")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                Result.success("Профиль → Оффлайн-карты → Выберите свою карту → Скачать")
            } else {
                Result.failure(Exception("Google Maps не установлен"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}