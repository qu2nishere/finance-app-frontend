package com.quan.finance_app.data.remote

/**
 * Khớp từng ký tự với các record DTO của report-service:
 * - backend/report-service/src/main/java/com/quan/reportservice/stats/dto/MonthlyTrendResponse.java
 * - backend/report-service/src/main/java/com/quan/reportservice/stats/dto/MonthlyBarPointResponse.java
 * - backend/report-service/src/main/java/com/quan/reportservice/stats/dto/CategoryBreakdownItemResponse.java
 * - backend/report-service/src/main/java/com/quan/reportservice/stats/dto/MonthlyTransactionsResponse.java
 * - backend/report-service/src/main/java/com/quan/reportservice/stats/dto/DailyTransactionGroupResponse.java
 * - backend/report-service/src/main/java/com/quan/reportservice/stats/dto/ReportTransactionItemResponse.java
 */

data class MonthlyBarPointDto(
    val period: String,
    val year: Int,
    val month: Int,
    val totalAmount: Long,
    val txnCount: Int,
    val isSelected: Boolean
)

data class CategoryBreakdownItemDto(
    val categoryId: String? = null,
    val categoryName: String,
    val categoryIcon: String? = null,
    val categoryColor: String? = null,
    val totalAmount: Long,
    val txnCount: Int,
    val sharePercent: Double? = null,
    val momChangePercent: Double? = null
)

data class MonthlyTrendDto(
    val period: String,
    val type: String,
    val categoryId: String? = null,
    val currencyCode: String? = "VND",
    val selectedAmount: Long,
    val previousAmount: Long,
    val momChangeAmount: Long,
    val momChangePercent: Double? = null,
    val points: List<MonthlyBarPointDto>? = emptyList(),
    val categoryBreakdown: List<CategoryBreakdownItemDto>? = emptyList()
)

data class ReportTransactionItemDto(
    val transactionId: String,
    val type: String,
    val amount: Long,
    val signedAmount: Long,
    val currencyCode: String? = "VND",
    val occurredAt: String,
    val occurredDate: String,
    val categoryId: String? = null,
    val categoryName: String? = null,
    val categoryIcon: String? = null,
    val categoryColor: String? = null,
    val walletId: String,
    val walletName: String,
    val merchant: String? = null,
    val note: String? = null
)

data class DailyTransactionGroupDto(
    val date: String,
    val totalAmount: Long,
    val netAmount: Long,
    val txnCount: Int,
    val items: List<ReportTransactionItemDto>? = emptyList()
)

data class MonthlyTransactionsDto(
    val period: String,
    val selectedDate: String? = null,
    val currencyCode: String? = "VND",
    val monthTotalAmount: Long,
    val monthTxnCount: Int,
    val filteredTotalAmount: Long,
    val filteredTxnCount: Int,
    val days: List<DailyTransactionGroupDto>? = emptyList()
)
