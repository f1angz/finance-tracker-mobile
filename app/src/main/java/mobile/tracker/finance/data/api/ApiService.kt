package mobile.tracker.finance.data.api

import mobile.tracker.finance.data.models.AuthResponse
import mobile.tracker.finance.data.models.LoginRequest
import mobile.tracker.finance.data.models.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

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
}
