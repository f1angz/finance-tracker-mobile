package mobile.tracker.finance.ui.screens.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mobile.tracker.finance.data.models.AuthResponse
import mobile.tracker.finance.data.repository.AuthRepository
import mobile.tracker.finance.data.repository.MockAuthRepository
import mobile.tracker.finance.utils.Result

/**
 * ViewModel для экрана входа
 */
class LoginViewModel(
    // Используем мок-репозиторий для тестирования без бекенда
    private val mockAuthRepository: MockAuthRepository = MockAuthRepository()
) : ViewModel() {

    // Состояние UI
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Обновить email
     */
    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = ""
        )
    }

    /**
     * Обновить пароль
     */
    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = ""
        )
    }

    /**
     * Переключить "Запомнить меня"
     */
    fun onRememberMeChange(rememberMe: Boolean) {
        _uiState.value = _uiState.value.copy(rememberMe = rememberMe)
    }

    /**
     * Выполнить вход
     */
    fun login(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        // Валидация
        if (!validateInputs()) {
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, error = null)

            // Используем мок
            when (val result = mockAuthRepository.login(
                currentState.email,
                currentState.password,
                currentState.rememberMe
            )) {
                is Result.Success -> {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        error = null
                    )
                    onSuccess()
                }
                is Result.Error -> {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Result.Loading -> {
                    // Уже в состоянии загрузки
                }
            }
        }
    }

    /**
     * Валидация полей
     */
    private fun validateInputs(): Boolean {
        val currentState = _uiState.value
        var isValid = true

        // Валидация email
        if (currentState.email.isBlank()) {
            _uiState.value = currentState.copy(emailError = "Введите email")
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            _uiState.value = currentState.copy(emailError = "Неверный формат email")
            isValid = false
        }

        // Валидация пароля
        if (currentState.password.isBlank()) {
            _uiState.value = currentState.copy(passwordError = "Введите пароль")
            isValid = false
        } else if (currentState.password.length < 6) {
            _uiState.value = currentState.copy(passwordError = "Пароль должен содержать минимум 6 символов")
            isValid = false
        }

        return isValid
    }

    /**
     * Очистить ошибку
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * Состояние UI экрана входа
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val emailError: String = "",
    val passwordError: String = ""
)
