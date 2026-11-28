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
    val missing: Boolean = false
)

class TransactionDetailViewModel(
    private val fetch: suspend (String) -> TransactionDto = ServiceLocator.ledgerRepository::getTransaction
) : ViewModel() {
    private val mutable = MutableStateFlow(DetailState())
    val state = mutable.asStateFlow()
    private var work: Job? = null

    fun reset() {
        work?.cancel()
        mutable.value = DetailState()
    }

    fun load(id: String) {
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
