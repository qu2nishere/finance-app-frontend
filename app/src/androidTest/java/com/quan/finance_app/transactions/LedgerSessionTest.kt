package com.quan.finance_app.transactions

import androidx.compose.ui.test.junit4.createComposeRule
import com.quan.finance_app.MainApp
import com.quan.finance_app.data.AuthResult
import com.quan.finance_app.data.ServiceLocator
import com.quan.finance_app.data.remote.*
import com.quan.finance_app.ui.auth.*
import com.quan.finance_app.ui.theme.MoneyFlowTheme
import com.quan.finance_app.ui.transaction.*
import kotlinx.coroutines.*
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class LedgerSessionTest {
    @get:Rule val compose = createComposeRule()

    @Test fun logoutDiscardsPendingHistoryAndFormOptionsBeforeNextLogin() {
        val repo = ServiceLocator.authRepository
        val api = ApiClient.ledgerApi
        val key = UUID.randomUUID().toString()
        val bEmail = "second-$key@example.invalid"
        fun register(email: String, label: String) = runBlocking {
            check(repo.register(email, key, label) is AuthResult.Success)
            repo.finishOnboarding()
            val wallet = api.createWallet(CreateWalletDto(label))
            api.createTransaction(CreateTransactionDto(wallet.id, null, "EXPENSE", 1000,
                "2026-11-28T02:00:00Z", null, label))
        }
        check(!runBlocking { repo.hasSession() })
        register(bEmail, "Wallet B")
        runBlocking { repo.logout() }
        register("first-$key@example.invalid", "Wallet A")
        val pending = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        try {
            val history = AllTransactionsViewModel { page, filter ->
                val result = ServiceLocator.ledgerRepository.history(page, filter)
                if (filter.q == "Wallet A") {
                    pending.complete(Unit)
                    withContext(NonCancellable) { release.await() }
                }
                result
            }
            val form = TransactionViewModel()
            val auth = compose.runOnUiThread { AuthViewModel() }
            compose.setContent { MoneyFlowTheme { MainApp(authViewModel = auth, historyViewModel = history, transactionViewModel = form) } }
            compose.waitUntil(20000) { form.state.value.wallets.any { it.name == "Wallet A" } }
            compose.runOnUiThread { history.open(); history.setFilter(HistoryFilter(q = "Wallet A")) }
            compose.waitUntil(10000) { pending.isCompleted }
            compose.runOnUiThread { auth.logout() }
            compose.waitUntil(15000) { form.state.value.wallets.isEmpty() }
            compose.waitUntil(15000) { !ServiceLocator.tokenStore.isLoggedIn }
            compose.runOnUiThread { auth.login(bEmail, key) }
            compose.waitUntil(20000) { form.state.value.wallets.any { it.name == "Wallet B" } }
            compose.runOnUiThread { history.open(); release.complete(Unit) }
            compose.waitUntil(15000) { history.state.value.transactions.singleOrNull()?.merchant == "Wallet B" }
            check(form.state.value.wallets.none { it.name == "Wallet A" })
            check(history.state.value.filter == HistoryFilter())
        } finally {
            release.complete(Unit)
            runBlocking { repo.logout() }
        }
    }
}
