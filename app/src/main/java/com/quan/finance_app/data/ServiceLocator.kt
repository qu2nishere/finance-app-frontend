package com.quan.finance_app.data

import android.content.Context
import com.quan.finance_app.data.local.TokenStore
import com.quan.finance_app.data.remote.ApiClient

/**
 * Chỗ nối dây thủ công giữa các lớp, khởi tạo một lần trong [com.quan.finance_app.FinanceApp].
 *
 * Đây là "dependency injection bằng tay". Khi số phụ thuộc nhiều lên thì thay bằng
 * Hilt — nhưng lúc đó bạn đã hiểu nó thay thế cái gì.
 */
object ServiceLocator {

    lateinit var tokenStore: TokenStore
        private set

    lateinit var authRepository: AuthRepository
        private set

    fun init(context: Context) {
        tokenStore = TokenStore(context.applicationContext)
        authRepository = AuthRepository(ApiClient.authApi, tokenStore)
    }
}
