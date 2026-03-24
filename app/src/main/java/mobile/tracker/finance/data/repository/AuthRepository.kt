package mobile.tracker.finance.data.repository

import mobile.tracker.finance.data.TokenManager
import mobile.tracker.finance.data.api.RetrofitClient
import mobile.tracker.finance.data.models.AuthResponse
import mobile.tracker.finance.data.models.LoginRequest
import mobile.tracker.finance.data.models.RegisterRequest
import mobile.tracker.finance.utils.Result
import retrofit2.Response

class AuthRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun login(email: String, password: String, rememberMe: Boolean): Result<AuthResponse> {
        return try {
            val response = apiService.login(LoginRequest(email, password, rememberMe))
            handleAuthResponse(response)
        } catch (e: Exception) {
            Result.Error("Ошибка подключения к серверу: ${e.message}")
        }
    }

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
            handleAuthResponse(response)
        } catch (e: Exception) {
            Result.Error("Ошибка подключения к серверу: ${e.message}")
        }
    }

    suspend fun logout() {
        TokenManager.clearToken()
    }

    private suspend fun handleAuthResponse(response: Response<AuthResponse>): Result<AuthResponse> {
        return if (response.isSuccessful) {
            response.body()?.let { authResponse ->
                TokenManager.saveToken(authResponse.token)
                TokenManager.saveUser(authResponse.user.name, authResponse.user.email)
                Result.Success(authResponse)
            } ?: Result.Error("Пустой ответ от сервера")
        } else {
            Result.Error("Ошибка ${response.code()}: ${response.message()}")
        }
    }
}
