package mobile.tracker.finance.data.repository

import mobile.tracker.finance.data.models.CreateDebtRequest
import mobile.tracker.finance.data.models.Debt
import mobile.tracker.finance.utils.Result

/**
 * Репозиторий для работы с долговыми обязательствами
 */
interface DebtRepository {

    /**
     * Получить список всех долгов (активных и погашенных)
     */
    suspend fun getDebts(): Result<List<Debt>>

    /**
     * Создать новый долг
     */
    suspend fun createDebt(request: CreateDebtRequest): Result<Debt>

    /**
     * Отметить долг как погашенный
     */
    suspend fun markAsPaid(id: String): Result<Debt>

    /**
     * Удалить долг
     */
    suspend fun deleteDebt(id: String): Result<Unit>
}
