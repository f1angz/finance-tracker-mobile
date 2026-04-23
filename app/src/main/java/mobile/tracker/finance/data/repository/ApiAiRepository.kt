package mobile.tracker.finance.data.repository

import mobile.tracker.finance.data.api.RetrofitClient
import mobile.tracker.finance.data.models.AiHealthScore
import mobile.tracker.finance.data.models.AiInsight
import mobile.tracker.finance.data.models.AiTip
import mobile.tracker.finance.data.models.ChatRequest
import mobile.tracker.finance.data.models.ChatResponse
import mobile.tracker.finance.utils.Result

class ApiAiRepository : AiRepository {

    private val api = RetrofitClient.apiService

    override suspend fun getHealthScore(): Result<AiHealthScore> = safeCall {
        val response = api.getAiHealthScore()
        if (!response.isSuccessful) return Result.Error("HTTP ${response.code()}: ${response.message()}")
        Result.Success(response.body() ?: return Result.Error("Пустой ответ сервера"))
    }

    override suspend fun getInsights(): Result<List<AiInsight>> = safeCall {
        val response = api.getAiInsights()
        if (!response.isSuccessful) return Result.Error("HTTP ${response.code()}: ${response.message()}")
        Result.Success(response.body() ?: return Result.Error("Пустой ответ сервера"))
    }

    override suspend fun getTips(): Result<List<AiTip>> = safeCall {
        val response = api.getAiTips()
        if (!response.isSuccessful) return Result.Error("HTTP ${response.code()}: ${response.message()}")
        Result.Success(response.body() ?: return Result.Error("Пустой ответ сервера"))
    }

    override suspend fun sendMessage(request: ChatRequest): Result<ChatResponse> = safeCall {
        val response = api.aiChat(request)
        if (!response.isSuccessful) return Result.Error("HTTP ${response.code()}: ${response.message()}")
        Result.Success(response.body() ?: return Result.Error("Пустой ответ сервера"))
    }

    private inline fun <T> safeCall(block: () -> Result<T>): Result<T> = try {
        block()
    } catch (e: Exception) {
        Result.Error(e.message ?: "Ошибка подключения к серверу")
    }
}
