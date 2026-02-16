package mobile.tracker.finance.data.repository

import mobile.tracker.finance.data.api.RetrofitClient
import mobile.tracker.finance.data.models.AuthResponse
import mobile.tracker.finance.data.models.LoginRequest
import mobile.tracker.finance.data.models.RegisterRequest
import mobile.tracker.finance.utils.Result
import retrofit2.Response

/**
 * Репозиторий для работы с аутентификацией
 */
class AuthRepository {

    private val apiService = RetrofitClient.apiService

    /**
     * Выполнить вход в систему
     */
    suspend fun login(email: String, password: String, rememberMe: Boolean): Result<AuthResponse> {
        return try {
            val response = apiService.login(LoginRequest(email, password, rememberMe))
            handleResponse(response)
        } catch (e: Exception) {
            Result.Error("Ошибка подключения к серверу: ${e.message}")
        }
    }

    /**
     * Зарегистрировать нового пользователя
     */
    suspend fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<AuthResponse> {
        return try {
            val response = apiService.register(
                RegisterRequest(name, email, password, confirmPassword)
            )
            handleResponse(response)
        } catch (e: Exception) {
            Result.Error("Ошибка подключения к серверу: ${e.message}")
        }
    }

    /**
     * Обработка ответа от сервера
     */
    private fun <T> handleResponse(response: Response<T>): Result<T> {
        return if (response.isSuccessful) {
            response.body()?.let {
                Result.Success(it)
            } ?: Result.Error("Пустой ответ от сервера")
        } else {
            Result.Error("Ошибка: ${response.code()} - ${response.message()}")
        }
    }
}
