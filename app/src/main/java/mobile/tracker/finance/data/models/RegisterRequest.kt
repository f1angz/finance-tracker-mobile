package mobile.tracker.finance.data.models

/**
 * Запрос для регистрации нового пользователя
 */
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)
