package com.example.valenciatravel.presentation.feed


import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.valenciatravel.R
import com.example.valenciatravel.domain.model.Place
import com.example.valenciatravel.presentation.UnauthorizedScreen
import com.example.valenciatravel.presentation.feed.FeedViewModel.PlaceInteractionState
import kotlinx.coroutines.delay

@Composable
fun FeedScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToPlaceDetails: (Long) -> Unit,
    viewModel: FeedViewModel = hiltViewModel()
) {
    var isAuthorized by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isAuthorized = viewModel.isUserAuthorized()
    }

    if (isAuthorized) {
        AuthorizedFeedScreen(
            onNavigateToPlaceDetails = onNavigateToPlaceDetails,
            viewModel = viewModel
        )
    } else {
        UnauthorizedScreen(
            title = "Для доступа к этому экрану авторизуйтесь",
            onLoginClick = onNavigateToLogin
        )
    }
}

@Composable
private fun AuthorizedFeedScreen(
    onNavigateToPlaceDetails: (Long) -> Unit,
    viewModel: FeedViewModel
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val placeStates by viewModel.currentPlaceStates.collectAsState()

    state.error?.let { error ->
        LaunchedEffect(error) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.places.isEmpty() -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Нет мест для показа",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            else -> {
                val currentPlace = state.places[state.currentIndex]
                val currentPlaceState = placeStates[currentPlace.id] ?: PlaceInteractionState()

                PlaceFeedCard(
                    place = currentPlace,
                    placeState = currentPlaceState,
                    onDetailsClick = { onNavigateToPlaceDetails(currentPlace.id) },
                    onSwipeUp = { viewModel.nextPlace() },
                    onSwipeDown = { viewModel.previousPlace() },
                    onLikeClick = { viewModel.likeCurrentPlace() },
                    onDislikeClick = { viewModel.dislikeCurrentPlace() },
                    onBookmarkClick = { viewModel.toggleFavoriteCurrentPlace() }
                )
            }
        }
    }
}

@Composable
private fun PlaceFeedCard(
    place: Place,
    placeState: PlaceInteractionState,
    onDetailsClick: () -> Unit,
    onSwipeUp: () -> Unit,
    onSwipeDown: () -> Unit,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }

    val animatedOffset by animateFloatAsState(
        targetValue = if (isAnimating) dragOffset else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        finishedListener = {
            isAnimating = false
            dragOffset = 0f
        }
    )

    val cardAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500)
    )

    val cardScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationY = animatedOffset
                alpha = cardAlpha
                scaleX = cardScale
                scaleY = cardScale
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isAnimating = false
                    },
                    onDragEnd = {
                        isAnimating = true
                        if (dragOffset < -150) {
                            onSwipeUp()
                        } else if (dragOffset > 150) {
                            onSwipeDown()
                        }
                    }
                ) { change, dragAmount ->
                    if (!isAnimating) {
                        dragOffset += dragAmount.y
                        dragOffset = dragOffset.coerceIn(-300f, 300f)
                    }
                }
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PlaceImage(
                        imageName = place.imageLinks.getOrNull(0) ?: "",
                        placeName = place.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )

                    PlaceImage(
                        imageName = place.imageLinks.getOrNull(1) ?: place.imageLinks.getOrNull(0)
                        ?: "",
                        placeName = place.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = place.category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = place.sText,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onDetailsClick,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(28.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp
                    )
                ) {
                    Text(
                        text = "Подробнее",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActionButton(
                icon = if (placeState.isLiked) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                contentDescription = "Нравится",
                isActive = placeState.isLiked,
                onClick = onLikeClick
            )

            ActionButton(
                icon = if (placeState.isDisliked) Icons.Default.ThumbDown else Icons.Default.ThumbDownOffAlt,
                contentDescription = "Не нравится",
                isActive = placeState.isDisliked,
                onClick = onDislikeClick
            )

            ActionButton(
                icon = if (placeState.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = if (placeState.isFavorite) "Убрать из избранного" else "Добавить в избранное",
                isActive = placeState.isFavorite,
                onClick = onBookmarkClick
            )
        }
    }
}

@Composable
private fun PlaceImage(
    imageName: String,
    placeName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
        contentDescription = placeName,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    contentDescription: String,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        )
    )

    val containerColor = if (isActive) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    }

    val contentColor = if (isActive) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    FloatingActionButton(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .size(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp)
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}


