package com.example.valenciatravel.presentation.details


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.valenciatravel.R
import com.example.valenciatravel.data.util.rememberLocationPermissionLauncher
import com.example.valenciatravel.domain.model.Place


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailsScreen(
    placeId: Long,
    onBackClick: () -> Unit,
    viewModel: PlaceDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val selectedRating by viewModel.selectedRating.collectAsState()

    val requestLocationPermission = rememberLocationPermissionLauncher { granted ->
        if (granted) {
            viewModel.onLocationPermissionGranted()
        } else {
            viewModel.onLocationPermissionDenied()
        }
    }

    LaunchedEffect(placeId) {
        viewModel.loadPlaceDetails(placeId)
    }

    LaunchedEffect(viewModel.shouldRequestLocationPermission) {
        if (viewModel.shouldRequestLocationPermission) {
            requestLocationPermission()
            viewModel.onLocationPermissionRequested()
        }
    }

    state.error?.let { error ->
        val context = LocalContext.current
        LaunchedEffect(error) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = state.place?.category ?: "Загрузка...",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold

                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.place != null -> {
                    PlaceDetailsContent(
                        place = state.place!!,
                        selectedRating = selectedRating,
                        isFavorite = state.isFavorite,
                        isUserAuthorized = viewModel.isUserAuthorized,
                        onRatePlace = { rating ->
                            viewModel.ratePlace(rating)
                        },
                        onToggleFavorite = { viewModel.toggleFavorite() },
                        onOpenRoute = { lat, lng ->
                            viewModel.requestRouteWithPermission(lat, lng)
                        }
                    )
                }

                state.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.error!!,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )

                        Button(
                            onClick = { viewModel.loadPlaceDetails(placeId) }
                        ) {
                            Text("Повторить")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceDetailsContent(
    place: Place,
    selectedRating: Int,
    isFavorite: Boolean,
    isUserAuthorized: Boolean,
    onRatePlace: (Int) -> Unit,
    onToggleFavorite: () -> Unit,
    onOpenRoute: (Double, Double) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            val context = LocalContext.current
            val pagerState = rememberPagerState(pageCount = { place.imageLinks.size })

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                ) { page ->
                    val imageName = place.imageLinks[page]
                    val imageResourceId = context.resources.getIdentifier(
                        imageName,
                        "drawable",
                        context.packageName
                    )

                    Image(
                        painter = if (imageResourceId != 0) {
                            painterResource(imageResourceId)
                        } else {
                            painterResource(R.drawable.placeholder_image)
                        },
                        contentDescription = "${place.name} - изображение ${page + 1}",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentScale = ContentScale.Fit
                    )
                }

                if (place.imageLinks.size > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(place.imageLinks.size) { index ->
                            Box(
                                modifier = Modifier
                                    .size(
                                        width = 24.dp,
                                        height = 4.dp
                                    )
                                    .background(
                                        color = if (index == pagerState.currentPage) {
                                            Color.White
                                        } else {
                                            Color.White.copy(alpha = 0.4f)
                                        },
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        onOpenRoute(place.latitude, place.longitude)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = "Проложить маршрут",
                        modifier = Modifier.size(28.dp)
                    )
                }

                if (isUserAuthorized) {
                    IconButton(
                        onClick = onToggleFavorite
                    ) {
                        Icon(
                            imageVector = if (isFavorite) {
                                Icons.Default.Bookmark
                            } else {
                                Icons.Default.BookmarkBorder
                            },
                            contentDescription = if (isFavorite) {
                                "Убрать из избранного"
                            } else {
                                "Добавить в избранное"
                            },
                            tint = if (isFavorite) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                LocalContentColor.current
                            },
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {

                    Text(
                        text = "    ${place.text}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        }

        if (isUserAuthorized) {
            item {
                RatingSection(
                    selectedRating = selectedRating,
                    onRatePlace = onRatePlace
                )
            }

        }
    }
}

@Composable
private fun RatingSection(
    selectedRating: Int,
    onRatePlace: (Int) -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Оцените это место",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(5) { index ->
                    val starIndex = index + 1
                    IconButton(
                        onClick = {
                            onRatePlace(starIndex)
                        }
                    ) {
                        Icon(
                            imageVector = if (starIndex <= selectedRating) {
                                Icons.Default.Star
                            } else {
                                Icons.Default.StarBorder
                            },
                            contentDescription = "Звезда $starIndex",
                            tint = if (starIndex <= selectedRating) {
                                Color(0xFFFFD700)
                            } else {
                                MaterialTheme.colorScheme.outline
                            },
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}