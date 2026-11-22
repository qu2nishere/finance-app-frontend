package com.quan.finance_app.data

import com.google.gson.Gson
import com.quan.finance_app.data.remote.BudgetApi
import com.quan.finance_app.data.remote.CategoryLimitDto
import com.quan.finance_app.data.remote.CreateLimitRequestDto
import com.quan.finance_app.data.remote.CurrentBudgetDto
import com.quan.finance_app.data.remote.ProblemDetailDto
import com.quan.finance_app.data.remote.UpdateLimitRequestDto
import kotlinx.coroutines.CancellationException
import retrofit2.Response
import java.io.IOException
import java.time.LocalDate

sealed interface BudgetResult<out T> {
    data class Success<T>(val data: T) : BudgetResult<T>
    data class Failure(val message: String, val code: String? = null) : BudgetResult<Nothing>
}

enum class LimitStatus {
    OK,
    WARNING,
    EXCEEDED
}

data class CategoryLimitItem(
    val id: String,
    val categoryId: String,
    val categoryName: String,
    val categoryIcon: String?,
    val categoryColor: String?,
    val limitAmount: Long,
    val spentAmount: Long,
    val alertThreshold: Int,
    val status: LimitStatus
)

data class CurrentBudgetData(
    val periodKey: String,
    val periodStart: LocalDate?,
    val periodEnd: LocalDate?,
    val daysLeft: Int,
    val totalLimit: Long,
    val totalSpent: Long,
    val limits: List<CategoryLimitItem>
)

/**
 * Tầng duy nhất gọi BudgetApi, bắt IOException và dịch ProblemDetail để tầng UI không đụng data.remote.
 */
open class BudgetRepository(private val api: BudgetApi? = null) {

    private val gson = Gson()

    open suspend fun getCurrentBudget(period: String? = null): BudgetResult<CurrentBudgetData> {
        val client = requireNotNull(api) { "BudgetApi chưa được khởi tạo" }
        return execute(
            request = { client.getCurrentBudget(period) },
            mapper = { it.toDomain() }
        )
    }

    open suspend fun createLimit(
        categoryId: String,
        categoryName: String,
        categoryIcon: String?,
        categoryColor: String?,
        limitAmount: Long,
        alertThreshold: Int = 80,
        periodKey: String? = null
    ): BudgetResult<CategoryLimitItem> {
        val client = requireNotNull(api) { "BudgetApi chưa được khởi tạo" }
        val body = CreateLimitRequestDto(
            categoryId = categoryId,
            categoryName = categoryName,
            categoryIcon = categoryIcon,
            categoryColor = categoryColor,
            limitAmount = limitAmount,
            alertThreshold = alertThreshold,
            periodKey = periodKey
        )
        return execute(
            request = { client.createLimit(body) },
            mapper = { it.toDomain() }
        )
    }

    open suspend fun updateLimit(
        id: String,
        limitAmount: Long?,
        alertThreshold: Int?
    ): BudgetResult<CategoryLimitItem> {
        val client = requireNotNull(api) { "BudgetApi chưa được khởi tạo" }
        val body = UpdateLimitRequestDto(
            limitAmount = limitAmount,
            alertThreshold = alertThreshold
        )
        return execute(
            request = { client.updateLimit(id, body) },
            mapper = { it.toDomain() }
        )
    }

    open suspend fun deleteLimit(id: String): BudgetResult<Unit> {
        val client = requireNotNull(api) { "BudgetApi chưa được khởi tạo" }
        return executeUnit { client.deleteLimit(id) }
    }

    protected suspend fun <Dto, Domain> execute(
        request: suspend () -> Response<Dto>,
        mapper: (Dto) -> Domain
    ): BudgetResult<Domain> {
        return try {
            val response = request()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                BudgetResult.Success(mapper(body))
            } else {
                parseFailure(response)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: IOException) {
            BudgetResult.Failure(
                message = "Không kết nối được máy chủ. Bạn kiểm tra lại mạng nhé.",
                code = "NETWORK_ERROR"
            )
        } catch (e: Exception) {
            BudgetResult.Failure(
                message = e.message ?: "Đã có lỗi xảy ra, vui lòng thử lại",
                code = "UNKNOWN_ERROR"
            )
        }
    }

    protected suspend fun executeUnit(
        request: suspend () -> Response<Unit>
    ): BudgetResult<Unit> {
        return try {
            val response = request()
            if (response.isSuccessful) {
                BudgetResult.Success(Unit)
            } else {
                parseFailure(response)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: IOException) {
            BudgetResult.Failure(
                message = "Không kết nối được máy chủ. Bạn kiểm tra lại mạng nhé.",
                code = "NETWORK_ERROR"
            )
        } catch (e: Exception) {
            BudgetResult.Failure(
                message = e.message ?: "Đã có lỗi xảy ra, vui lòng thử lại",
                code = "UNKNOWN_ERROR"
            )
        }
    }

    private fun <T> parseFailure(response: Response<T>): BudgetResult.Failure {
        val problem = runCatching {
            gson.fromJson(response.errorBody()?.string(), ProblemDetailDto::class.java)
        }.getOrNull()

        val firstFieldError = problem?.errors?.values?.firstOrNull()
        val message = when {
            !firstFieldError.isNullOrBlank() -> firstFieldError
            !problem?.detail.isNullOrBlank() -> problem!!.detail!!
            else -> "Lỗi máy chủ (HTTP ${response.code()})"
        }
        return BudgetResult.Failure(message = message, code = problem?.code)
    }
}

internal fun CurrentBudgetDto.toDomain(): CurrentBudgetData = CurrentBudgetData(
    periodKey = periodKey ?: "",
    periodStart = periodStart?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
    periodEnd = periodEnd?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
    daysLeft = daysLeft ?: 0,
    totalLimit = totalLimit ?: 0L,
    totalSpent = totalSpent ?: 0L,
    limits = (limits ?: emptyList()).map(CategoryLimitDto::toDomain)
)

internal fun CategoryLimitDto.toDomain(): CategoryLimitItem = CategoryLimitItem(
    id = id ?: "",
    categoryId = categoryId ?: "",
    categoryName = categoryName ?: "Danh mục",
    categoryIcon = categoryIcon,
    categoryColor = categoryColor?.trim(),
    limitAmount = limitAmount ?: 0L,
    spentAmount = spentAmount ?: 0L,
    alertThreshold = alertThreshold ?: 80,
    status = when (status?.uppercase()) {
        "EXCEEDED" -> LimitStatus.EXCEEDED
        "WARNING" -> LimitStatus.WARNING
        else -> LimitStatus.OK
    }
)
