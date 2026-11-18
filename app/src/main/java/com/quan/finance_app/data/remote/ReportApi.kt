package com.quan.finance_app.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ReportApi {

    @GET("api/stats/monthly-trend")
    suspend fun getMonthlyTrend(
        @Query("period") period: String,
        @Query("months") months: Int = 5,
        @Query("type") type: String = "INCOME",
        @Query("categoryId") categoryId: String? = null
    ): Response<MonthlyTrendDto>

    @GET("api/stats/transactions")
    suspend fun getTransactions(
        @Query("period") period: String,
        @Query("date") date: String? = null,
        @Query("type") type: String? = null,
        @Query("categoryId") categoryId: String? = null
    ): Response<MonthlyTransactionsDto>
}
