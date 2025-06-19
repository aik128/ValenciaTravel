package com.example.valenciatravel.presentation.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen(
    viewModel: PreferencesViewModel = hiltViewModel(),
    onNavigateToMain: () -> Unit
) {

    val preferencesState by viewModel.preferencesState.collectAsState()
    val likedCategories by viewModel.likedCategories.collectAsState()
    val dislikedCategories by viewModel.dislikedCategories.collectAsState()
    val saveState by viewModel.saveState.collectAsState()

    val scrollState = rememberScrollState()

    LaunchedEffect(saveState) {
        if (saveState is SaveState.Success) {
            onNavigateToMain()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .statusBarsPadding()
    ) {

        Text(
            text = "Оцените категории",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )

        when (preferencesState) {
            is PreferencesState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is PreferencesState.Success -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .verticalScroll(scrollState)
                    ) {
                        for (index in 0 until 10) {
                            val categoryId = index + 1
                            val categoryName = viewModel.getCategoryName(categoryId) ?: "Категория $categoryId"
                            val isLiked = likedCategories.contains(categoryId)
                            val isDisliked = dislikedCategories.contains(categoryId)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = categoryName,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 22.sp
                                    ),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.likeCategory(categoryId) },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.ThumbUp,
                                            contentDescription = "Нравится",
                                            tint = if (isLiked) Color.Green else Color.Gray.copy(alpha = 0.6f),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.dislikeCategory(categoryId) },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.ThumbDown,
                                            contentDescription = "Не нравится",
                                            tint = if (isDisliked) Color.Red else Color.Gray.copy(alpha = 0.6f),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }

                            if (index < 9) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }

                    if (scrollState.maxValue > 0) {
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .fillMaxHeight()
                                .align(Alignment.CenterEnd)
                                .padding(end = 2.dp)
                                .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(1.5.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .fillMaxHeight(0.2f)
                                    .align(Alignment.TopCenter)
                                    .offset(
                                        y = (scrollState.value.toFloat() / scrollState.maxValue.toFloat() *
                                                0.3f * scrollState.viewportSize.toFloat() * 0.98f).dp
                                    )
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                        RoundedCornerShape(1.5.dp)
                                    )
                            )
                        }
                    }
                }
            }
            is PreferencesState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = (preferencesState as PreferencesState.Error).message,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.loadPreferences() }
                        ) {
                            Text("Повторить попытку")
                        }
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.savePreferences() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(vertical = 16.dp),
            enabled = saveState !is SaveState.Loading
        ) {
            if (saveState is SaveState.Loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "Сохранить",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}