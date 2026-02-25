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
        delay(500)

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
     * Получить последние транзакции для главного экрана
     */
    override suspend fun getRecentTransactions(limit: Int): Result<List<Transaction>> {
        delay(400)

        val transactions = listOf(
            Transaction(
                id = "1",
                title = "Продукты",
                description = "Супермаркет Пятёрочка",
                amount = -3450.0,
                category = TransactionCategory.PRODUCTS,
                date = "Сегодня",
                time = "14:30",
                type = TransactionType.EXPENSE
            ),
            Transaction(
                id = "2",
                title = "Зарплата",
                description = "Ежемесячная зарплата",
                amount = 85000.0,
                category = TransactionCategory.SALARY,
                date = "Вчера",
                time = "09:00",
                type = TransactionType.INCOME
            ),
            Transaction(
                id = "3",
                title = "Транспорт",
                description = "Заправка автомобиля",
                amount = -1200.0,
                category = TransactionCategory.TRANSPORT,
                date = "Вчера",
                time = "08:15",
                type = TransactionType.EXPENSE
            ),
            Transaction(
                id = "4",
                title = "Развлечения",
                description = "Кино с семьёй",
                amount = -2800.0,
                category = TransactionCategory.ENTERTAINMENT,
                date = "2 дня назад",
                time = "19:45",
                type = TransactionType.EXPENSE
            ),
            Transaction(
                id = "5",
                title = "Фриланс",
                description = "Разработка проекта",
                amount = 15000.0,
                category = TransactionCategory.FREELANCE,
                date = "3 дня назад",
                time = "15:00",
                type = TransactionType.INCOME
            )
        ).take(limit)

        return Result.Success(transactions)
    }

    /**
     * Полный список транзакций для экрана "Операции" с фильтрацией и поиском
     */
    override suspend fun getTransactions(
        filter: TransactionFilter,
        searchQuery: String
    ): Result<List<TransactionGroup>> {
        delay(400)

        val allTransactions = listOf(
            // Сегодня
            Transaction(
                id = "op_1",
                title = "Продукты",
                description = "Супермаркет Пятёрочка",
                amount = -3450.0,
                category = TransactionCategory.PRODUCTS,
                date = "Сегодня",
                time = "14:30",
                type = TransactionType.EXPENSE
            ),
            // Вчера
            Transaction(
                id = "op_2",
                title = "Зарплата",
                description = "Ежемесячная зарплата",
                amount = 85000.0,
                category = TransactionCategory.SALARY,
                date = "Вчера",
                time = "09:00",
                type = TransactionType.INCOME
            ),
            Transaction(
                id = "op_3",
                title = "Транспорт",
                description = "Заправка автомобиля",
                amount = -1200.0,
                category = TransactionCategory.TRANSPORT,
                date = "Вчера",
                time = "08:15",
                type = TransactionType.EXPENSE
            ),
            // 2 дня назад
            Transaction(
                id = "op_4",
                title = "Развлечения",
                description = "Кино с семьёй",
                amount = -2800.0,
                category = TransactionCategory.ENTERTAINMENT,
                date = "2 дня назад",
                time = "19:45",
                type = TransactionType.EXPENSE
            ),
            // 3 дня назад
            Transaction(
                id = "op_5",
                title = "Фриланс",
                description = "Разработка проекта",
                amount = 25000.0,
                category = TransactionCategory.FREELANCE,
                date = "3 дня назад",
                time = "15:00",
                type = TransactionType.INCOME
            ),
            Transaction(
                id = "op_6",
                title = "Одежда",
                description = "Онлайн-магазин Wildberries",
                amount = -4500.0,
                category = TransactionCategory.CLOTHING,
                date = "3 дня назад",
                time = "11:20",
                type = TransactionType.EXPENSE
            ),
            // 5 дней назад
            Transaction(
                id = "op_7",
                title = "Здоровье",
                description = "Аптека 36.6",
                amount = -1800.0,
                category = TransactionCategory.HEALTH,
                date = "5 дней назад",
                time = "10:05",
                type = TransactionType.EXPENSE
            ),
            Transaction(
                id = "op_8",
                title = "Фриланс",
                description = "Консультация клиента",
                amount = 12000.0,
                category = TransactionCategory.FREELANCE,
                date = "5 дней назад",
                time = "17:30",
                type = TransactionType.INCOME
            )
        )

        // Применяем фильтр по типу
        val filteredByType = when (filter) {
            TransactionFilter.ALL -> allTransactions
            TransactionFilter.INCOME -> allTransactions.filter { it.type == TransactionType.INCOME }
            TransactionFilter.EXPENSE -> allTransactions.filter { it.type == TransactionType.EXPENSE }
        }

        // Применяем поиск по названию и описанию
        val filtered = if (searchQuery.isBlank()) {
            filteredByType
        } else {
            filteredByType.filter { transaction ->
                transaction.title.contains(searchQuery, ignoreCase = true) ||
                transaction.description.contains(searchQuery, ignoreCase = true)
            }
        }

        // Группируем по дате (порядок сохраняется через LinkedHashMap)
        val grouped = filtered
            .groupBy { it.date }
            .map { (dateLabel, transactions) ->
                TransactionGroup(dateLabel = dateLabel, transactions = transactions)
            }

        return Result.Success(grouped)
    }
}
