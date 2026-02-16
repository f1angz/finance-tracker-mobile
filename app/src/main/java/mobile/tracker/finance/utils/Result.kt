package mobile.tracker.finance.utils

/**
 * Sealed класс для обработки результатов операций
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
