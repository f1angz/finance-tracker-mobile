package mobile.tracker.finance.data.repository

import mobile.tracker.finance.data.models.AiHealthScore
import mobile.tracker.finance.data.models.AiInsight
import mobile.tracker.finance.data.models.AiTip
import mobile.tracker.finance.data.models.ChatRequest
import mobile.tracker.finance.data.models.ChatResponse
import mobile.tracker.finance.utils.Result

/**
 * Репозиторий для работы с ИИ-помощником.
 * TODO: заменить MockAiRepository на реальную реализацию при подключении AI-бекенда.
 */
interface AiRepository {

    /** Получить финансовое здоровье пользователя */
    suspend fun getHealthScore(): Result<AiHealthScore>

    /** Получить список инсайтов (аналитических выводов) */
    suspend fun getInsights(): Result<List<AiInsight>>

    /** Получить список финансовых советов */
    suspend fun getTips(): Result<List<AiTip>>

    /** Отправить сообщение в чат и получить ответ ИИ */
    suspend fun sendMessage(request: ChatRequest): Result<ChatResponse>
}
