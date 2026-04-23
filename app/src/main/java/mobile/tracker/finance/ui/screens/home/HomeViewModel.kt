package mobile.tracker.finance.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobile.tracker.finance.data.models.*
import mobile.tracker.finance.data.repository.ApiFinanceRepository
import mobile.tracker.finance.data.repository.FinanceRepository
import mobile.tracker.finance.utils.Result
import java.util.Calendar

class HomeViewModel(
    private val repository: FinanceRepository = ApiFinanceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(selectedMonth = currentMonthString()))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.addTransaction(transaction)
            loadData()
        }
    }

    fun prevMonth() {
        _uiState.update { it.copy(selectedMonth = monthOffset(it.selectedMonth, -1)) }
        loadData()
    }

    fun nextMonth() {
        val next = monthOffset(_uiState.value.selectedMonth, 1)
        if (next <= currentMonthString()) {
            _uiState.update { it.copy(selectedMonth = next) }
            loadData()
        }
    }

    fun loadData() {
        val month = _uiState.value.selectedMonth
        // Для текущего месяца не передаём параметр — бекенд использует дефолт
        val monthParam = if (month == currentMonthString()) null else month
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            launch { loadFinanceStats(monthParam) }
            launch { loadCategoryExpenses(monthParam) }
            launch { loadMonthlyStats() }
            launch { loadRecentTransactions() }
        }
    }

    private suspend fun loadFinanceStats(month: String?) {
        when (val result = repository.getFinanceStats(month)) {
            is Result.Success -> _uiState.update { it.copy(stats = result.data) }
            is Result.Error   -> _uiState.update { it.copy(error = result.message) }
            is Result.Loading -> Unit
        }
    }

    private suspend fun loadCategoryExpenses(month: String?) {
        when (val result = repository.getCategoryExpenses(month)) {
            is Result.Success -> _uiState.update { it.copy(categoryExpenses = result.data) }
            is Result.Error   -> _uiState.update { it.copy(error = result.message) }
            is Result.Loading -> Unit
        }
    }

    private suspend fun loadMonthlyStats() {
        when (val result = repository.getMonthlyStats()) {
            is Result.Success -> _uiState.update { it.copy(monthlyStats = result.data, isLoading = false) }
            is Result.Error   -> _uiState.update { it.copy(error = result.message, isLoading = false) }
            is Result.Loading -> Unit
        }
    }

    private suspend fun loadRecentTransactions() {
        when (val result = repository.getRecentTransactions(5)) {
            is Result.Success -> _uiState.update { it.copy(recentTransactions = result.data) }
            is Result.Error   -> _uiState.update { it.copy(error = result.message) }
            is Result.Loading -> Unit
        }
    }

    companion object {
        fun currentMonthString(): String {
            val cal = Calendar.getInstance()
            return "%04d-%02d".format(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
        }

        fun monthOffset(month: String, offset: Int): String {
            val (year, mon) = month.split("-").map { it.toInt() }
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, mon - 1)
                add(Calendar.MONTH, offset)
            }
            return "%04d-%02d".format(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
        }

        fun formatMonthDisplay(month: String): String {
            val (year, mon) = month.split("-").map { it.toInt() }
            val name = listOf(
                "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
            )[mon - 1]
            return "$name $year"
        }
    }
}

data class HomeUiState(
    val selectedMonth: String = "",
    val isLoading: Boolean = false,
    val stats: FinanceStats? = null,
    val categoryExpenses: List<CategoryExpense> = emptyList(),
    val monthlyStats: List<MonthlyStats> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),
    val error: String? = null
)
