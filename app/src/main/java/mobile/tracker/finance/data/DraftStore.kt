package mobile.tracker.finance.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import mobile.tracker.finance.data.models.TransactionType
import mobile.tracker.finance.ui.screens.operations.TransactionDraft

private val Context.draftDataStore by preferencesDataStore(name = "finance_draft")

object DraftStore {

    private val TYPE_KEY     = stringPreferencesKey("draft_type")
    private val TITLE_KEY    = stringPreferencesKey("draft_title")
    private val AMOUNT_KEY   = stringPreferencesKey("draft_amount")
    private val CATEGORY_KEY = stringPreferencesKey("draft_category")
    private val DATE_KEY     = longPreferencesKey("draft_date")
    private val COMMENT_KEY  = stringPreferencesKey("draft_comment")

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    suspend fun save(draft: TransactionDraft) {
        appContext.draftDataStore.edit { p ->
            p[TYPE_KEY]     = draft.type.name
            p[TITLE_KEY]    = draft.title
            p[AMOUNT_KEY]   = draft.amountText
            p[CATEGORY_KEY] = draft.categorySlug ?: ""
            p[DATE_KEY]     = draft.dateMillis
            p[COMMENT_KEY]  = draft.comment
        }
    }

    suspend fun load(): TransactionDraft? {
        val p = appContext.draftDataStore.data.first()
        val title    = p[TITLE_KEY]    ?: ""
        val amount   = p[AMOUNT_KEY]   ?: ""
        val category = p[CATEGORY_KEY] ?: ""
        val comment  = p[COMMENT_KEY]  ?: ""
        if (title.isBlank() && amount.isBlank() && category.isBlank() && comment.isBlank()) return null
        return TransactionDraft(
            type = p[TYPE_KEY]?.let { runCatching { TransactionType.valueOf(it) }.getOrDefault(TransactionType.EXPENSE) }
                ?: TransactionType.EXPENSE,
            title      = title,
            amountText = amount,
            categorySlug = category.ifBlank { null },
            dateMillis = p[DATE_KEY] ?: System.currentTimeMillis(),
            comment    = comment
        )
    }

    suspend fun clear() {
        appContext.draftDataStore.edit { it.clear() }
    }
}
