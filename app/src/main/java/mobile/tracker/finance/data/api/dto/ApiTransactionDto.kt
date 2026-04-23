package mobile.tracker.finance.data.api.dto

import mobile.tracker.finance.data.models.TransactionType

data class ApiTransactionDto(
    val id: String? = null,
    val title: String,
    val description: String = "",
    val amount: Double,
    val category: String,
    val date: String,
    val time: String = "",
    val type: TransactionType
)
