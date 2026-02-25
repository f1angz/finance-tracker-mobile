package mobile.tracker.finance.data.repository

import mobile.tracker.finance.data.models.*
import mobile.tracker.finance.utils.Result

/**
 * Интерфейс репозитория для работы с финансовыми данными
 */
interface FinanceRepository {
    /**
     * Получить статистику финансов
     */
    suspend fun getFinanceStats(): Result<FinanceStats>

    /**
     * Получить расходы по категориям
     */
    suspend fun getCategoryExpenses(): Result<List<CategoryExpense>>

    /**
     * Получить статистику по месяцам
     */
    suspend fun getMonthlyStats(): Result<List<MonthlyStats>>

    /**
     * Получить последние транзакции (для главного экрана)
     * @param limit Количество транзакций
     */
    suspend fun getRecentTransactions(limit: Int = 10): Result<List<Transaction>>

    /**
     * Получить все транзакции с фильтрацией и поиском (для экрана Операции)
     * @param filter Тип фильтра (все / доходы / расходы)
     * @param searchQuery Строка поиска по названию или описанию
     */
    suspend fun getTransactions(
        filter: TransactionFilter = TransactionFilter.ALL,
        searchQuery: String = ""
    ): Result<List<TransactionGroup>>
}
