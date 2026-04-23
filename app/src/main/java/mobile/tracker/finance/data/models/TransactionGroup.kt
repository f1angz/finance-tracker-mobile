package mobile.tracker.finance.data.models

/**
 * Группа транзакций по дате
 * @param dateLabel Метка даты (например, "Сегодня", "Вчера", "2 дня назад")
 * @param transactions Список транзакций в этой группе
 */
data class TransactionGroup(
    val dateLabel: String,
    val transactions: List<Transaction>
)
