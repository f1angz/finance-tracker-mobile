package mobile.tracker.finance.ui.screens.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val userName: String = "Иван Петров",
    val userEmail: String = "ivan@example.com",
    val userInitials: String = "ИП",
    val isDarkTheme: Boolean = false,
    val currency: String = "Российский рубль (RUB)",
    val dateFormat: String = "ДД.ММ.ГГГГ",
    val pushNotificationsEnabled: Boolean = true,
    val emailNewsletterEnabled: Boolean = false,
    val biometricsEnabled: Boolean = false,
    val appVersion: String = "1.0.0"
)

class SettingsViewModel : ViewModel() {

    // TODO: заменить на DI-инъекцию (AuthRepository, UserRepository и т.д.)
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onToggleDarkTheme() {
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
    }

    fun onTogglePushNotifications() {
        _uiState.update { it.copy(pushNotificationsEnabled = !it.pushNotificationsEnabled) }
    }

    fun onToggleEmailNewsletter() {
        _uiState.update { it.copy(emailNewsletterEnabled = !it.emailNewsletterEnabled) }
    }

    fun onToggleBiometrics() {
        _uiState.update { it.copy(biometricsEnabled = !it.biometricsEnabled) }
    }

    fun onCurrencyClick() {
        // TODO: открыть диалог/экран выбора валюты
    }

    fun onDateFormatClick() {
        // TODO: открыть диалог/экран выбора формата даты
    }

    fun onChangePassword() {
        // TODO: открыть экран смены пароля
    }

    fun onExportData() {
        // TODO: выгрузить данные в CSV через FileProvider
    }

    fun onImportData() {
        // TODO: загрузить данные из CSV через ActivityResultContracts
    }

    fun onLegalInfoClick() {
        // TODO: открыть экран правовой информации
    }

    fun onEditProfile() {
        // TODO: открыть экран редактирования профиля
    }

    fun onLogout() {
        // TODO: очистить токен авторизации (AuthRepository.logout()) и перейти на Login
    }
}
