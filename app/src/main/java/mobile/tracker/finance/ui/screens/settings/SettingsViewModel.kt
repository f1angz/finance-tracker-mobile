package mobile.tracker.finance.ui.screens.settings

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import mobile.tracker.finance.notifications.NotificationScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobile.tracker.finance.data.ThemeManager
import mobile.tracker.finance.data.TokenManager
import mobile.tracker.finance.data.api.RetrofitClient
import mobile.tracker.finance.data.repository.AuthRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

data class SettingsUiState(
    val userName: String = "",
    val userEmail: String = "",
    val userInitials: String = "",
    val isDarkTheme: Boolean = false,
    val currency: String = "Российский рубль (RUB)",
    val dateFormat: String = "ДД.ММ.ГГГГ",
    val pushNotificationsEnabled: Boolean = false,
    val emailNewsletterEnabled: Boolean = false,
    val biometricsEnabled: Boolean = false,
    val appVersion: String = "1.0.0",
    val loggedOut: Boolean = false,
    val isImporting: Boolean = false,
    val isExporting: Boolean = false,
    val importMessage: String? = null,
    val exportMessage: String? = null
)

class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val authRepository = AuthRepository()
    private val api = RetrofitClient.apiService

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        val name = TokenManager.getUserName() ?: ""
        val email = TokenManager.getUserEmail() ?: ""
        val initials = name.trim().split(" ")
            .take(2).mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
            .joinToString("")
        _uiState.update {
            it.copy(
                userName     = name,
                userEmail    = email,
                userInitials = initials,
                isDarkTheme  = ThemeManager.current()
            )
        }
    }

    fun onToggleDarkTheme() {
        viewModelScope.launch {
            ThemeManager.toggle()
            _uiState.update { it.copy(isDarkTheme = ThemeManager.current()) }
        }
    }

    fun onTogglePushNotifications() {
        val enabled = !_uiState.value.pushNotificationsEnabled
        _uiState.update { it.copy(pushNotificationsEnabled = enabled) }
        val context = getApplication<Application>()
        if (enabled) {
            NotificationScheduler.schedule(context)
        } else {
            NotificationScheduler.cancel(context)
        }
    }

    fun onToggleEmailNewsletter() {
        _uiState.update { it.copy(emailNewsletterEnabled = !it.emailNewsletterEnabled) }
    }

    fun onToggleBiometrics() {
        _uiState.update { it.copy(biometricsEnabled = !it.biometricsEnabled) }
    }

    fun onCurrencyClick() { }

    fun onDateFormatClick() { }

    fun onChangePassword() { }

    fun onLegalInfoClick() { }

    fun onEditProfile() { }

    fun onLogout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { it.copy(loggedOut = true) }
        }
    }

    // ─── Import ───────────────────────────────────────────────────────────────

    fun importBankStatement(uri: Uri, context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isImporting = true) }
            try {
                val bytes = context.contentResolver.openInputStream(uri)!!.use { it.readBytes() }
                val requestBody = bytes.toRequestBody("application/pdf".toMediaType())
                val part = MultipartBody.Part.createFormData("file", "statement.pdf", requestBody)
                val response = api.importBankStatement(part)
                if (response.isSuccessful) {
                    val result = response.body()!!
                    _uiState.update {
                        it.copy(
                            isImporting = false,
                            importMessage = "Импортировано ${result.imported} транзакций, пропущено ${result.skipped}"
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isImporting = false, importMessage = "Ошибка импорта: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isImporting = false, importMessage = "Ошибка: ${e.message}")
                }
            }
        }
    }

    // ─── Export ───────────────────────────────────────────────────────────────

    fun exportPdf(from: String, to: String, context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }
            try {
                val response = api.exportPdf(from, to)
                if (response.isSuccessful) {
                    val bytes = response.body()!!.bytes()
                    val exportsDir = File(context.cacheDir, "exports").also { it.mkdirs() }
                    val file = File(exportsDir, "finance-report-$from-$to.pdf")
                    file.writeBytes(bytes)

                    val fileUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(fileUri, "application/pdf")
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    _uiState.update { it.copy(isExporting = false, exportMessage = "Отчёт сохранён") }
                } else {
                    _uiState.update {
                        it.copy(isExporting = false, exportMessage = "Ошибка экспорта: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isExporting = false, exportMessage = "Ошибка: ${e.message}")
                }
            }
        }
    }

    fun clearImportMessage() = _uiState.update { it.copy(importMessage = null) }
    fun clearExportMessage() = _uiState.update { it.copy(exportMessage = null) }
}
