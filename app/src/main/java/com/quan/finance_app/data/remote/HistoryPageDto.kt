package com.quan.finance_app.data.remote

data class HistoryFilter(
    val q: String = "",
    val from: String? = null,
    val to: String? = null,
    val walletId: String? = null,
    val categoryId: String? = null,
    val type: String? = null
)

data class HistoryPageDto(
    val items: List<TransactionDto>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val totalsByCurrency: List<CurrencyTotalsDto>
)

data class CurrencyTotalsDto(val currencyCode: String, val totalIncome: Long, val totalExpense: Long)
