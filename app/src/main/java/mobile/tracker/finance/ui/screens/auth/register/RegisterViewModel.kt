package mobile.tracker.finance.ui.screens.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mobile.tracker.finance.data.repository.AuthRepository
import mobile.tracker.finance.data.repository.MockAuthRepository
import mobile.tracker.finance.utils.Result

/**
 * ViewModel для экрана регистрации
 */
class RegisterViewModel(
    // Используем мок-репозиторий для тестирования без бекенда
    private val mockAuthRepository: MockAuthRepository = MockAuthRepository()
) : ViewModel() {

    // Состояние UI
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    /**
     * Обновить имя
     */
    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(
            name = name,
            nameError = ""
        )
    }

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
     * Обновить подтверждение пароля
     */
    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = ""
        )
    }

    /**
     * Выполнить регистрацию
     */
    fun register(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        // Валидация
        if (!validateInputs()) {
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, error = null)

            // Используем мок-репозиторий (замените на authRepository, когда бекенд будет готов)
            when (val result = mockAuthRepository.register(
                currentState.name,
                currentState.email,
                currentState.password,
                currentState.confirmPassword
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

        // Валидация имени
        if (currentState.name.isBlank()) {
            _uiState.value = currentState.copy(nameError = "Введите имя")
            isValid = false
        } else if (currentState.name.length < 2) {
            _uiState.value = currentState.copy(nameError = "Имя должно содержать минимум 2 символа")
            isValid = false
        }

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

        // Валидация подтверждения пароля
        if (currentState.confirmPassword.isBlank()) {
            _uiState.value = currentState.copy(confirmPasswordError = "Подтвердите пароль")
            isValid = false
        } else if (currentState.password != currentState.confirmPassword) {
            _uiState.value = currentState.copy(confirmPasswordError = "Пароли не совпадают")
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
 * Состояние UI экрана регистрации
 */
data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val nameError: String = "",
    val emailError: String = "",
    val passwordError: String = "",
    val confirmPasswordError: String = ""
)
