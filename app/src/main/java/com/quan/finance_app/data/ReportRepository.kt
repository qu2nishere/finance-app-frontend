package com.quan.finance_app.data

import com.google.gson.Gson
import com.quan.finance_app.data.remote.CashflowSummaryDto
import com.quan.finance_app.data.remote.CategoryBreakdownItemDto
import com.quan.finance_app.data.remote.DailyTransactionGroupDto
import com.quan.finance_app.data.remote.MonthlyBarPointDto
import com.quan.finance_app.data.remote.MonthlyTransactionsDto
import com.quan.finance_app.data.remote.MonthlyTrendDto
import com.quan.finance_app.data.remote.ProblemDetailDto
import com.quan.finance_app.data.remote.ReportApi
import com.quan.finance_app.data.remote.ReportTransactionItemDto
import com.quan.finance_app.data.remote.WalletCashflowItemDto
import kotlinx.coroutines.CancellationException
import retrofit2.Response
import java.io.IOException
import java.time.Instant
import java.time.LocalDate

sealed interface ReportResult<out T> {
    data class Success<T>(val data: T) : ReportResult<T>
    data class Failure(val message: String, val code: String? = null) : ReportResult<Nothing>
}

data class MonthlyBarPoint(
    val period: String,
    val year: Int,
    val month: Int,
    val totalAmount: Long,
    val txnCount: Int,
    val isSelected: Boolean
)

data class CategoryBreakdownItem(
    val categoryId: String?,
    val categoryName: String,
    val categoryIcon: String?,
    val categoryColor: String?,
    val totalAmount: Long,
    val txnCount: Int,
    val sharePercent: Double?,
    val momChangePercent: Double?
)

data class MonthlyTrendData(
    val period: String,
    val type: String,
    val categoryId: String?,
    val currencyCode: String,
    val selectedAmount: Long,
    val previousAmount: Long,
    val momChangeAmount: Long,
    val momChangePercent: Double?,
    val points: List<MonthlyBarPoint>,
    val categoryBreakdown: List<CategoryBreakdownItem>
)

data class ReportTransactionItem(
    val transactionId: String,
    val type: String,
    val amount: Long,
    val signedAmount: Long,
    val currencyCode: String,
    val occurredAt: Instant,
    val occurredDate: LocalDate,
    val categoryId: String?,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String,
    val walletId: String,
    val walletName: String,
    val merchant: String?,
    val note: String?
)

data class DailyTransactionGroup(
    val date: LocalDate,
    val totalAmount: Long,
    val netAmount: Long,
    val txnCount: Int,
    val items: List<ReportTransactionItem>
)

data class MonthlyTransactionsData(
    val period: String,
    val selectedDate: LocalDate?,
    val currencyCode: String,
    val monthTotalAmount: Long,
    val monthTxnCount: Int,
    val filteredTotalAmount: Long,
    val filteredTxnCount: Int,
    val days: List<DailyTransactionGroup>
)

data class WalletCashflowItem(
    val walletId: String,
    val walletName: String,
    val walletKind: String,
    val color: String?,
    val accountMask: String?,
    val openingBalance: Long,
    val totalIncome: Long,
    val totalExpense: Long,
    val netFlow: Long,
    val closingBalance: Long,
    val includeInTotal: Boolean
)

data class CashflowSummaryData(
    val period: String,
    val currencyCode: String,
    val openingBalance: Long,
    val totalIncome: Long,
    val totalExpense: Long,
    val closingBalance: Long,
    val wallets: List<WalletCashflowItem>
)

/**
 * Tầng duy nhất gọi ReportApi, bắt IOException, dịch ProblemDetail và chuyển chuỗi ngày/giờ
 * sang java.time để UI/ViewModel không chạm vào package data.remote.
 */
open class ReportRepository(private val api: ReportApi? = null) {

    private val gson = Gson()

    open suspend fun getMonthlyTrend(
        period: String,
        months: Int = 5,
        type: String = "INCOME",
        categoryId: String? = null
    ): ReportResult<MonthlyTrendData> {
        val client = requireNotNull(api) { "ReportApi chưa được khởi tạo" }
        return execute(
            request = { client.getMonthlyTrend(period = period, months = months, type = type, categoryId = categoryId) },
            mapper = { it.toDomain() }
        )
    }

    open suspend fun getTransactions(
        period: String,
        date: LocalDate? = null,
        type: String? = null,
        categoryId: String? = null
    ): ReportResult<MonthlyTransactionsData> {
        val client = requireNotNull(api) { "ReportApi chưa được khởi tạo" }
        return execute(
            request = {
                client.getTransactions(
                    period = period,
                    date = date?.toString(),
                    type = type,
                    categoryId = categoryId
                )
            },
            mapper = { it.toDomain() }
        )
    }

    open suspend fun getCashflowSummary(period: String): ReportResult<CashflowSummaryData> {
        val client = requireNotNull(api) { "ReportApi chưa được khởi tạo" }
        return execute(
            request = { client.getCashflowSummary(period = period) },
            mapper = { it.toDomain() }
        )
    }

    private suspend fun <Dto, Domain> execute(
        request: suspend () -> Response<Dto>,
        mapper: (Dto) -> Domain
    ): ReportResult<Domain> {
        return try {
            val response = request()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                ReportResult.Success(mapper(body))
            } else {
                parseFailure(response)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: IOException) {
            ReportResult.Failure(
                message = "Không kết nối được máy chủ. Bạn kiểm tra lại mạng hoặc trạng thái backend nhé.",
                code = "NETWORK_ERROR"
            )
        } catch (e: Exception) {
            ReportResult.Failure(
                message = e.message ?: "Đã có lỗi xảy ra khi tải dữ liệu thống kê",
                code = "UNKNOWN_ERROR"
            )
        }
    }

    private fun <T> parseFailure(response: Response<T>): ReportResult.Failure {
        val problem = runCatching {
            gson.fromJson(response.errorBody()?.string(), ProblemDetailDto::class.java)
        }.getOrNull()

        val detail = problem?.detail
        if (!detail.isNullOrBlank()) {
            return ReportResult.Failure(message = detail, code = problem.code)
        }
        return ReportResult.Failure(
            message = "Lỗi máy chủ (HTTP ${response.code()})",
            code = problem?.code
        )
    }
}

private fun MonthlyTrendDto.toDomain(): MonthlyTrendData = MonthlyTrendData(
    period = period,
    type = type,
    categoryId = categoryId,
    currencyCode = currencyCode ?: "VND",
    selectedAmount = selectedAmount,
    previousAmount = previousAmount,
    momChangeAmount = momChangeAmount,
    momChangePercent = momChangePercent,
    points = (points ?: emptyList()).map(MonthlyBarPointDto::toDomain),
    categoryBreakdown = (categoryBreakdown ?: emptyList()).map(CategoryBreakdownItemDto::toDomain)
)

private fun MonthlyBarPointDto.toDomain(): MonthlyBarPoint = MonthlyBarPoint(
    period = period,
    year = year,
    month = month,
    totalAmount = totalAmount,
    txnCount = txnCount,
    isSelected = isSelected
)

private fun CategoryBreakdownItemDto.toDomain(): CategoryBreakdownItem = CategoryBreakdownItem(
    categoryId = categoryId,
    categoryName = categoryName,
    categoryIcon = categoryIcon,
    categoryColor = categoryColor,
    totalAmount = totalAmount,
    txnCount = txnCount,
    sharePercent = sharePercent,
    momChangePercent = momChangePercent
)

private fun MonthlyTransactionsDto.toDomain(): MonthlyTransactionsData = MonthlyTransactionsData(
    period = period,
    selectedDate = selectedDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
    currencyCode = currencyCode ?: "VND",
    monthTotalAmount = monthTotalAmount,
    monthTxnCount = monthTxnCount,
    filteredTotalAmount = filteredTotalAmount,
    filteredTxnCount = filteredTxnCount,
    days = (days ?: emptyList()).map(DailyTransactionGroupDto::toDomain)
)

private fun DailyTransactionGroupDto.toDomain(): DailyTransactionGroup {
    val parsedDate = LocalDate.parse(date)
    return DailyTransactionGroup(
        date = parsedDate,
        totalAmount = totalAmount,
        netAmount = netAmount,
        txnCount = txnCount,
        items = (items ?: emptyList()).map { it.toDomain(parsedDate) }
    )
}

private fun ReportTransactionItemDto.toDomain(fallbackDate: LocalDate): ReportTransactionItem {
    val parsedDate = runCatching { LocalDate.parse(occurredDate) }.getOrDefault(fallbackDate)
    val parsedInstant = runCatching { Instant.parse(occurredAt) }.getOrDefault(Instant.EPOCH)
    return ReportTransactionItem(
        transactionId = transactionId,
        type = type,
        amount = amount,
        signedAmount = signedAmount,
        currencyCode = currencyCode ?: "VND",
        occurredAt = parsedInstant,
        occurredDate = parsedDate,
        categoryId = categoryId,
        categoryName = categoryName ?: "Chưa phân loại",
        categoryIcon = categoryIcon ?: "Category",
        categoryColor = categoryColor ?: "#94A3B8",
        walletId = walletId,
        walletName = walletName,
        merchant = merchant,
        note = note
    )
}

private fun CashflowSummaryDto.toDomain(): CashflowSummaryData = CashflowSummaryData(
    period = period,
    currencyCode = currencyCode ?: "VND",
    openingBalance = openingBalance,
    totalIncome = totalIncome,
    totalExpense = totalExpense,
    closingBalance = closingBalance,
    wallets = (wallets ?: emptyList()).map(WalletCashflowItemDto::toDomain)
)

private fun WalletCashflowItemDto.toDomain(): WalletCashflowItem = WalletCashflowItem(
    walletId = walletId,
    walletName = walletName,
    walletKind = walletKind,
    color = color,
    accountMask = accountMask,
    openingBalance = openingBalance,
    totalIncome = totalIncome,
    totalExpense = totalExpense,
    netFlow = netFlow,
    closingBalance = closingBalance,
    includeInTotal = includeInTotal
)
