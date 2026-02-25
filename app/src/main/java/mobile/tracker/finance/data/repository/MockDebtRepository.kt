package mobile.tracker.finance.data.repository

import kotlinx.coroutines.delay
import mobile.tracker.finance.data.models.CreateDebtRequest
import mobile.tracker.finance.data.models.Debt
import mobile.tracker.finance.data.models.DebtType
import mobile.tracker.finance.utils.Result

/**
 * Mock-реализация DebtRepository с тестовыми данными.
 * TODO: заменить на RealDebtRepository при подключении бекенда.
 */
class MockDebtRepository : DebtRepository {

    override suspend fun getDebts(): Result<List<Debt>> {
        delay(300)
        return Result.Success(SAMPLE_DEBTS)
    }

    override suspend fun createDebt(request: CreateDebtRequest): Result<Debt> {
        delay(300)
        // TODO: POST /debts
        return Result.Error("Добавление долгов будет доступно после подключения бекенда")
    }

    override suspend fun markAsPaid(id: String): Result<Debt> {
        delay(300)
        // TODO: PUT /debts/{id}/paid
        return Result.Error("Погашение будет доступно после подключения бекенда")
    }

    override suspend fun deleteDebt(id: String): Result<Unit> {
        delay(300)
        // TODO: DELETE /debts/{id}
        return Result.Error("Удаление будет доступно после подключения бекенда")
    }

    companion object {
        val SAMPLE_DEBTS = listOf(
            Debt(
                id = "1",
                personName = "Алексей Смирнов",
                type = DebtType.I_OWE,
                amount = 50_000.0,
                dueDate = "2026-03-15",
                isPaid = false
            ),
            Debt(
                id = "2",
                personName = "Мария Петрова",
                type = DebtType.THEY_OWE,
                amount = 25_000.0,
                dueDate = "2026-02-20",
                isPaid = false
            ),
            Debt(
                id = "3",
                personName = "Дмитрий Иванов",
                type = DebtType.THEY_OWE,
                amount = 100_000.0,
                dueDate = "2025-12-01",
                isPaid = true
            )
        )
    }
}
