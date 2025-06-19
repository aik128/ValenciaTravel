package com.example.valenciatravel.domain.usecase

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OpenRouteInMapsUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(
        destinationLat: Double,
        destinationLng: Double,
        hasLocationPermission: Boolean = false
    ): Result<Boolean> {
        return try {
            val uri = if (hasLocationPermission) {
                val currentLocation = getCurrentLocation()
                if (currentLocation != null) {
                    Uri.parse("https://www.google.com/maps/dir/${currentLocation.first},${currentLocation.second}/$destinationLat,$destinationLng")
                } else {
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=$destinationLat,$destinationLng")
                }
            } else {
                Uri.parse("https://www.google.com/maps/search/?api=1&query=$destinationLat,$destinationLng")
            }

            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                Result.success(true)
            } else {
                val browserIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(browserIntent)
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getCurrentLocation(): Pair<Double, Double>? {
        return try {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            val providers = listOf(
                LocationManager.GPS_PROVIDER,
                LocationManager.NETWORK_PROVIDER
            )

            for (provider in providers) {
                if (locationManager.isProviderEnabled(provider)) {
                    val location = locationManager.getLastKnownLocation(provider)
                    if (location != null) {
                        return Pair(location.latitude, location.longitude)
                    }
                }
            }

            null
        } catch (e: Exception) {
            null
        }
    }
}

