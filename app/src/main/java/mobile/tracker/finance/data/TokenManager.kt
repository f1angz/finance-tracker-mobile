package mobile.tracker.finance.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.authDataStore by preferencesDataStore(name = "finance_auth")

/**
 * Хранит JWT-токен в памяти и в DataStore (для сохранения между сессиями).
 * Инициализируется один раз в FinanceApp.onCreate().
 */
object TokenManager {

    private val TOKEN_KEY = stringPreferencesKey("auth_token")
    private val NAME_KEY  = stringPreferencesKey("user_name")
    private val EMAIL_KEY = stringPreferencesKey("user_email")

    @Volatile private var _token: String? = null
    @Volatile private var _userName: String? = null
    @Volatile private var _userEmail: String? = null

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    /** Загружает токен и данные пользователя из DataStore. Вызывается при старте приложения. */
    suspend fun loadFromStorage() {
        val prefs = appContext.authDataStore.data.first()
        _token     = prefs[TOKEN_KEY]
        _userName  = prefs[NAME_KEY]
        _userEmail = prefs[EMAIL_KEY]
    }

    /** Возвращает текущий токен (из памяти). */
    fun getToken(): String? = _token

    /** Авторизован ли пользователь. */
    fun isLoggedIn(): Boolean = _token != null

    fun getUserName(): String? = _userName
    fun getUserEmail(): String? = _userEmail

    /** Сохраняет токен после успешного входа / регистрации. */
    suspend fun saveToken(token: String) {
        _token = token
        appContext.authDataStore.edit { prefs -> prefs[TOKEN_KEY] = token }
    }

    /** Сохраняет данные пользователя после успешного входа / регистрации. */
    suspend fun saveUser(name: String, email: String) {
        _userName  = name
        _userEmail = email
        appContext.authDataStore.edit { prefs ->
            prefs[NAME_KEY]  = name
            prefs[EMAIL_KEY] = email
        }
    }

    /** Удаляет токен и данные пользователя при выходе из аккаунта. */
    suspend fun clearToken() {
        _token     = null
        _userName  = null
        _userEmail = null
        appContext.authDataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(NAME_KEY)
            prefs.remove(EMAIL_KEY)
        }
    }
}
