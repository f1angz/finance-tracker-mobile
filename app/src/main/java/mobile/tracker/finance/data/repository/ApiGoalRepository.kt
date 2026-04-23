package mobile.tracker.finance.data.repository

import mobile.tracker.finance.data.api.RetrofitClient
import mobile.tracker.finance.data.models.CreateGoalRequest
import mobile.tracker.finance.data.models.Goal
import mobile.tracker.finance.data.models.GoalContributionRequest
import mobile.tracker.finance.utils.Result
import retrofit2.Response

class ApiGoalRepository : GoalRepository {

    private val api = RetrofitClient.apiService

    override suspend fun getGoals(): Result<List<Goal>> = safeCall { api.getGoals() }

    override suspend fun createGoal(request: CreateGoalRequest): Result<Goal> =
        safeCall { api.createGoal(request) }

    override suspend fun addContribution(
        goalId: String,
        request: GoalContributionRequest
    ): Result<Goal> = safeCall { api.addGoalContribution(goalId, request) }

    override suspend fun deleteGoal(id: String): Result<Unit> = safeCall { api.deleteGoal(id) }

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
