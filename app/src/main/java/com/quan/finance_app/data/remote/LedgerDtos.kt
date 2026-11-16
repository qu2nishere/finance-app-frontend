package com.quan.finance_app.data.remote

data class CategoryDto(
    val id: String,
    val parentId: String?,
    val name: String,
    val flow: String, // "EXPENSE", "INCOME"
    val icon: String,
    val color: String,
    val system: Boolean,
    val usageCount: Int
)

data class CreateCategoryDto(
    val name: String,
    val flow: String, // "EXPENSE", "INCOME"
    val icon: String? = "Category",
    val color: String? = "#64748B"
)

data class WalletDto(
    val id: String,
    val name: String,
    val kind: String,
    val currencyCode: String,
    val initialBalance: Long,
    val currentBalance: Long,
    val icon: String,
    val color: String,
    val includeInTotal: Boolean,
    val displayOrder: Int,
    val note: String?
)

data class CreateWalletDto(
    val name: String,
    val kind: String = "CASH",
    val initialBalance: Long = 0L,
    val icon: String? = "AccountBalanceWallet",
    val color: String? = "#2563EB",
    val note: String? = null
)

data class CreateTransactionDto(
    val walletId: String,
    val categoryId: String?,
    val type: String, // "EXPENSE", "INCOME"
    val amount: Long,
    val occurredAt: String, // ISO-8601 string
    val note: String?,
    val merchant: String? = null
)

data class TransactionDto(
    val id: String,
    val walletId: String,
    val walletName: String?,
    val categoryId: String?,
    val categoryName: String?,
    val categoryIcon: String?,
    val categoryColor: String?,
    val type: String,
    val amount: Long,
    val signedAmount: Long,
    val currencyCode: String,
    val occurredAt: String,
    val occurredDate: String,
    val note: String?,
    val merchant: String?
)

data class MonthlySummaryDto(
    val totalIncome: Long,
    val totalExpense: Long,
    val netBalance: Long
)

data class PageDto<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val number: Int,
    val last: Boolean
)
