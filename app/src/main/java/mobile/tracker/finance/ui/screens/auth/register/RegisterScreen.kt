package mobile.tracker.finance.ui.screens.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mobile.tracker.finance.ui.components.CustomTextField
import mobile.tracker.finance.ui.components.GradientBackground
import mobile.tracker.finance.ui.components.PrimaryButton
import mobile.tracker.finance.ui.theme.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import mobile.tracker.finance.R

/**
 * Экран регистрации нового пользователя
 */
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
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
                            .size(56.dp)
                            .background(PrimaryBlue, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.icon),
                            contentDescription = "App Icon",
                            tint = White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Заголовок
                    Text(
                        text = "Finance Tracker",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Подзаголовок
                    Text(
                        text = "Создайте аккаунт для начала работы",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

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
                            // Поле Имя
                            CustomTextField(
                                label = "Имя",
                                value = uiState.name,
                                onValueChange = viewModel::onNameChange,
                                placeholder = "Иван Петров",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                isError = uiState.nameError.isNotEmpty(),
                                errorMessage = uiState.nameError
                            )

                            Spacer(modifier = Modifier.height(20.dp))

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

                            // Поле Подтвердите пароль
                            CustomTextField(
                                label = "Подтвердите пароль",
                                value = uiState.confirmPassword,
                                onValueChange = viewModel::onConfirmPasswordChange,
                                placeholder = "••••••••",
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                isError = uiState.confirmPasswordError.isNotEmpty(),
                                errorMessage = uiState.confirmPasswordError
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Кнопка регистрации
                            PrimaryButton(
                                text = "Зарегистрироваться",
                                onClick = { viewModel.register(onRegisterSuccess) },
                                enabled = !uiState.isLoading
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Ссылка на вход
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = TextSecondary)) {
                                append("Уже есть аккаунт? ")
                            }
                            withStyle(style = SpanStyle(color = PrimaryBlue)) {
                                append("Войти")
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
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
