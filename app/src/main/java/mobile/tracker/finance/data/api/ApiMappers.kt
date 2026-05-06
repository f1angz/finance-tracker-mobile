package mobile.tracker.finance.data.api

import mobile.tracker.finance.data.api.dto.ApiCategoryExpenseDto
import mobile.tracker.finance.data.api.dto.ApiTransactionDto
import mobile.tracker.finance.data.models.CategoryExpense
import mobile.tracker.finance.data.models.Transaction
import mobile.tracker.finance.data.models.TransactionCategory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ─── ApiTransactionDto ↔ Transaction ──────────────────────────────────────────

public fun ApiTransactionDto.toDomain(): Transaction {
    val knownEnum = category.toTransactionCategory()
    val slug = if (knownEnum != TransactionCategory.OTHER) knownEnum.toSlug() else category.lowercase(Locale.getDefault())
    val name = if (knownEnum != TransactionCategory.OTHER) knownEnum.displayName
               else category.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    return Transaction(
        id           = id ?: "",
        title        = title,
        description  = description,
        amount       = amount,
        categorySlug = slug,
        categoryName = name,
        date         = date.toDateLabel(),
        time         = time,
        type         = type
    )
}

public fun Transaction.toApiDto(): ApiTransactionDto = ApiTransactionDto(
    id          = id.ifBlank { null },
    title       = title,
    description = description,
    amount      = kotlin.math.abs(amount),
    category    = categorySlug,
    date        = date.toIsoDate(),
    time        = time,
    type        = type
)

// ─── ApiCategoryExpenseDto → CategoryExpense ──────────────────────────────────

fun ApiCategoryExpenseDto.toDomain(): CategoryExpense {
    val name = categoryName?.takeIf { it.isNotBlank() }
        ?: run {
            val knownEnum = category.toTransactionCategory()
            if (knownEnum != TransactionCategory.OTHER) knownEnum.displayName
            else category.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
    return CategoryExpense(categoryName = name, amount = amount, percentage = percentage)
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

/** Slug бекенда ("products") или русское название ("Продукты") → enum Android */
fun String.toTransactionCategory(): TransactionCategory = when (lowercase(Locale.getDefault())) {
    "products",      "продукты"    -> TransactionCategory.PRODUCTS
    "entertainment", "развлечение", "развлечения" -> TransactionCategory.ENTERTAINMENT
    "clothing",      "одежда"      -> TransactionCategory.CLOTHING
    "transport",     "транспорт"   -> TransactionCategory.TRANSPORT
    "health",        "здоровье"    -> TransactionCategory.HEALTH
    "salary",        "зарплата"    -> TransactionCategory.SALARY
    "freelance",     "фриланс"     -> TransactionCategory.FREELANCE
    else                           -> TransactionCategory.OTHER
}

/** Enum Android → slug для отправки на бекенд */
fun TransactionCategory.toSlug(): String = name.lowercase(Locale.getDefault())

/**
 * ISO-дата "2026-03-03" → русская метка ("Сегодня", "Вчера", "2 дня назад").
 * Если формат не распознан, возвращает строку как есть.
 */
fun String.toDateLabel(): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val txDate = sdf.parse(this) ?: return this

        val todayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val daysDiff = ((todayCal.timeInMillis - txDate.time) / (1000L * 60 * 60 * 24)).toInt()
        when {
            daysDiff == 0 -> "Сегодня"
            daysDiff == 1 -> "Вчера"
            daysDiff in 2..6 -> "$daysDiff дня назад"
            else -> this
        }
    } catch (e: Exception) {
        this
    }
}

/**
 * Русская метка ("Сегодня") → ISO-дата "YYYY-MM-DD" для отправки на бекенд.
 * Если строка уже в ISO-формате, возвращает её без изменений.
 */
fun String.toIsoDate(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return try {
        sdf.parse(this) // уже ISO
        this
    } catch (e: Exception) {
        val cal = Calendar.getInstance()
        when (this) {
            "Вчера" -> cal.add(Calendar.DAY_OF_YEAR, -1)
            else    -> Unit // "Сегодня" или неизвестная метка — используем сегодня
        }
        sdf.format(cal.time)
    }
}
