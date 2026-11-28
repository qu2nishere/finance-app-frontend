package com.quan.finance_app.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quan.finance_app.data.ServiceLocator
import com.quan.finance_app.data.remote.TransactionDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class DetailState(
    val id: String? = null,
    val transaction: TransactionDto? = null,
    val message: String? = null,
    val deleting: Boolean = false,
    val removal: Removal? = null,
    val missing: Boolean = false
)

enum class Removal { DELETED, ALREADY_GONE }

class TransactionDetailViewModel(
    private val remove: suspend (String) -> Result<Unit> = { ServiceLocator.ledgerRepository.deleteTransaction(it) },
    private val fetch: suspend (String) -> TransactionDto = ServiceLocator.ledgerRepository::getTransaction
) : ViewModel() {
    private val mutable = MutableStateFlow(DetailState())
    val state = mutable.asStateFlow()
    private var work: Job? = null

    fun reset() {
        work?.cancel()
        mutable.value = DetailState()
    }

    fun delete(id: String) {
        val current = mutable.value
        val txn = current.transaction ?: return
        if (txn.id != id || current.deleting || current.removal != null ||
            txn.type !in setOf("INCOME", "EXPENSE")) return
        mutable.value = current.copy(deleting = true, message = null)
        work = viewModelScope.launch {
            try {
                remove(id).getOrThrow()
                ensureActive()
                mutable.value = DetailState(id = id, removal = Removal.DELETED)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                ensureActive()
                mutable.value = when ((error as? HttpException)?.code()) {
                    404 -> DetailState(id = id, removal = Removal.ALREADY_GONE)
                    401 -> current.copy(message = "Phiên đăng nhập đã hết hạn.")
                    else -> current.copy(message = "Chưa xác nhận được kết quả xoá. Bạn có thể thử lại.")
                }
            }
        }
    }

    fun load(id: String) {
        if (mutable.value.id == id && (mutable.value.deleting || mutable.value.removal != null)) return
        work?.cancel()
        mutable.value = DetailState(id = id)
        work = viewModelScope.launch {
            try {
                val row = fetch(id)
                ensureActive()
                mutable.value = DetailState(id = id, transaction = row)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                ensureActive()
                val missing = (error as? HttpException)?.code() == 404
                mutable.value = DetailState(id = id, missing = missing, message = when {
                    missing -> "Giao dịch không còn trong sổ."
                    (error as? HttpException)?.code() == 401 -> "Phiên đăng nhập đã hết hạn."
                    else -> "Không tải được chi tiết giao dịch."
                })
            }
        }
    }
}
