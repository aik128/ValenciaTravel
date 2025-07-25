package com.example.valenciatravel.presentation.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.valenciatravel.R
import com.example.valenciatravel.presentation.theme.AppButton
import com.example.valenciatravel.presentation.theme.AppTextField
import com.example.valenciatravel.presentation.theme.GlassCard
import com.example.valenciatravel.presentation.theme.GlassEffect


@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit,
    onNavigateToMain: () -> Unit,
    onNavigateToGuest: () -> Unit
) {
    val loginState by viewModel.loginState.collectAsState()
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onNavigateToMain()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.valencia_main),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp, start = 32.dp, end = 24.dp)
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Вход",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = GlassEffect.TextColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        value = login,
                        onValueChange = { login = it },
                        label = "Логин",
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AppTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Пароль",
                        modifier = Modifier.fillMaxWidth(),
                        isPassword = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        )
                    )

                    if (loginState is LoginState.Error) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = (loginState as LoginState.Error).message,
                            color = GlassEffect.ErrorColor,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    AppButton(
                        text = "Войти",
                        onClick = { viewModel.login(login, password) },
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Login,
                        enabled = loginState !is LoginState.Loading
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = onNavigateToRegister,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = GlassEffect.WarmYellow
                        )
                    ) {
                        Text("Нет аккаунта? Зарегистрироваться")
                    }

                    TextButton(
                        onClick = {
                            viewModel.loginAsGuest()
                            onNavigateToGuest()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = GlassEffect.WarmYellow
                        )
                    ) {
                        Text("Войти как гость")
                    }

                    if (loginState is LoginState.Loading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator(color = GlassEffect.WarmYellow)
                    }
                }
            }
        }
    }
}

















