package mobile.tracker.finance.data.models

/**
 * Тип долгового обязательства
 */
enum class DebtType {
    /** Я должен другому человеку */
    I_OWE,
    /** Мне должны */
    THEY_OWE
}

/**
 * Модель долгового обязательства
 */
data class Debt(
    val id: String,
    val personName: String,
    val type: DebtType,
    val amount: Double,
    /** Срок возврата в формате "YYYY-MM-DD" */
    val dueDate: String,
    val isPaid: Boolean = false
)

/** DTO для создания нового долга */
data class CreateDebtRequest(
    val personName: String,
    val type: DebtType,
    val amount: Double,
    val dueDate: String
)
