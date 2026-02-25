package mobile.tracker.finance.data.models

/**
 * Модель финансовой цели
 */
data class Goal(
    val id: String,
    val emoji: String,
    val title: String,
    val daysLeft: Int,
    val savedAmount: Double,
    val targetAmount: Double,
    /** Цвет акцента в hex без '#', например "3B82F6" */
    val accentColor: String
) {
    val remainingAmount: Double
        get() = (targetAmount - savedAmount).coerceAtLeast(0.0)

    val progressFraction: Float
        get() = (savedAmount / targetAmount).toFloat().coerceIn(0f, 1f)

    val percentFormatted: String
        get() = "${(progressFraction * 100).toInt()}%"
}

/** DTO для создания новой цели */
data class CreateGoalRequest(
    val emoji: String,
    val title: String,
    val targetAmount: Double,
    /** Целевая дата в формате "YYYY-MM-DD" */
    val targetDate: String
)

/** DTO для пополнения цели */
data class GoalContributionRequest(
    val amount: Double
)
