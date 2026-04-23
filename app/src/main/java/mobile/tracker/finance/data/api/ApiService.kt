package mobile.tracker.finance.data.api

import mobile.tracker.finance.data.api.dto.ApiCategoryExpenseDto
import mobile.tracker.finance.data.api.dto.ApiTransactionDto
import mobile.tracker.finance.data.models.AiHealthScore
import mobile.tracker.finance.data.models.AiInsight
import mobile.tracker.finance.data.models.AiTip
import mobile.tracker.finance.data.models.AuthResponse
import mobile.tracker.finance.data.models.Category
import mobile.tracker.finance.data.models.ChatRequest
import mobile.tracker.finance.data.models.ChatResponse
import mobile.tracker.finance.data.models.CreateDebtRequest
import mobile.tracker.finance.data.models.CreateGoalRequest
import mobile.tracker.finance.data.models.Debt
import mobile.tracker.finance.data.models.FinanceStats
import mobile.tracker.finance.data.models.Goal
import mobile.tracker.finance.data.models.GoalContributionRequest
import mobile.tracker.finance.data.models.ImportResult
import mobile.tracker.finance.data.models.Limit
import mobile.tracker.finance.data.models.LimitRequest
import mobile.tracker.finance.data.models.ForgotPasswordRequest
import mobile.tracker.finance.data.models.LoginRequest
import mobile.tracker.finance.data.models.MonthlyStats
import mobile.tracker.finance.data.models.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Unit>

    @GET("finance/stats")
    suspend fun getFinanceStats(
        @Query("month") month: String? = null
    ): Response<FinanceStats>

    @GET("finance/category-expenses")
    suspend fun getCategoryExpenses(
        @Query("month") month: String? = null
    ): Response<List<ApiCategoryExpenseDto>>

    @GET("finance/monthly-stats")
    suspend fun getMonthlyStats(): Response<List<MonthlyStats>>

    @GET("transactions")
    suspend fun getTransactions(
        @Query("type") type: String? = null,
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<List<ApiTransactionDto>>

    @POST("transactions")
    suspend fun createTransaction(@Body transaction: ApiTransactionDto): Response<ApiTransactionDto>

    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: String): Response<Unit>

    @GET("categories")
    suspend fun getCategories(
        @Query("type") type: String? = null
    ): Response<List<Category>>

    @POST("categories")
    suspend fun createCategory(@Body category: Category): Response<Category>

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String): Response<Unit>

    @GET("limits")
    suspend fun getLimits(
        @Query("period") period: String? = null
    ): Response<List<Limit>>

    @POST("limits")
    suspend fun createLimit(@Body request: LimitRequest): Response<Limit>

    @PUT("limits/{id}")
    suspend fun updateLimit(
        @Path("id") id: String,
        @Body request: LimitRequest
    ): Response<Limit>

    @DELETE("limits/{id}")
    suspend fun deleteLimit(@Path("id") id: String): Response<Unit>

    @GET("goals")
    suspend fun getGoals(): Response<List<Goal>>

    @POST("goals")
    suspend fun createGoal(@Body request: CreateGoalRequest): Response<Goal>

    @POST("goals/{id}/contributions")
    suspend fun addGoalContribution(
        @Path("id") id: String,
        @Body request: GoalContributionRequest
    ): Response<Goal>

    @DELETE("goals/{id}")
    suspend fun deleteGoal(@Path("id") id: String): Response<Unit>
    @GET("debts")
    suspend fun getDebts(): Response<List<Debt>>

    @POST("debts")
    suspend fun createDebt(@Body request: CreateDebtRequest): Response<Debt>

    @PUT("debts/{id}/paid")
    suspend fun markDebtAsPaid(@Path("id") id: String): Response<Debt>

    @DELETE("debts/{id}")
    suspend fun deleteDebt(@Path("id") id: String): Response<Unit>

    // ─── Import / Export ──────────────────────────────────────────────────────

    @Multipart
    @POST("import/bank-statement")
    suspend fun importBankStatement(
        @Part file: MultipartBody.Part
    ): Response<ImportResult>

    @Streaming
    @GET("export/pdf")
    suspend fun exportPdf(
        @Query("from") from: String,
        @Query("to") to: String
    ): Response<ResponseBody>

    // Эндпоинты для ИИ

    @GET("ai/health-score")
    suspend fun getAiHealthScore(): Response<AiHealthScore>

    @GET("ai/insights")
    suspend fun getAiInsights(): Response<List<AiInsight>>

    @GET("ai/tips")
    suspend fun getAiTips(): Response<List<AiTip>>

    @POST("ai/chat")
    suspend fun aiChat(@Body request: ChatRequest): Response<ChatResponse>
}
