package mobile.tracker.finance.data.repository

import mobile.tracker.finance.data.api.RetrofitClient
import mobile.tracker.finance.data.models.Category
import mobile.tracker.finance.data.models.CategoryFilter
import mobile.tracker.finance.utils.Result
import retrofit2.Response

class ApiCategoryRepository : CategoryRepository {

    private val api = RetrofitClient.apiService

    override suspend fun getCategories(filter: CategoryFilter): Result<List<Category>> =
        safeCall { api.getCategories(type = filter.name) }

    override suspend fun addCategory(category: Category) {
        try { api.createCategory(category) } catch (_: Exception) {}
    }

    override suspend fun updateCategory(category: Category) {
        try { api.updateCategory(category.id, category) } catch (_: Exception) {}
    }

    override suspend fun deleteCategory(id: String) {
        try { api.deleteCategory(id) } catch (_: Exception) {}
    }

    private suspend fun <T> safeCall(block: suspend () -> Response<T>): Result<T> {
        return try {
            val response = block()
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Пустой ответ от сервера")
            } else {
                Result.Error("Ошибка ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Ошибка подключения: ${e.message}")
        }
    }
}
