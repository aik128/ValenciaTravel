package com.example.valenciatravel.domain.usecase


import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUserCategoryWeightsUseCase @Inject constructor(
    private val getPreferencesUseCase: GetPreferencesUseCase
) {
    suspend operator fun invoke(): Map<Int, Int> {
        return try {
            getPreferencesUseCase().fold(
                onSuccess = { preferences ->
                    if (preferences != null) {
                        mapOf(
                            1 to preferences.c1,
                            2 to preferences.c2,
                            3 to preferences.c3,
                            4 to preferences.c4,
                            5 to preferences.c5,
                            6 to preferences.c6,
                            7 to preferences.c7,
                            8 to preferences.c8,
                            9 to preferences.c9,
                            10 to preferences.c10
                        )
                    } else {
                        emptyMap()
                    }
                },
                onFailure = { emptyMap() }
            )
        } catch (e: Exception) {
            emptyMap()
        }
    }
}