package mobile.tracker.finance.data.models

/**
 * Фильтр для списка транзакций
 */
enum class TransactionFilter {
    ALL,     // Все операции
    INCOME,  // Только доходы
    EXPENSE  // Только расходы
}
