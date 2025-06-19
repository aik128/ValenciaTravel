package com.example.valenciatravel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.valenciatravel.domain.usecase.LoadPlacesFromJsonUseCase
import com.example.valenciatravel.presentation.navigation.NavGraph
import com.example.valenciatravel.presentation.theme.GuideTheme
import com.google.android.gms.maps.MapsInitializer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var loadPlacesFromJsonUseCase: LoadPlacesFromJsonUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GuideTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            loadPlacesFromJsonUseCase()
        }

        try {
            MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST) { result ->
                when (result) {
                    MapsInitializer.Renderer.LATEST -> {
                    }
                    MapsInitializer.Renderer.LEGACY -> {
                    }
                }
            }
        } catch (e: Exception) {
        }

    }
}