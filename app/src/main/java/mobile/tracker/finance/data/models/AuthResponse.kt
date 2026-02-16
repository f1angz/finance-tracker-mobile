package mobile.tracker.finance.data.models

/**
 * Ответ от сервера при успешной аутентификации
 */
data class AuthResponse(
    val token: String,
    val user: User
)
