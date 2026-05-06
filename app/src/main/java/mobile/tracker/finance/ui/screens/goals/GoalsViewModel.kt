package mobile.tracker.finance.ui.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobile.tracker.finance.data.models.CreateDebtRequest
import mobile.tracker.finance.data.models.CreateGoalRequest
import mobile.tracker.finance.data.models.Debt
import mobile.tracker.finance.data.models.DebtType
import mobile.tracker.finance.data.models.Goal
import mobile.tracker.finance.data.models.GoalContributionRequest
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
    val error: String? = null,
    val showAddGoalSheet: Boolean = false,
    val showAddDebtSheet: Boolean = false,
    val contributeGoalId: String? = null,
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

    init { loadData() }

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

    fun selectTab(tab: GoalsTab) = _uiState.update { it.copy(selectedTab = tab) }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch { financeRepository.addTransaction(transaction) }
    }

    // ── Goals ─────────────────────────────────────────────────────────────────

    fun onAddGoal() = _uiState.update { it.copy(showAddGoalSheet = true) }
    fun onDismissAddGoal() = _uiState.update { it.copy(showAddGoalSheet = false) }

    fun createGoal(emoji: String, title: String, targetAmount: Double, targetDate: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(showAddGoalSheet = false) }
            val req = CreateGoalRequest(
                emoji = emoji,
                title = title,
                targetAmount = targetAmount,
                targetDate = targetDate
            )
            when (val r = goalRepository.createGoal(req)) {
                is Result.Success -> _uiState.update { s -> s.copy(goals = s.goals + r.data) }
                is Result.Error   -> _uiState.update { it.copy(error = r.message) }
                else -> {}
            }
        }
    }

    fun onContribute(goalId: String) = _uiState.update { it.copy(contributeGoalId = goalId) }
    fun onDismissContribute() = _uiState.update { it.copy(contributeGoalId = null) }

    fun contribute(amount: Double) {
        val goalId = _uiState.value.contributeGoalId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(contributeGoalId = null) }
            when (val r = goalRepository.addContribution(goalId, GoalContributionRequest(amount))) {
                is Result.Success -> _uiState.update { s ->
                    s.copy(goals = s.goals.map { if (it.id == goalId) r.data else it })
                }
                is Result.Error -> _uiState.update { it.copy(error = r.message) }
                else -> {}
            }
        }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            goalRepository.deleteGoal(goalId)
            _uiState.update { s -> s.copy(goals = s.goals.filter { it.id != goalId }) }
        }
    }

    // ── Debts ─────────────────────────────────────────────────────────────────

    fun onAddDebt() = _uiState.update { it.copy(showAddDebtSheet = true) }
    fun onDismissAddDebt() = _uiState.update { it.copy(showAddDebtSheet = false) }

    fun createDebt(personName: String, type: DebtType, amount: Double, dueDate: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(showAddDebtSheet = false) }
            val req = CreateDebtRequest(
                personName = personName,
                type = type,
                amount = amount,
                dueDate = dueDate
            )
            when (val r = debtRepository.createDebt(req)) {
                is Result.Success -> _uiState.update { s -> s.copy(debts = s.debts + r.data) }
                is Result.Error   -> _uiState.update { it.copy(error = r.message) }
                else -> {}
            }
        }
    }

    fun onRepayDebt(debtId: String) {
        viewModelScope.launch {
            when (val r = debtRepository.markAsPaid(debtId)) {
                is Result.Success -> _uiState.update { s ->
                    s.copy(debts = s.debts.map { if (it.id == debtId) r.data else it })
                }
                is Result.Error -> _uiState.update { it.copy(error = r.message) }
                else -> {}
            }
        }
    }

    fun deleteDebt(debtId: String) {
        viewModelScope.launch {
            debtRepository.deleteDebt(debtId)
            _uiState.update { s -> s.copy(debts = s.debts.filter { it.id != debtId }) }
        }
    }
}
