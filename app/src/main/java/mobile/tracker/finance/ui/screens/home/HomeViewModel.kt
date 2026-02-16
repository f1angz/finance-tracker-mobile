package mobile.tracker.finance.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobile.tracker.finance.data.models.*
import mobile.tracker.finance.data.repository.FinanceRepository
import mobile.tracker.finance.data.repository.MockFinanceRepository
import mobile.tracker.finance.utils.Result

/**
 * ViewModel для главного экрана
 * Управляет состоянием и бизнес-логикой главного экрана
 */
class HomeViewModel(
    private val repository: FinanceRepository = MockFinanceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    /**
     * Загрузить все данные для главного экрана
     */
    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Загружаем данные параллельно
            launch { loadFinanceStats() }
            launch { loadCategoryExpenses() }
            launch { loadMonthlyStats() }
            launch { loadRecentTransactions() }
        }
    }

    /**
     * Загрузить финансовую статистику
     */
    private suspend fun loadFinanceStats() {
        when (val result = repository.getFinanceStats()) {
            is Result.Success -> {
                _uiState.update { it.copy(stats = result.data) }
            }
            is Result.Error -> {
                _uiState.update { it.copy(error = result.message) }
            }
            is Result.Loading -> {
                // Состояние загрузки уже установлено
            }
        }
    }

    /**
     * Загрузить расходы по категориям
     */
    private suspend fun loadCategoryExpenses() {
        when (val result = repository.getCategoryExpenses()) {
            is Result.Success -> {
                _uiState.update { it.copy(categoryExpenses = result.data) }
            }
            is Result.Error -> {
                _uiState.update { it.copy(error = result.message) }
            }
            is Result.Loading -> {
                // Состояние загрузки уже установлено
            }
        }
    }

    /**
     * Загрузить статистику по месяцам
     */
    private suspend fun loadMonthlyStats() {
        when (val result = repository.getMonthlyStats()) {
            is Result.Success -> {
                _uiState.update { it.copy(monthlyStats = result.data, isLoading = false) }
            }
            is Result.Error -> {
                _uiState.update { it.copy(error = result.message, isLoading = false) }
            }
            is Result.Loading -> {
                // Состояние загрузки уже установлено
            }
        }
    }

    /**
     * Загрузить последние транзакции
     */
    private suspend fun loadRecentTransactions() {
        when (val result = repository.getRecentTransactions(5)) {
            is Result.Success -> {
                _uiState.update { it.copy(recentTransactions = result.data) }
            }
            is Result.Error -> {
                _uiState.update { it.copy(error = result.message) }
            }
            is Result.Loading -> {
                // Состояние загрузки уже установлено
            }
        }
    }
}

/**
 * Состояние UI главного экрана
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val stats: FinanceStats? = null,
    val categoryExpenses: List<CategoryExpense> = emptyList(),
    val monthlyStats: List<MonthlyStats> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),
    val error: String? = null
)
