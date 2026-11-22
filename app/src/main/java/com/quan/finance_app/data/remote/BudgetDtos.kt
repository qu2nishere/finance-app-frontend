package com.quan.finance_app.data.remote

/**
 * DTO cho budget-service:
 * - backend/budget-service/src/main/java/com/quan/budgetservice/budget/dto/CurrentBudgetResponse.java
 * - backend/budget-service/src/main/java/com/quan/budgetservice/budget/dto/CategoryLimitResponse.java
 * - backend/budget-service/src/main/java/com/quan/budgetservice/budget/dto/CreateLimitRequest.java
 * - backend/budget-service/src/main/java/com/quan/budgetservice/budget/dto/UpdateLimitRequest.java
 * - backend/budget-service/src/main/java/com/quan/budgetservice/goal/dto/GoalResponse.java
 * - backend/budget-service/src/main/java/com/quan/budgetservice/goal/dto/CreateGoalRequest.java
 * - backend/budget-service/src/main/java/com/quan/budgetservice/goal/dto/UpdateGoalRequest.java
 * - backend/budget-service/src/main/java/com/quan/budgetservice/goal/dto/GoalDepositResponse.java
 * - backend/budget-service/src/main/java/com/quan/budgetservice/goal/dto/CreateDepositRequest.java
 * - backend/budget-service/src/main/java/com/quan/budgetservice/goal/dto/DepositResultResponse.java
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

data class GoalResponseDto(
    val id: String?,
    val name: String?,
    val icon: String?,
    val color: String?,
    val targetAmount: Long?,
    val savedAmount: Long?,
    val targetDate: String?,
    val walletId: String?,
    val walletName: String?,
    val priority: Int?,
    val status: String?,
    val completedAt: String?,
    val note: String?
)

data class CreateGoalRequestDto(
    val name: String,
    val icon: String? = null,
    val color: String? = null,
    val targetAmount: Long,
    val targetDate: String? = null,
    val walletId: String? = null,
    val walletName: String? = null,
    val priority: Int? = null,
    val note: String? = null
)

data class UpdateGoalRequestDto(
    val name: String? = null,
    val icon: String? = null,
    val color: String? = null,
    val targetAmount: Long? = null,
    val targetDate: String? = null,
    val walletId: String? = null,
    val walletName: String? = null,
    val priority: Int? = null,
    val note: String? = null
)

data class GoalDepositResponseDto(
    val id: String?,
    val amount: Long?,
    val depositedAt: String?,
    val note: String?
)

data class CreateDepositRequestDto(
    val amount: Long,
    val note: String? = null
)

data class DepositResultResponseDto(
    val deposit: GoalDepositResponseDto?,
    val goal: GoalResponseDto?
)
