package mobile.tracker.finance.data.models

/**
 * Модель категории для экрана "Категории"
 * @param id Уникальный идентификатор
 * @param name Отображаемое название
 * @param slug Ключ для маппинга иконки/цвета и будущей интеграции с API
 * @param operationsCount Количество операций в категории
 * @param totalAmount Суммарный оборот по категории
 * @param type Тип категории (расход / доход / прочее)
 */
data class Category(
    val id: String,
    val name: String,
    val slug: String,
    val operationsCount: Int,
    val totalAmount: Double,
    val type: CategoryType
)

/**
 * Тип категории
 */
enum class CategoryType {
    EXPENSE,  // Расходы
    INCOME,   // Доходы
    OTHER     // Прочие
}

/**
 * Фильтр на экране категорий
 */
enum class CategoryFilter {
    EXPENSE,  // Расходы
    INCOME,   // Доходы
    OTHER     // Прочие
}
