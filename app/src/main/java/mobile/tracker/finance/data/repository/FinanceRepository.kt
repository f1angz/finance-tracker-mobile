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
     * Получить последние транзакции
     * @param limit Количество транзакций
     */
    suspend fun getRecentTransactions(limit: Int = 10): Result<List<Transaction>>
}
