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
import mobile.tracker.finance.data.repository.CategoryRepository
import mobile.tracker.finance.data.repository.MockCategoryRepository
import mobile.tracker.finance.utils.Result

data class CategoriesUiState(
    val isLoading: Boolean = true,
    val categories: List<Category> = emptyList(),
    val activeFilter: CategoryFilter = CategoryFilter.EXPENSE,
    val counts: Map<CategoryFilter, Int> = emptyMap(),
    val error: String? = null
)

class CategoriesViewModel(
    private val repository: CategoryRepository = MockCategoryRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    // Кэш всех категорий по фильтрам (избегаем повторных запросов)
    private val cachedByFilter = mutableMapOf<CategoryFilter, List<Category>>()

    init {
        loadAllCategories()
    }

    private fun loadAllCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            var errorMessage: String? = null

            for (filter in CategoryFilter.values()) {
                when (val result = repository.getCategories(filter)) {
                    is Result.Success -> cachedByFilter[filter] = result.data
                    is Result.Error   -> errorMessage = result.message
                    else              -> Unit
                }
            }

            val counts = CategoryFilter.values().associateWith {
                (cachedByFilter[it] ?: emptyList()).size
            }
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

    fun onFilterChanged(filter: CategoryFilter) {
        _uiState.update {
            it.copy(
                activeFilter = filter,
                categories = cachedByFilter[filter] ?: emptyList()
            )
        }
    }
}
