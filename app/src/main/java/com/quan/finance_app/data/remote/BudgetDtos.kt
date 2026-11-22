package com.quan.finance_app.data.remote

/**
 * DTO cho budget-service:
 * - backend/budget-service/src/main/java/com/quan/budgetservice/budget/dto/CurrentBudgetResponse.java
 * - backend/budget-service/src/main/java/com/quan/budgetservice/budget/dto/CategoryLimitResponse.java
 * - backend/budget-service/src/main/java/com/quan/budgetservice/budget/dto/CreateLimitRequest.java
 * - backend/budget-service/src/main/java/com/quan/budgetservice/budget/dto/UpdateLimitRequest.java
 */
data class CurrentBudgetDto(
    val periodKey: String?,
    val periodStart: String?,
    val periodEnd: String?,
    val daysLeft: Int?,
    val totalLimit: Long?,
    val totalSpent: Long?,
    val limits: List<CategoryLimitDto>?
)

data class CategoryLimitDto(
    val id: String?,
    val categoryId: String?,
    val categoryName: String?,
    val categoryIcon: String?,
    val categoryColor: String?,
    val limitAmount: Long?,
    val spentAmount: Long?,
    val alertThreshold: Int?,
    val status: String?
)

data class CreateLimitRequestDto(
    val categoryId: String,
    val categoryName: String,
    val categoryIcon: String? = null,
    val categoryColor: String? = null,
    val limitAmount: Long,
    val alertThreshold: Int? = 80,
    val periodKey: String? = null
)

data class UpdateLimitRequestDto(
    val limitAmount: Long? = null,
    val alertThreshold: Int? = null
)
