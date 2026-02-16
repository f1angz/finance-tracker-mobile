package mobile.tracker.finance.ui.screens.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mobile.tracker.finance.R
import mobile.tracker.finance.ui.components.CustomTextField
import mobile.tracker.finance.ui.components.GradientBackground
import mobile.tracker.finance.ui.components.PrimaryButton
import mobile.tracker.finance.ui.theme.Black
import mobile.tracker.finance.ui.theme.PrimaryBlue
import mobile.tracker.finance.ui.theme.TextPrimary
import mobile.tracker.finance.ui.theme.TextSecondary
import mobile.tracker.finance.ui.theme.White

/**
 * Экран входа в приложение
 */
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Показать ошибку в Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    GradientBackground {
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Иконка приложения
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(PrimaryBlue, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.icon),
                            contentDescription = "App Icon",
                            tint = White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Заголовок
                    Text(
                        text = "Finance Tracker",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Подзаголовок
                    Text(
                        text = "Добро пожаловать обратно",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Белая карточка с формой
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 20.dp,
                                shape = RoundedCornerShape(16.dp),
                                ambientColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.1f),
                                spotColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.1f)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            // Поле Email
                            CustomTextField(
                                label = "Email",
                                value = uiState.email,
                                onValueChange = viewModel::onEmailChange,
                                placeholder = "ivan@example.com",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                isError = uiState.emailError.isNotEmpty(),
                                errorMessage = uiState.emailError
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Поле Пароль
                            CustomTextField(
                                label = "Пароль",
                                value = uiState.password,
                                onValueChange = viewModel::onPasswordChange,
                                placeholder = "••••••••",
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                isError = uiState.passwordError.isNotEmpty(),
                                errorMessage = uiState.passwordError
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Запомнить меня и Забыли пароль
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = uiState.rememberMe,
                                        onCheckedChange = viewModel::onRememberMeChange
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Запомнить меня",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Black
                                    )
                                }

                                Text(
                                    text = "Забыли пароль?",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = PrimaryBlue,
                                    modifier = Modifier.clickable { /* TODO: Реализовать сброс пароля */ }
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Кнопка входа
                            PrimaryButton(
                                text = "Войти",
                                onClick = { viewModel.login(onLoginSuccess) },
                                enabled = !uiState.isLoading
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Ссылка на регистрацию
                    Row {
                        Text(
                            text = "Нет аккаунта? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "Зарегистрироваться",
                            style = MaterialTheme.typography.bodyLarge,
                            color = PrimaryBlue,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }

                // Индикатор загрузки
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryBlue
                    )
                }
            }
        }
    }
}
