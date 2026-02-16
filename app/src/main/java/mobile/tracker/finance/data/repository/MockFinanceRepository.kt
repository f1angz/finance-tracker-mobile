package mobile.tracker.finance.data.repository

import kotlinx.coroutines.delay
import mobile.tracker.finance.data.models.*
import mobile.tracker.finance.utils.Result

/**
 * Мок-репозиторий для работы с финансовыми данными
 * Используется для демонстрации UI с тестовыми данными
 */
class MockFinanceRepository : FinanceRepository {

    /**
     * Получить статистику финансов с мок-данными
     */
    override suspend fun getFinanceStats(): Result<FinanceStats> {
        delay(500) // Имитация сетевого запроса

        return Result.Success(
            FinanceStats(
                balance = 245680.0,
                balanceChange = 12.5,
                income = 320450.0,
                incomeChange = 8.2,
                expense = 74770.0,
                expenseChange = -1.1,
                savings = 245680.0,
                savingsChange = 15.8
            )
        )
    }

    /**
     * Получить расходы по категориям с мок-данными
     */
    override suspend fun getCategoryExpenses(): Result<List<CategoryExpense>> {
        delay(300)

        val expenses = listOf(
            CategoryExpense(TransactionCategory.PRODUCTS, 25000.0, 33.4f),
            CategoryExpense(TransactionCategory.ENTERTAINMENT, 15000.0, 20.1f),
            CategoryExpense(TransactionCategory.CLOTHING, 8000.0, 10.7f),
            CategoryExpense(TransactionCategory.TRANSPORT, 18000.0, 24.1f),
            CategoryExpense(TransactionCategory.HEALTH, 6000.0, 8.0f),
            CategoryExpense(TransactionCategory.OTHER, 2770.0, 3.7f)
        )

        return Result.Success(expenses)
    }

    /**
     * Получить статистику по месяцам с мок-данными
     */
    override suspend fun getMonthlyStats(): Result<List<MonthlyStats>> {
        delay(300)

        val stats = listOf(
            MonthlyStats("Янв", 280000.0, 65000.0),
            MonthlyStats("Фев", 290000.0, 70000.0),
            MonthlyStats("Мар", 310000.0, 68000.0),
            MonthlyStats("Апр", 305000.0, 72000.0),
            MonthlyStats("Май", 320000.0, 75000.0)
        )

        return Result.Success(stats)
    }

    /**
     * Получить последние транзакции с мок-данными
     */
    override suspend fun getRecentTransactions(limit: Int): Result<List<Transaction>> {
        delay(400)

        val transactions = listOf(
            Transaction(
                id = "1",
                title = "Продукты",
                amount = -3450.0,
                category = TransactionCategory.PRODUCTS,
                date = "Вчера, 15:30",
                type = TransactionType.EXPENSE
            ),
            Transaction(
                id = "2",
                title = "Зарплата",
                amount = 95000.0,
                category = TransactionCategory.SALARY,
                date = "Вчера, 09:00",
                type = TransactionType.INCOME
            ),
            Transaction(
                id = "3",
                title = "Транспорт",
                amount = -1200.0,
                category = TransactionCategory.TRANSPORT,
                date = "Вчера, 08:15",
                type = TransactionType.EXPENSE
            ),
            Transaction(
                id = "4",
                title = "Развлечения",
                amount = -2800.0,
                category = TransactionCategory.ENTERTAINMENT,
                date = "2 дня назад",
                type = TransactionType.EXPENSE
            ),
            Transaction(
                id = "5",
                title = "Фриланс",
                amount = 15000.0,
                category = TransactionCategory.FREELANCE,
                date = "3 дня назад",
                type = TransactionType.INCOME
            )
        ).take(limit)

        return Result.Success(transactions)
    }
}
