package mobile.tracker.finance.ui.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobile.tracker.finance.data.models.Debt
import mobile.tracker.finance.data.models.Goal
import mobile.tracker.finance.data.models.Transaction
import mobile.tracker.finance.data.repository.ApiDebtRepository
import mobile.tracker.finance.data.repository.ApiFinanceRepository
import mobile.tracker.finance.data.repository.ApiGoalRepository
import mobile.tracker.finance.data.repository.DebtRepository
import mobile.tracker.finance.data.repository.FinanceRepository
import mobile.tracker.finance.data.repository.GoalRepository
import mobile.tracker.finance.utils.Result

enum class GoalsTab { GOALS, DEBTS }

data class GoalsUiState(
    val goals: List<Goal> = emptyList(),
    val debts: List<Debt> = emptyList(),
    val selectedTab: GoalsTab = GoalsTab.GOALS,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val activeDebts: List<Debt> get() = debts.filter { !it.isPaid }
    val paidDebts: List<Debt>   get() = debts.filter { it.isPaid }
}

class GoalsViewModel : ViewModel() {

    private val goalRepository: GoalRepository = ApiGoalRepository()
    private val debtRepository: DebtRepository = ApiDebtRepository()
    private val financeRepository: FinanceRepository = ApiFinanceRepository()

    private val _uiState = MutableStateFlow(GoalsUiState(isLoading = true))
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val goalsDeferred = async { goalRepository.getGoals() }
            val debtsDeferred = async { debtRepository.getDebts() }

            val goals = when (val r = goalsDeferred.await()) {
                is Result.Success -> r.data
                is Result.Error -> {
                    _uiState.update { it.copy(error = r.message, isLoading = false) }
                    return@launch
                }
                else -> emptyList()
            }

            val debts = when (val r = debtsDeferred.await()) {
                is Result.Success -> r.data
                is Result.Error -> {
                    _uiState.update { it.copy(error = r.message, isLoading = false) }
                    return@launch
                }
                else -> emptyList()
            }

            _uiState.update { it.copy(goals = goals, debts = debts, isLoading = false) }
        }
    }

    fun selectTab(tab: GoalsTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch { financeRepository.addTransaction(transaction) }
    }

    fun onAddGoal() {
        // TODO: открыть диалог добавления цели
    }

    fun onAddDebt() {
        // TODO: открыть диалог добавления долга
    }

    fun onContribute(goalId: String) {
        // TODO: открыть диалог пополнения цели
    }

    fun onRepayDebt(debtId: String) {
        // TODO: открыть диалог погашения долга
    }
}
