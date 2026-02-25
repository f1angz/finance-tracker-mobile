package mobile.tracker.finance.data.repository

import kotlinx.coroutines.delay
import mobile.tracker.finance.data.models.CreateGoalRequest
import mobile.tracker.finance.data.models.Goal
import mobile.tracker.finance.data.models.GoalContributionRequest
import mobile.tracker.finance.utils.Result

/**
 * Mock-реализация GoalRepository с тестовыми данными.
 * TODO: заменить на RealGoalRepository при подключении бекенда.
 */
class MockGoalRepository : GoalRepository {

    override suspend fun getGoals(): Result<List<Goal>> {
        delay(300)
        return Result.Success(SAMPLE_GOALS)
    }

    override suspend fun createGoal(request: CreateGoalRequest): Result<Goal> {
        delay(300)
        // TODO: POST /goals
        return Result.Error("Создание целей будет доступно после подключения бекенда")
    }

    override suspend fun addContribution(
        goalId: String,
        request: GoalContributionRequest
    ): Result<Goal> {
        delay(300)
        // TODO: POST /goals/{id}/contributions
        return Result.Error("Пополнение будет доступно после подключения бекенда")
    }

    override suspend fun deleteGoal(id: String): Result<Unit> {
        delay(300)
        // TODO: DELETE /goals/{id}
        return Result.Error("Удаление будет доступно после подключения бекенда")
    }

    companion object {
        val SAMPLE_GOALS = listOf(
            Goal(
                id = "1",
                emoji = "🏖️",
                title = "Отпуск в Европе",
                daysLeft = 161,
                savedAmount = 145_000.0,
                targetAmount = 200_000.0,
                accentColor = "3B82F6"
            ),
            Goal(
                id = "2",
                emoji = "💻",
                title = "Новый MacBook Pro",
                daysLeft = 117,
                savedAmount = 180_000.0,
                targetAmount = 250_000.0,
                accentColor = "8B5CF6"
            ),
            Goal(
                id = "3",
                emoji = "🛡️",
                title = "Аварийный фонд",
                daysLeft = 330,
                savedAmount = 320_000.0,
                targetAmount = 500_000.0,
                accentColor = "10B981"
            ),
            Goal(
                id = "4",
                emoji = "🏠",
                title = "Первый взнос",
                daysLeft = 540,
                savedAmount = 600_000.0,
                targetAmount = 1_500_000.0,
                accentColor = "F59E0B"
            )
        )
    }
}
