package mobile.tracker.finance.data.api

import mobile.tracker.finance.data.models.AuthResponse
import mobile.tracker.finance.data.models.Category
import mobile.tracker.finance.data.models.CreateDebtRequest
import mobile.tracker.finance.data.models.CreateGoalRequest
import mobile.tracker.finance.data.models.Debt
import mobile.tracker.finance.data.models.Goal
import mobile.tracker.finance.data.models.GoalContributionRequest
import mobile.tracker.finance.data.models.Limit
import mobile.tracker.finance.data.models.LimitRequest
import mobile.tracker.finance.data.models.LoginRequest
import mobile.tracker.finance.data.models.RegisterRequest
import mobile.tracker.finance.data.models.Transaction
import retrofit2.Response
import retrofit2.http.*

/**
 * Интерфейс API для работы с бекендом
 * Эндпоинты нужно будет обновить, когда бекенд будет готов
 */
interface ApiService {

    /**
     * Вход в систему
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    /**
     * Регистрация нового пользователя
     */
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    /**
     * Сброс пароля (для будущей реализации)
     */
    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body email: String): Response<Unit>

    // ─── Транзакции ───────────────────────────────────────────────────────────

    /**
     * Получить список транзакций с фильтрацией, поиском и пагинацией
     * @param type "INCOME" | "EXPENSE" | null (все)
     * @param search Строка поиска
     * @param page Номер страницы (начиная с 1)
     * @param limit Количество записей на странице
     */
    @GET("transactions")
    suspend fun getTransactions(
        @Query("type") type: String? = null,
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<List<Transaction>>

    /**
     * Создать новую транзакцию
     */
    @POST("transactions")
    suspend fun createTransaction(@Body transaction: Transaction): Response<Transaction>

    /**
     * Удалить транзакцию по ID
     */
    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: String): Response<Unit>

    // ─── Категории ────────────────────────────────────────────────────────────

    /**
     * Получить список категорий с фильтрацией по типу
     * @param type "EXPENSE" | "INCOME" | "OTHER" | null (все)
     */
    @GET("categories")
    suspend fun getCategories(
        @Query("type") type: String? = null
    ): Response<List<Category>>

    /**
     * Создать новую категорию
     */
    @POST("categories")
    suspend fun createCategory(@Body category: Category): Response<Category>

    /**
     * Удалить категорию по ID
     */
    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String): Response<Unit>

    // ─── Лимиты ───────────────────────────────────────────────────────────────

    /**
     * Получить список лимитов за период
     * @param period Период в формате "YYYY-MM"; null — текущий месяц
     */
    @GET("limits")
    suspend fun getLimits(
        @Query("period") period: String? = null
    ): Response<List<Limit>>

    /**
     * Создать новый лимит
     */
    @POST("limits")
    suspend fun createLimit(@Body request: LimitRequest): Response<Limit>

    /**
     * Обновить лимит по ID
     */
    @PUT("limits/{id}")
    suspend fun updateLimit(
        @Path("id") id: String,
        @Body request: LimitRequest
    ): Response<Limit>

    /**
     * Удалить лимит по ID
     */
    @DELETE("limits/{id}")
    suspend fun deleteLimit(@Path("id") id: String): Response<Unit>

    // ─── Цели ─────────────────────────────────────────────────────────────────

    /**
     * Получить список целей
     */
    @GET("goals")
    suspend fun getGoals(): Response<List<Goal>>

    /**
     * Создать новую цель
     */
    @POST("goals")
    suspend fun createGoal(@Body request: CreateGoalRequest): Response<Goal>

    /**
     * Пополнить цель
     */
    @POST("goals/{id}/contributions")
    suspend fun addGoalContribution(
        @Path("id") id: String,
        @Body request: GoalContributionRequest
    ): Response<Goal>

    /**
     * Удалить цель
     */
    @DELETE("goals/{id}")
    suspend fun deleteGoal(@Path("id") id: String): Response<Unit>

    // ─── Долги ────────────────────────────────────────────────────────────────

    /**
     * Получить список долгов
     */
    @GET("debts")
    suspend fun getDebts(): Response<List<Debt>>

    /**
     * Создать новый долг
     */
    @POST("debts")
    suspend fun createDebt(@Body request: CreateDebtRequest): Response<Debt>

    /**
     * Отметить долг как погашенный
     */
    @PUT("debts/{id}/paid")
    suspend fun markDebtAsPaid(@Path("id") id: String): Response<Debt>

    /**
     * Удалить долг
     */
    @DELETE("debts/{id}")
    suspend fun deleteDebt(@Path("id") id: String): Response<Unit>
}
