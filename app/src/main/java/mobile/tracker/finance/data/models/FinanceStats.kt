package mobile.tracker.finance.data.models

/**
 * Модель финансовой статистики
 * @param balance Текущий баланс
 * @param balanceChange Процентное изменение баланса
 * @param income Общий доход
 * @param incomeChange Процентное изменение дохода
 * @param expense Общий расход
 * @param expenseChange Процентное изменение расхода
 * @param savings Сбережения
 * @param savingsChange Процентное изменение сбережений
 */
data class FinanceStats(
    val balance: Double,
    val balanceChange: Double,
    val income: Double,
    val incomeChange: Double,
    val expense: Double,
    val expenseChange: Double,
    val savings: Double,
    val savingsChange: Double
)

/**
 * Модель данных для круговой диаграммы расходов по категориям
 * @param category Категория
 * @param amount Сумма расходов
 * @param percentage Процент от общих расходов
 */
data class CategoryExpense(
    val category: TransactionCategory,
    val amount: Double,
    val percentage: Float
)

/**
 * Модель данных для столбчатой диаграммы по месяцам
 * @param month Месяц
 * @param income Доход за месяц
 * @param expense Расход за месяц
 */
data class MonthlyStats(
    val month: String,
    val income: Double,
    val expense: Double
)
