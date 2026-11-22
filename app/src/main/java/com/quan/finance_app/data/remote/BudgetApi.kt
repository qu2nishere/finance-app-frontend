package com.quan.finance_app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BudgetApi {

    @GET("api/budgets/current")
    suspend fun getCurrentBudget(
        @Query("period") period: String? = null
    ): Response<CurrentBudgetDto>

    @POST("api/limits")
    suspend fun createLimit(
        @Body body: CreateLimitRequestDto
    ): Response<CategoryLimitDto>

    @PATCH("api/limits/{id}")
    suspend fun updateLimit(
        @Path("id") id: String,
        @Body body: UpdateLimitRequestDto
    ): Response<CategoryLimitDto>

    @DELETE("api/limits/{id}")
    suspend fun deleteLimit(
        @Path("id") id: String
    ): Response<Unit>

    @GET("api/goals")
    suspend fun getGoals(
        @Query("status") status: String? = null
    ): Response<List<GoalResponseDto>>

    @POST("api/goals")
    suspend fun createGoal(
        @Body body: CreateGoalRequestDto
    ): Response<GoalResponseDto>

    @GET("api/goals/{id}")
    suspend fun getGoal(
        @Path("id") id: String
    ): Response<GoalResponseDto>

    @PATCH("api/goals/{id}")
    suspend fun updateGoal(
        @Path("id") id: String,
        @Body body: UpdateGoalRequestDto
    ): Response<GoalResponseDto>

    @DELETE("api/goals/{id}")
    suspend fun deleteGoal(
        @Path("id") id: String
    ): Response<Unit>

    @POST("api/goals/{id}/deposits")
    suspend fun depositToGoal(
        @Path("id") id: String,
        @Body body: CreateDepositRequestDto
    ): Response<DepositResultResponseDto>

    @GET("api/goals/{id}/deposits")
    suspend fun getGoalDeposits(
        @Path("id") id: String
    ): Response<List<GoalDepositResponseDto>>
}
