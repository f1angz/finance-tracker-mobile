package mobile.tracker.finance.data.repository

import kotlinx.coroutines.delay
import mobile.tracker.finance.data.models.Category
import mobile.tracker.finance.data.models.CategoryFilter
import mobile.tracker.finance.data.models.CategoryType
import mobile.tracker.finance.utils.Result

/**
 * Мок-репозиторий для работы с категориями
 * Используется до подключения реального бекенда
 */
class MockCategoryRepository : CategoryRepository {

    private val allCategories = listOf(
        // ─── Расходы ─────────────────────────────────────────────────────────
        Category(
            id = "cat_1",
            name = "Продукты",
            slug = "products",
            operationsCount = 145,
            totalAmount = 52300.0,
            type = CategoryType.EXPENSE
        ),
        Category(
            id = "cat_2",
            name = "Транспорт",
            slug = "transport",
            operationsCount = 89,
            totalAmount = 28900.0,
            type = CategoryType.EXPENSE
        ),
        Category(
            id = "cat_3",
            name = "Здоровье",
            slug = "health",
            operationsCount = 34,
            totalAmount = 45200.0,
            type = CategoryType.EXPENSE
        ),
        Category(
            id = "cat_4",
            name = "Одежда",
            slug = "clothing",
            operationsCount = 23,
            totalAmount = 31400.0,
            type = CategoryType.EXPENSE
        ),
        Category(
            id = "cat_5",
            name = "Развлечения",
            slug = "entertainment",
            operationsCount = 67,
            totalAmount = 38900.0,
            type = CategoryType.EXPENSE
        ),
        Category(
            id = "cat_6",
            name = "Коммунальные",
            slug = "utilities",
            operationsCount = 12,
            totalAmount = 19500.0,
            type = CategoryType.EXPENSE
        ),
        // ─── Доходы ──────────────────────────────────────────────────────────
        Category(
            id = "cat_7",
            name = "Зарплата",
            slug = "salary",
            operationsCount = 12,
            totalAmount = 320000.0,
            type = CategoryType.INCOME
        ),
        Category(
            id = "cat_8",
            name = "Фриланс",
            slug = "freelance",
            operationsCount = 5,
            totalAmount = 75000.0,
            type = CategoryType.INCOME
        ),
        // ─── Прочие ──────────────────────────────────────────────────────────
        Category(
            id = "cat_9",
            name = "Прочее",
            slug = "other",
            operationsCount = 8,
            totalAmount = 5200.0,
            type = CategoryType.OTHER
        )
    )

    override suspend fun getCategories(filter: CategoryFilter): Result<List<Category>> {
        delay(400)
        val type = when (filter) {
            CategoryFilter.EXPENSE -> CategoryType.EXPENSE
            CategoryFilter.INCOME  -> CategoryType.INCOME
            CategoryFilter.OTHER   -> CategoryType.OTHER
        }
        return Result.Success(allCategories.filter { it.type == type })
    }
}
