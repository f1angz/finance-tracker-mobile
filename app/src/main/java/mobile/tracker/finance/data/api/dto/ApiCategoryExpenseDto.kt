package mobile.tracker.finance.data.api.dto

data class ApiCategoryExpenseDto(
    val category: String,
    val categoryName: String? = null,
    val amount: Double,
    val percentage: Float
)
