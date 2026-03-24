package mobile.tracker.finance.ui.screens.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mobile.tracker.finance.data.repository.AuthRepository
import mobile.tracker.finance.utils.Result

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, emailError = "")
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, passwordError = "")
    }

    fun onRememberMeChange(rememberMe: Boolean) {
        _uiState.value = _uiState.value.copy(rememberMe = rememberMe)
    }

    fun login(onSuccess: () -> Unit) {
        if (!validateInputs()) return

        val currentState = _uiState.value
        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, error = null)

            when (val result = authRepository.login(
                currentState.email,
                currentState.password,
                currentState.rememberMe
            )) {
                is Result.Success -> {
                    _uiState.value = currentState.copy(isLoading = false, error = null)
                    onSuccess()
                }
                is Result.Error -> {
                    _uiState.value = currentState.copy(isLoading = false, error = result.message)
                }
                is Result.Loading -> Unit
            }
        }
    }

    private fun validateInputs(): Boolean {
        val s = _uiState.value
        var isValid = true

        if (s.email.isBlank()) {
            _uiState.value = s.copy(emailError = "Введите email")
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.email).matches()) {
            _uiState.value = s.copy(emailError = "Неверный формат email")
            isValid = false
        }

        if (s.password.isBlank()) {
            _uiState.value = s.copy(passwordError = "Введите пароль")
            isValid = false
        } else if (s.password.length < 6) {
            _uiState.value = s.copy(passwordError = "Пароль должен содержать минимум 6 символов")
            isValid = false
        }

        return isValid
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val emailError: String = "",
    val passwordError: String = ""
)
