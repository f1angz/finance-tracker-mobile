package mobile.tracker.finance.ui.screens.operations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mobile.tracker.finance.data.DraftStore
import mobile.tracker.finance.data.models.TransactionType

data class TransactionDraft(
    val type: TransactionType = TransactionType.EXPENSE,
    val title: String = "",
    val amountText: String = "",
    val categorySlug: String? = null,
    val dateMillis: Long = System.currentTimeMillis(),
    val comment: String = ""
) {
    fun isEmpty() = title.isBlank() && amountText.isBlank() && categorySlug == null && comment.isBlank()
}

class DraftViewModel : ViewModel() {

    private val _draft = MutableStateFlow<TransactionDraft?>(null)
    val draft: StateFlow<TransactionDraft?> = _draft.asStateFlow()

    init {
        viewModelScope.launch {
            _draft.value = DraftStore.load()
        }
    }

    fun saveDraft(draft: TransactionDraft) {
        if (draft.isEmpty()) {
            clearDraft()
        } else {
            _draft.value = draft
            viewModelScope.launch { DraftStore.save(draft) }
        }
    }

    fun clearDraft() {
        _draft.value = null
        viewModelScope.launch { DraftStore.clear() }
    }
}
