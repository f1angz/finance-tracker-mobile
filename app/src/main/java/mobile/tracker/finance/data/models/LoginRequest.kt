package mobile.tracker.finance.data.models

/**
 * Запрос для входа в систему
 */
data class LoginRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean = false
)
