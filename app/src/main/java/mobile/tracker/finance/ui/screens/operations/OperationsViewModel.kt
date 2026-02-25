package mobile.tracker.finance.ui.screens.operations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobile.tracker.finance.data.models.TransactionFilter
import mobile.tracker.finance.data.models.TransactionGroup
import mobile.tracker.finance.data.repository.FinanceRepository
import mobile.tracker.finance.data.repository.MockFinanceRepository
import mobile.tracker.finance.utils.Result

/**
 * ViewModel для экрана "Операции"
 * Управляет списком транзакций, фильтрацией и поиском
 */
class OperationsViewModel(
    private val repository: FinanceRepository = MockFinanceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(OperationsUiState())
    val uiState: StateFlow<OperationsUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadTransactions()
    }

    /**
     * Изменить активный фильтр (Все / Доходы / Расходы)
     */
    fun onFilterChanged(filter: TransactionFilter) {
        _uiState.update { it.copy(activeFilter = filter) }
        loadTransactions()
    }

    /**
     * Изменить строку поиска (с debounce 300 мс)
     */
    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            loadTransactions()
        }
    }

    /**
     * Перезагрузить транзакции с текущими фильтрами
     */
    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val state = _uiState.value
            when (val result = repository.getTransactions(state.activeFilter, state.searchQuery)) {
                is Result.Success -> {
                    _uiState.update { it.copy(transactionGroups = result.data, isLoading = false) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.message, isLoading = false) }
                }
                is Result.Loading -> {}
            }
        }
    }
}

/**
 * Состояние UI экрана "Операции"
 */
data class OperationsUiState(
    val isLoading: Boolean = false,
    val transactionGroups: List<TransactionGroup> = emptyList(),
    val activeFilter: TransactionFilter = TransactionFilter.ALL,
    val searchQuery: String = "",
    val error: String? = null
)
