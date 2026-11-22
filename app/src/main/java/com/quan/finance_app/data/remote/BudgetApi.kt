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
}
