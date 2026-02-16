package mobile.tracker.finance.data.models

/**
 * Модель ошибки API
 */
data class ApiError(
    val message: String,
    val code: Int? = null
)
