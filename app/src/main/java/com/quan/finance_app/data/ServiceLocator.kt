package com.quan.finance_app.data

import android.content.Context
import com.quan.finance_app.data.local.TokenCipher
import com.quan.finance_app.data.local.TokenStore
import com.quan.finance_app.data.remote.ApiClient

/**
 * Chỗ nối dây thủ công giữa các lớp, khởi tạo một lần trong [com.quan.finance_app.FinanceApp].
 */
object ServiceLocator {

    lateinit var tokenStore: TokenStore
        private set

    lateinit var authRepository: AuthRepository
        private set

    lateinit var ledgerRepository: LedgerRepository
        private set

    lateinit var budgetRepository: BudgetRepository
        private set

    lateinit var reportRepository: ReportRepository
        private set

    fun init(context: Context) {
        val tokenCipher = TokenCipher()
        tokenStore = TokenStore(context.applicationContext, tokenCipher)
        authRepository = AuthRepository(
            api = ApiClient.authApi,
            refreshApi = ApiClient.refreshAuthApi,
            tokenStore = tokenStore
        )
        ledgerRepository = LedgerRepository(ApiClient.ledgerApi)
        budgetRepository = BudgetRepository(ApiClient.budgetApi)
        reportRepository = ReportRepository(ApiClient.reportApi)
    }
}
