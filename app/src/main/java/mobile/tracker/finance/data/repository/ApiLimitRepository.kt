package mobile.tracker.finance.data.repository

import mobile.tracker.finance.data.api.RetrofitClient
import mobile.tracker.finance.data.models.Limit
import mobile.tracker.finance.data.models.LimitRequest
import mobile.tracker.finance.utils.Result
import retrofit2.Response

class ApiLimitRepository : LimitRepository {

    private val api = RetrofitClient.apiService

    override suspend fun getLimits(period: String?): Result<List<Limit>> =
        safeCall { api.getLimits(period) }

    override suspend fun createLimit(request: LimitRequest): Result<Limit> =
        safeCall { api.createLimit(request) }

    override suspend fun updateLimit(id: String, request: LimitRequest): Result<Limit> =
        safeCall { api.updateLimit(id, request) }

    override suspend fun deleteLimit(id: String): Result<Unit> =
        safeCall { api.deleteLimit(id) }

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
