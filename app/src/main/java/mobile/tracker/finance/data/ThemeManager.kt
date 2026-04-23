package mobile.tracker.finance.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

private val Context.themeDataStore by preferencesDataStore(name = "finance_theme")

object ThemeManager {

    private val DARK_KEY = booleanPreferencesKey("dark_theme")

    private lateinit var appContext: Context

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    suspend fun load() {
        val prefs = appContext.themeDataStore.data.first()
        _isDarkTheme.value = prefs[DARK_KEY] ?: false
    }

    suspend fun toggle() {
        val newValue = !_isDarkTheme.value
        _isDarkTheme.value = newValue
        appContext.themeDataStore.edit { it[DARK_KEY] = newValue }
    }

    fun current(): Boolean = _isDarkTheme.value
}
