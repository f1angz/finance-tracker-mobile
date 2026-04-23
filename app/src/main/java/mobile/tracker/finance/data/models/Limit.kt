package mobile.tracker.finance.data.models

/**
 * Модель лимита расходов по категории
 */
data class Limit(
    val id: String,
    val categorySlug: String,
    val categoryName: String,
    val period: String,           // Например, "Февраль 2026"
    val limitAmount: Double,
    val spentAmount: Double
) {
    val remainingAmount: Double
        get() = (limitAmount - spentAmount).coerceAtLeast(0.0)

    val progressFraction: Float
        get() = (spentAmount / limitAmount).toFloat().coerceIn(0f, 1f)

    val percentFormatted: String
        get() = "%.1f%%".format(progressFraction * 100)

    val status: LimitStatus
        get() = when {
            progressFraction >= 1f   -> LimitStatus.EXCEEDED
            progressFraction >= 0.8f -> LimitStatus.CLOSE
            else                     -> LimitStatus.OK
        }
}

enum class LimitStatus { OK, CLOSE, EXCEEDED }

/**
 * Тело запроса для создания / обновления лимита
 */
data class LimitRequest(
    val categorySlug: String,
    val amount: Double,
    val period: String   // Формат "YYYY-MM", например "2026-02"
)
