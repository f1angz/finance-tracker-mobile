package mobile.tracker.finance.data.models

/**
 * Модель транзакции (операции)
 * @param id Уникальный идентификатор транзакции
 * @param title Название транзакции
 * @param amount Сумма транзакции (положительная для доходов, отрицательная для расходов)
 * @param category Категория транзакции
 * @param date Дата и время транзакции
 * @param type Тип транзакции (доход/расход)
 */
data class Transaction(
    val id: String,
    val title: String,
    val amount: Double,
    val category: TransactionCategory,
    val date: String,
    val type: TransactionType
)

/**
 * Тип транзакции
 */
enum class TransactionType {
    INCOME,  // Доход
    EXPENSE  // Расход
}

/**
 * Категория транзакции
 */
enum class TransactionCategory(val displayName: String, val color: Long) {
    PRODUCTS("Продукты", 0xFF2196F3),
    ENTERTAINMENT("Развлечение", 0xFFE91E63),
    CLOTHING("Одежда", 0xFFFFC107),
    TRANSPORT("Транспорт", 0xFF4CAF50),
    HEALTH("Здоровье", 0xFF9C27B0),
    OTHER("Прочие", 0xFF607D8B),
    SALARY("Зарплата", 0xFF4CAF50),
    FREELANCE("Фриланс", 0xFF00BCD4)
}
