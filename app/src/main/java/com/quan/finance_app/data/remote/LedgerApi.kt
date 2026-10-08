package com.quan.finance_app.data.remote

import retrofit2.http.*

interface LedgerApi {

    @GET("api/categories")
    suspend fun getCategories(): List<CategoryDto>

    @POST("api/categories")
    suspend fun createCategory(@Body body: CreateCategoryDto): CategoryDto

    @GET("api/wallets")
    suspend fun getWallets(): List<WalletDto>

    @POST("api/wallets")
    suspend fun createWallet(@Body body: CreateWalletDto): WalletDto

    @GET("api/transactions")
    suspend fun getTransactions(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): PageDto<TransactionDto>

    @GET("api/transactions/summary")
    suspend fun getMonthlySummary(): MonthlySummaryDto

    @POST("api/transactions")
    suspend fun createTransaction(@Body body: CreateTransactionDto): TransactionDto

    @DELETE("api/transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: String)
}
