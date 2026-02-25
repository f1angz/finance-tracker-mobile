package mobile.tracker.finance.ui.screens.limits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobile.tracker.finance.data.models.Limit
import mobile.tracker.finance.data.repository.LimitRepository
import mobile.tracker.finance.data.repository.MockLimitRepository
import mobile.tracker.finance.utils.Result

data class LimitsUiState(
    val limits: List<Limit> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val totalLimit: Double    get() = limits.sumOf { it.limitAmount }
    val totalSpent: Double    get() = limits.sumOf { it.spentAmount }
    val totalRemaining: Double get() = (totalLimit - totalSpent).coerceAtLeast(0.0)
}

class LimitsViewModel : ViewModel() {

    // TODO: заменить на DI-инъекцию при подключении реального бекенда
    private val repository: LimitRepository = MockLimitRepository()

    private val _uiState = MutableStateFlow(LimitsUiState(isLoading = true))
    val uiState: StateFlow<LimitsUiState> = _uiState.asStateFlow()

    init {
        loadLimits()
    }

    fun loadLimits() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getLimits()) {
                is Result.Success -> _uiState.update {
                    it.copy(limits = result.data, isLoading = false)
                }
                is Result.Error -> _uiState.update {
                    it.copy(error = result.message, isLoading = false)
                }
                else -> Unit
            }
        }
    }

    fun onAddLimit() {
        // TODO: открыть диалог / экран добавления лимита
    }
}
