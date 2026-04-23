package mobile.tracker.finance.data.repository

import mobile.tracker.finance.data.models.CreateGoalRequest
import mobile.tracker.finance.data.models.Goal
import mobile.tracker.finance.data.models.GoalContributionRequest
import mobile.tracker.finance.utils.Result

/**
 * Репозиторий для работы с финансовыми целями
 */
interface GoalRepository {

    /**
     * Получить список всех целей
     */
    suspend fun getGoals(): Result<List<Goal>>

    /**
     * Создать новую цель
     */
    suspend fun createGoal(request: CreateGoalRequest): Result<Goal>

    /**
     * Пополнить накопления по цели
     * @param goalId ID цели
     * @param request Сумма пополнения
     */
    suspend fun addContribution(goalId: String, request: GoalContributionRequest): Result<Goal>

    /**
     * Удалить цель
     */
    suspend fun deleteGoal(id: String): Result<Unit>
}
