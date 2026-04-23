package mobile.tracker.finance.data.repository

import mobile.tracker.finance.data.api.RetrofitClient
import mobile.tracker.finance.data.models.CreateDebtRequest
import mobile.tracker.finance.data.models.Debt
import mobile.tracker.finance.utils.Result
import retrofit2.Response

class ApiDebtRepository : DebtRepository {

    private val api = RetrofitClient.apiService

    override suspend fun getDebts(): Result<List<Debt>> = safeCall { api.getDebts() }

    override suspend fun createDebt(request: CreateDebtRequest): Result<Debt> =
        safeCall { api.createDebt(request) }

    override suspend fun markAsPaid(id: String): Result<Debt> =
        safeCall { api.markDebtAsPaid(id) }

    override suspend fun deleteDebt(id: String): Result<Unit> = safeCall { api.deleteDebt(id) }

    private suspend fun <T> safeCall(block: suspend () -> Response<T>): Result<T> {
        return try {
            val response = block()
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: if (response.code() == 204) @Suppress("UNCHECKED_CAST") Result.Success(Unit as T)
                    else Result.Error("Пустой ответ от сервера")
            } else {
                Result.Error("Ошибка ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Ошибка подключения: ${e.message}")
        }
    }
}
