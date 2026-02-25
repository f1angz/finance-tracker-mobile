package mobile.tracker.finance.data.repository

import mobile.tracker.finance.data.models.Limit
import mobile.tracker.finance.data.models.LimitRequest
import mobile.tracker.finance.utils.Result

/**
 * Репозиторий для работы с лимитами расходов
 */
interface LimitRepository {

    /**
     * Получить список лимитов за период
     * @param period Период в формате "YYYY-MM"; null — текущий месяц
     */
    suspend fun getLimits(period: String? = null): Result<List<Limit>>

    /**
     * Создать новый лимит
     */
    suspend fun createLimit(request: LimitRequest): Result<Limit>

    /**
     * Обновить лимит
     */
    suspend fun updateLimit(id: String, request: LimitRequest): Result<Limit>

    /**
     * Удалить лимит
     */
    suspend fun deleteLimit(id: String): Result<Unit>
}
