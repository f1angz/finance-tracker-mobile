package mobile.tracker.finance.ui.screens.ai_assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobile.tracker.finance.data.models.AiHealthScore
import mobile.tracker.finance.data.models.AiInsight
import mobile.tracker.finance.data.models.AiTip
import mobile.tracker.finance.data.models.ChatMessage
import mobile.tracker.finance.data.models.ChatRequest
import mobile.tracker.finance.data.repository.AiRepository
import mobile.tracker.finance.data.repository.MockAiRepository
import mobile.tracker.finance.utils.Result
import java.util.UUID

enum class AiTab { INSIGHTS, TIPS, CHAT }

data class AiAssistantUiState(
    val healthScore: AiHealthScore? = null,
    val insights: List<AiInsight> = emptyList(),
    val tips: List<AiTip> = emptyList(),
    val chatMessages: List<ChatMessage> = emptyList(),
    val isLoadingInsights: Boolean = false,
    val isLoadingTips: Boolean = false,
    val isSendingMessage: Boolean = false,
    val chatInput: String = "",
    val error: String? = null
)

class AiAssistantViewModel : ViewModel() {

    // TODO: заменить на DI-инъекцию при подключении реального AI-бекенда
    private val repository: AiRepository = MockAiRepository()

    private val _uiState = MutableStateFlow(AiAssistantUiState(isLoadingInsights = true))
    val uiState: StateFlow<AiAssistantUiState> = _uiState.asStateFlow()

    private val _selectedTab = MutableStateFlow(AiTab.INSIGHTS)
    val selectedTab: StateFlow<AiTab> = _selectedTab.asStateFlow()

    init {
        loadHealthScore()
        loadInsights()
        addWelcomeMessage()
    }

    fun selectTab(tab: AiTab) {
        _selectedTab.value = tab
        when (tab) {
            AiTab.TIPS -> if (_uiState.value.tips.isEmpty()) {
                // Синхронно ставим флаг загрузки до начала корутины,
                // чтобы не мелькало пустое состояние "Нет советов"
                _uiState.update { it.copy(isLoadingTips = true) }
                loadTips()
            }
            else -> Unit
        }
    }

    fun onChatInputChange(input: String) {
        _uiState.update { it.copy(chatInput = input) }
    }

    fun sendMessage() {
        val message = _uiState.value.chatInput.trim()
        if (message.isEmpty() || _uiState.value.isSendingMessage) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = message,
            isFromUser = true
        )
        _uiState.update {
            it.copy(
                chatMessages = it.chatMessages + userMessage,
                chatInput = "",
                isSendingMessage = true,
                error = null
            )
        }

        viewModelScope.launch {
            when (val result = repository.sendMessage(ChatRequest(message))) {
                is Result.Success -> {
                    val aiMessage = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        content = result.data.reply,
                        isFromUser = false
                    )
                    _uiState.update {
                        it.copy(
                            chatMessages = it.chatMessages + aiMessage,
                            isSendingMessage = false
                        )
                    }
                }
                is Result.Error -> _uiState.update {
                    it.copy(error = result.message, isSendingMessage = false)
                }
                else -> Unit
            }
        }
    }

    private fun loadHealthScore() {
        viewModelScope.launch {
            when (val result = repository.getHealthScore()) {
                is Result.Success -> _uiState.update { it.copy(healthScore = result.data) }
                else -> Unit
            }
        }
    }

    private fun loadInsights() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingInsights = true, error = null) }
            when (val result = repository.getInsights()) {
                is Result.Success -> _uiState.update {
                    it.copy(insights = result.data, isLoadingInsights = false)
                }
                is Result.Error -> _uiState.update {
                    it.copy(error = result.message, isLoadingInsights = false)
                }
                else -> Unit
            }
        }
    }

    private fun loadTips() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingTips = true, error = null) }
            when (val result = repository.getTips()) {
                is Result.Success -> _uiState.update {
                    it.copy(tips = result.data, isLoadingTips = false)
                }
                is Result.Error -> _uiState.update {
                    it.copy(error = result.message, isLoadingTips = false)
                }
                else -> Unit
            }
        }
    }

    private fun addWelcomeMessage() {
        val welcome = ChatMessage(
            id = "welcome",
            content = "Привет! Я ИИ-помощник по финансам. Могу помочь с анализом расходов, советами по экономии и планированием бюджета. Что вас интересует?",
            isFromUser = false
        )
        _uiState.update { it.copy(chatMessages = listOf(welcome)) }
    }
}
