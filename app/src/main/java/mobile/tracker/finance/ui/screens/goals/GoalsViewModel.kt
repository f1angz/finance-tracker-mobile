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
import mobile.tracker.finance.data.repository.DebtRepository
import mobile.tracker.finance.data.repository.GoalRepository
import mobile.tracker.finance.data.repository.MockDebtRepository
import mobile.tracker.finance.data.repository.MockGoalRepository
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
    val paidDebts: List<Debt> get() = debts.filter { it.isPaid }
}

class GoalsViewModel : ViewModel() {

    // TODO: заменить на DI-инъекцию при подключении реального бекенда
    private val goalRepository: GoalRepository = MockGoalRepository()
    private val debtRepository: DebtRepository = MockDebtRepository()

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

            val goalsResult = goalsDeferred.await()
            val debtsResult = debtsDeferred.await()

            val goals = when (goalsResult) {
                is Result.Success -> goalsResult.data
                is Result.Error -> {
                    _uiState.update { it.copy(error = goalsResult.message, isLoading = false) }
                    return@launch
                }
                else -> emptyList()
            }

            val debts = when (debtsResult) {
                is Result.Success -> debtsResult.data
                is Result.Error -> {
                    _uiState.update { it.copy(error = debtsResult.message, isLoading = false) }
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

    fun onAddGoal() {
        // TODO: открыть диалог / экран добавления цели
    }

    fun onAddDebt() {
        // TODO: открыть диалог / экран добавления долга
    }

    fun onContribute(goalId: String) {
        // TODO: открыть диалог пополнения цели
    }

    fun onRepayDebt(debtId: String) {
        // TODO: открыть диалог погашения долга
    }
}
