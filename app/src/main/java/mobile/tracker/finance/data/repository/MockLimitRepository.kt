package mobile.tracker.finance.data.repository

import kotlinx.coroutines.delay
import mobile.tracker.finance.data.models.Limit
import mobile.tracker.finance.data.models.LimitRequest
import mobile.tracker.finance.utils.Result

/**
 * Mock-реализация LimitRepository с тестовыми данными.
 * Заменить на RealLimitRepository при подключении бекенда.
 */
class MockLimitRepository : LimitRepository {

    override suspend fun getLimits(period: String?): Result<List<Limit>> {
        delay(300)
        return Result.Success(SAMPLE_LIMITS)
    }

    override suspend fun createLimit(request: LimitRequest): Result<Limit> {
        delay(200)
        val newLimit = Limit(
            id = System.currentTimeMillis().toString(),
            categorySlug = request.categorySlug,
            categoryName = request.categorySlug,
            period = request.period,
            limitAmount = request.amount,
            spentAmount = 0.0
        )
        return Result.Success(newLimit)
    }

    override suspend fun updateLimit(id: String, request: LimitRequest): Result<Limit> {
        delay(200)
        val original = SAMPLE_LIMITS.find { it.id == id }
            ?: return Result.Error("Лимит не найден")
        return Result.Success(original.copy(limitAmount = request.amount))
    }

    override suspend fun deleteLimit(id: String): Result<Unit> {
        delay(200)
        return Result.Success(Unit)
    }

    companion object {
        private val SAMPLE_LIMITS = listOf(
            Limit(
                id = "1",
                categorySlug = "products",
                categoryName = "Продукты",
                period = "Февраль 2026",
                limitAmount = 20_000.0,
                spentAmount = 18_500.0
            ),
            Limit(
                id = "2",
                categorySlug = "transport",
                categoryName = "Транспорт",
                period = "Февраль 2026",
                limitAmount = 15_000.0,
                spentAmount = 12_300.0
            ),
            Limit(
                id = "3",
                categorySlug = "entertainment",
                categoryName = "Развлечения",
                period = "Февраль 2026",
                limitAmount = 10_000.0,
                spentAmount = 8_900.0
            )
        )
    }
}
