package mobile.tracker.finance.data.repository

import kotlinx.coroutines.delay
import mobile.tracker.finance.data.models.AuthResponse
import mobile.tracker.finance.data.models.User
import mobile.tracker.finance.utils.Result

/**
 * Мок-репозиторий для тестирования без реального бекенда
 *
 * Тестовые данные для входа:
 * Email: test@example.com
 * Пароль: 123456
 */
class MockAuthRepository {

    // Тестовый пользователь
    private val testUser = User(
        id = "test-user-id-12345",
        name = "Тестовый Пользователь",
        email = "test@example.com"
    )

    // Список зарегистрированных пользователей (симуляция БД)
    private val registeredUsers = mutableMapOf(
        "test@example.com" to testUser
    )

    // Пароли пользователей (в реальности хешируются на сервере)
    private val userPasswords = mutableMapOf(
        "test@example.com" to "123456"
    )

    /**
     * Выполнить вход (мок)
     */
    suspend fun login(email: String, password: String, rememberMe: Boolean): Result<AuthResponse> {
        // Имитация задержки сети
        delay(1000)

        // Проверка существования пользователя
        val user = registeredUsers[email]
        if (user == null) {
            return Result.Error("Пользователь с таким email не найден")
        }

        // Проверка пароля
        val storedPassword = userPasswords[email]
        if (storedPassword != password) {
            return Result.Error("Неверный пароль")
        }

        // Генерация мок-токена
        val mockToken = "mock-jwt-token-${System.currentTimeMillis()}"

        return Result.Success(
            AuthResponse(
                token = mockToken,
                user = user
            )
        )
    }

    /**
     * Зарегистрировать нового пользователя (мок)
     */
    suspend fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<AuthResponse> {
        // Имитация задержки сети
        delay(1000)

        // Проверка совпадения паролей
        if (password != confirmPassword) {
            return Result.Error("Пароли не совпадают")
        }

        // Проверка, что пользователь еще не зарегистрирован
        if (registeredUsers.containsKey(email)) {
            return Result.Error("Пользователь с таким email уже существует")
        }

        // Создание нового пользователя
        val newUser = User(
            id = "user-id-${System.currentTimeMillis()}",
            name = name,
            email = email
        )

        // Сохранение в "базу данных"
        registeredUsers[email] = newUser
        userPasswords[email] = password

        // Генерация мок-токена
        val mockToken = "mock-jwt-token-${System.currentTimeMillis()}"

        return Result.Success(
            AuthResponse(
                token = mockToken,
                user = newUser
            )
        )
    }

    /**
     * Получить список всех зарегистрированных пользователей (для отладки)
     */
    fun getAllUsers(): Map<String, User> = registeredUsers.toMap()
}
