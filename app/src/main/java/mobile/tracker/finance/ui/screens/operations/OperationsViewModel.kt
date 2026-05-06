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
import mobile.tracker.finance.data.models.Transaction
import mobile.tracker.finance.data.models.TransactionFilter
import mobile.tracker.finance.data.models.TransactionGroup
import mobile.tracker.finance.data.repository.ApiFinanceRepository
import mobile.tracker.finance.data.repository.FinanceRepository
import mobile.tracker.finance.utils.Result
import java.text.SimpleDateFormat
import java.util.*

// ─── Фильтр по дате ──────────────────────────────────────────────────────────

enum class DatePreset(val label: String) {
    ALL("Все время"),
    TODAY("Сегодня"),
    THIS_WEEK("Эта неделя"),
    THIS_MONTH("Этот месяц"),
    LAST_MONTH("Прошлый месяц"),
    CUSTOM("Период")
}

// ─── Расширенный фильтр операций ─────────────────────────────────────────────

data class OperationsExtraFilter(
    val datePreset: DatePreset = DatePreset.ALL,
    val dateFrom: Long? = null,
    val dateTo: Long? = null,
    val categorySlug: String? = null,
    val amountMin: Double? = null,
    val amountMax: Double? = null
) {
    val isActive: Boolean
        get() = datePreset != DatePreset.ALL || categorySlug != null ||
                amountMin != null || amountMax != null

    fun toDateRange(): Pair<Calendar, Calendar>? {
        fun today() = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0);      set(Calendar.MILLISECOND, 0)
        }
        return when (datePreset) {
            DatePreset.ALL -> null
            DatePreset.TODAY -> Pair(
                today(),
                today().apply { add(Calendar.DAY_OF_YEAR, 1) }
            )
            DatePreset.THIS_WEEK -> {
                val monday = today().apply {
                    val dow      = get(Calendar.DAY_OF_WEEK)
                    val daysBack = (dow - Calendar.MONDAY + 7) % 7
                    add(Calendar.DAY_OF_YEAR, -daysBack)
                }
                Pair(monday, today().apply { add(Calendar.DAY_OF_YEAR, 1) })
            }
            DatePreset.THIS_MONTH -> Pair(
                today().apply { set(Calendar.DAY_OF_MONTH, 1) },
                today().apply { add(Calendar.DAY_OF_YEAR, 1) }
            )
            DatePreset.LAST_MONTH -> Pair(
                today().apply { add(Calendar.MONTH, -1); set(Calendar.DAY_OF_MONTH, 1) },
                today().apply { set(Calendar.DAY_OF_MONTH, 1) }
            )
            DatePreset.CUSTOM -> {
                if (dateFrom != null && dateTo != null) {
                    val from = Calendar.getInstance().apply {
                        timeInMillis = dateFrom
                        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0);      set(Calendar.MILLISECOND, 0)
                    }
                    val to = Calendar.getInstance().apply {
                        timeInMillis = dateTo
                        set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                    }
                    Pair(from, to)
                } else null
            }
        }
    }
}

// ─── ViewModel ───────────────────────────────────────────────────────────────

class OperationsViewModel(
    private val repository: FinanceRepository = ApiFinanceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(OperationsUiState())
    val uiState: StateFlow<OperationsUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadTransactions()
    }

    fun onFilterChanged(filter: TransactionFilter) {
        _uiState.update { it.copy(activeFilter = filter) }
        loadTransactions()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            loadTransactions()
        }
    }

    fun onExtraFilterChanged(filter: OperationsExtraFilter) {
        _uiState.update { it.copy(extraFilter = filter) }
        loadTransactions()
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.addTransaction(transaction)
            loadTransactions()
        }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
            loadTransactions()
        }
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val state = _uiState.value
            when (val result = repository.getTransactions(state.activeFilter, state.searchQuery)) {
                is Result.Success -> {
                    val filtered = result.data.applyExtraFilter(state.extraFilter)
                    _uiState.update { it.copy(transactionGroups = filtered, isLoading = false) }
                }
                is Result.Error   -> _uiState.update { it.copy(error = result.message, isLoading = false) }
                is Result.Loading -> Unit
            }
        }
    }

    // ─── Клиентская фильтрация ────────────────────────────────────────────────

    private fun List<TransactionGroup>.applyExtraFilter(filter: OperationsExtraFilter): List<TransactionGroup> {
        if (!filter.isActive) return this
        val dateRange = filter.toDateRange()
        return mapNotNull { group ->
            if (dateRange != null) {
                val groupCal = group.dateLabel.toDateCal() ?: return@mapNotNull null
                val (from, to) = dateRange
                if (groupCal.before(from) || !groupCal.before(to)) return@mapNotNull null
            }
            val filtered = group.transactions.filter { tx ->
                val amount     = kotlin.math.abs(tx.amount)
                val amountOk   = (filter.amountMin == null || amount >= filter.amountMin) &&
                                 (filter.amountMax == null || amount <= filter.amountMax)
                val categoryOk = filter.categorySlug == null || tx.categorySlug == filter.categorySlug
                amountOk && categoryOk
            }
            if (filtered.isEmpty()) null else group.copy(transactions = filtered)
        }
    }

    private fun String.toDateCal(): Calendar? {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0);      set(Calendar.MILLISECOND, 0)
        }
        return when {
            this == "Сегодня" -> today
            this == "Вчера"   -> (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
            matches(Regex("\\d+ дня назад")) -> {
                val days = filter { it.isDigit() }.toIntOrNull() ?: return null
                (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -days) }
            }
            else -> try {
                Calendar.getInstance().apply {
                    time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .parse(this@toDateCal) ?: return null
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0);      set(Calendar.MILLISECOND, 0)
                }
            } catch (e: Exception) { null }
        }
    }
}

// ─── UI State ─────────────────────────────────────────────────────────────────

data class OperationsUiState(
    val isLoading: Boolean = false,
    val transactionGroups: List<TransactionGroup> = emptyList(),
    val activeFilter: TransactionFilter = TransactionFilter.ALL,
    val searchQuery: String = "",
    val extraFilter: OperationsExtraFilter = OperationsExtraFilter(),
    val error: String? = null
)
