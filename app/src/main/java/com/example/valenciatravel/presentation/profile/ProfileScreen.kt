package com.example.valenciatravel.presentation.profile


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.valenciatravel.R
import com.example.valenciatravel.presentation.theme.AppButton
import com.example.valenciatravel.presentation.theme.GlassCard
import com.example.valenciatravel.presentation.theme.GlassEffect

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToPreferences: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToMain: () -> Unit,
    onDeleteAccount: () -> Unit
) {

    val isAuthorized = remember { viewModel.isUserAuthorized() }

    val message by viewModel.message.collectAsState()
    val context = LocalContext.current



    message?.let { msg ->
        LaunchedEffect(msg) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    if (isAuthorized) {

        val userState by viewModel.userState.collectAsState()
        val deleteState by viewModel.deleteState.collectAsState()
        val favoriteCount by viewModel.favoriteCount.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.refreshFavoriteCount()
        }

        LaunchedEffect(deleteState) {
            if (deleteState is DeleteUserState.Success) {
                onDeleteAccount()
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_background_3),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(0.5f))

                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Добро пожаловать, ",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = GlassEffect.TextColor
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        userState?.let { user ->
                            Text(
                                text = user.login,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Light,
                                fontStyle = FontStyle.Italic,
                                textAlign = TextAlign.Center,
                                color = GlassEffect.TextColor
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "У вас в избранных $favoriteCount мест",
                                style = MaterialTheme.typography.bodyLarge,
                                color = GlassEffect.WarmYellow,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(0.6f))

                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val buttonSpacing = 18.dp

                        AppButton(
                            text = "Изменить оценки категорий",
                            onClick = onNavigateToPreferences,
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Default.Settings
                        )

                        Spacer(modifier = Modifier.height(buttonSpacing))

                        AppButton(
                            text = "Открыть избранное",
                            onClick = onNavigateToFavorites,
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Default.Bookmarks
                        )

                        Spacer(modifier = Modifier.height(buttonSpacing))

                        AppButton(
                            text = "Скачать оффлайн карты",
                            onClick = { viewModel.downloadOfflineMap() },
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Default.Download
                        )

                        Spacer(modifier = Modifier.height(buttonSpacing))

                        AppButton(
                            text = "Выйти из аккаунта",
                            onClick = { viewModel.logout() },
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Default.Logout
                        )

                        Spacer(modifier = Modifier.height(buttonSpacing))

                        AppButton(
                            text = "Удалить аккаунт",
                            onClick = { viewModel.deleteAccount() },
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Default.Delete,
                            isDestructive = true
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.2f))

                if (deleteState is DeleteUserState.Loading) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(
                                color = GlassEffect.WarmYellow
                            )
                        }
                    }
                }

                if (deleteState is DeleteUserState.Error) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(
                            text = (deleteState as DeleteUserState.Error).message,
                            color = GlassEffect.ErrorColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    } else {
        GuestProfileScreen(
            onLoginClick = onNavigateToMain,
            onDownloadMapsClick = { viewModel.downloadOfflineMap() }
        )
    }


}

@Composable
private fun GuestProfileScreen(
    onLoginClick: () -> Unit,
    onDownloadMapsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Гостевой режим",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Войдите в аккаунт для полного доступа",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onDownloadMapsClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Скачать оффлайн карты",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Войти в аккаунт",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}