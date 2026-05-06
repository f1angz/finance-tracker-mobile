package mobile.tracker.finance.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobile.tracker.finance.data.models.Category
import mobile.tracker.finance.data.models.CategoryFilter
import mobile.tracker.finance.data.models.Transaction
import mobile.tracker.finance.data.repository.ApiCategoryRepository
import mobile.tracker.finance.data.repository.ApiFinanceRepository
import mobile.tracker.finance.data.repository.CategoryRepository
import mobile.tracker.finance.data.repository.FinanceRepository
import mobile.tracker.finance.utils.Result

data class CategoriesUiState(
    val isLoading: Boolean = true,
    val categories: List<Category> = emptyList(),
    val activeFilter: CategoryFilter = CategoryFilter.EXPENSE,
    val counts: Map<CategoryFilter, Int> = emptyMap(),
    val error: String? = null,
    val editingCategory: Category? = null,
)

class CategoriesViewModel(
    private val repository: CategoryRepository = ApiCategoryRepository(),
    private val financeRepository: FinanceRepository = ApiFinanceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    private val cachedByFilter = mutableMapOf<CategoryFilter, List<Category>>()

    // Загружаем только EXPENSE и INCOME — OTHER убран из UI
    private val visibleFilters = listOf(CategoryFilter.EXPENSE, CategoryFilter.INCOME)

    init {
        loadAllCategories()
    }

    private fun loadAllCategories(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) _uiState.update { it.copy(isLoading = true, error = null) }

            var errorMessage: String? = null
            for (filter in visibleFilters) {
                when (val result = repository.getCategories(filter)) {
                    is Result.Success -> cachedByFilter[filter] = result.data
                    is Result.Error   -> errorMessage = result.message
                    else              -> Unit
                }
            }

            val counts = visibleFilters.associateWith { (cachedByFilter[it] ?: emptyList()).size }
            val activeFilter = _uiState.value.activeFilter

            _uiState.update {
                it.copy(
                    isLoading = false,
                    categories = cachedByFilter[activeFilter] ?: emptyList(),
                    counts = counts,
                    error = errorMessage
                )
            }
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            financeRepository.addTransaction(transaction)
            // Перезагружаем, чтобы обновились totalAmount и operationsCount
            cachedByFilter.clear()
            loadAllCategories(showLoading = false)
        }
    }

    fun addCategory(category: Category) {
        // Оптимистично показываем категорию сразу
        _uiState.update { state ->
            val shouldShow = category.type.name == state.activeFilter.name
            val newList = if (shouldShow) state.categories + category else state.categories
            val newCounts = state.counts.toMutableMap().also { map ->
                val filterKey = CategoryFilter.entries.firstOrNull { it.name == category.type.name }
                if (filterKey != null) map[filterKey] = (map[filterKey] ?: 0) + 1
            }
            state.copy(categories = newList, counts = newCounts)
        }
        // Синхронизируем с бекендом в фоне без спиннера
        viewModelScope.launch {
            repository.addCategory(category)
            cachedByFilter.clear()
            loadAllCategories(showLoading = false)
        }
    }

    fun onEditCategory(category: Category) =
        _uiState.update { it.copy(editingCategory = category) }

    fun onDismissEdit() =
        _uiState.update { it.copy(editingCategory = null) }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            _uiState.update { it.copy(editingCategory = null) }
            repository.updateCategory(category)
            cachedByFilter.clear()
            loadAllCategories()
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(editingCategory = null) }
            repository.deleteCategory(id)
            cachedByFilter.clear()
            loadAllCategories()
        }
    }

    fun reloadCategories() {
        cachedByFilter.clear()
        loadAllCategories(showLoading = false)
    }

    fun onFilterChanged(filter: CategoryFilter) {
        _uiState.update {
            it.copy(activeFilter = filter, categories = cachedByFilter[filter] ?: emptyList())
        }
    }
}
