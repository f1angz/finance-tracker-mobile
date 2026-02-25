package mobile.tracker.finance.data.repository

import mobile.tracker.finance.data.models.Category
import mobile.tracker.finance.data.models.CategoryFilter
import mobile.tracker.finance.utils.Result

/**
 * Интерфейс репозитория для работы с категориями
 */
interface CategoryRepository {

    /**
     * Получить список категорий с фильтрацией по типу
     * @param filter Фильтр по типу категории (расходы / доходы / прочие)
     */
    suspend fun getCategories(filter: CategoryFilter): Result<List<Category>>
}
