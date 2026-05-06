package mobile.tracker.finance.data.repository

import mobile.tracker.finance.data.api.toDomain
import mobile.tracker.finance.data.api.toApiDto
import mobile.tracker.finance.data.api.RetrofitClient
import mobile.tracker.finance.data.models.*
import mobile.tracker.finance.utils.Result

class ApiFinanceRepository : FinanceRepository {

    private val api = RetrofitClient.apiService

    override suspend fun getFinanceStats(month: String?): Result<FinanceStats> = safeCall {
        api.getFinanceStats(month)
    }

    override suspend fun getCategoryExpenses(month: String?): Result<List<CategoryExpense>> {
        return try {
            val response = api.getCategoryExpenses(month)
            if (response.isSuccessful) {
                response.body()?.let { dtos ->
                    val merged = dtos
                        .map { it.toDomain() }
                        .groupBy { it.categoryName }
                        .map { (_, items) ->
                            items.first().copy(
                                amount     = items.sumOf { it.amount },
                                percentage = items.sumOf { it.percentage.toDouble() }.toFloat()
                            )
                        }
                        .sortedByDescending { it.amount }
                    Result.Success(merged)
                } ?: Result.Error("Пустой ответ от сервера")
            } else {
                Result.Error("Ошибка ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Ошибка подключения: ${e.message}")
        }
    }

    override suspend fun getMonthlyStats(): Result<List<MonthlyStats>> = safeCall {
        api.getMonthlyStats()
    }

    override suspend fun getRecentTransactions(limit: Int): Result<List<Transaction>> {
        return try {
            val response = api.getTransactions(limit = limit)
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it.map { dto -> dto.toDomain() }) }
                    ?: Result.Error("Пустой ответ от сервера")
            } else {
                Result.Error("Ошибка ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Ошибка подключения: ${e.message}")
        }
    }

    override suspend fun getTransactions(
        filter: TransactionFilter,
        searchQuery: String
    ): Result<List<TransactionGroup>> {
        return try {
            val type = when (filter) {
                TransactionFilter.ALL     -> null
                TransactionFilter.INCOME  -> "INCOME"
                TransactionFilter.EXPENSE -> "EXPENSE"
            }
            val response = api.getTransactions(
                type = type,
                search = searchQuery.ifBlank { null }
            )
            if (response.isSuccessful) {
                response.body()?.let { dtos ->
                    val grouped = dtos
                        .map { it.toDomain() }
                        .groupBy { it.date }
                        .map { (label, txs) -> TransactionGroup(dateLabel = label, transactions = txs) }
                    Result.Success(grouped)
                } ?: Result.Error("Пустой ответ от сервера")
            } else {
                Result.Error("Ошибка ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Ошибка подключения: ${e.message}")
        }
    }

    override suspend fun addTransaction(transaction: Transaction) {
        try {
            api.createTransaction(transaction.toApiDto())
        } catch (_: Exception) {}
    }

    override suspend fun deleteTransaction(id: String) {
        try {
            api.deleteTransaction(id)
        } catch (_: Exception) {}
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private suspend fun <T> safeCall(block: suspend () -> retrofit2.Response<T>): Result<T> {
        return try {
            val response = block()
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Пустой ответ от сервера")
            } else {
                Result.Error("Ошибка ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Ошибка подключения: ${e.message}")
        }
    }
}
