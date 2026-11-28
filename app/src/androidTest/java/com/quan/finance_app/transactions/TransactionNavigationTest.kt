package com.quan.finance_app.transactions

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.quan.finance_app.MainApp
import com.quan.finance_app.data.AuthResult
import com.quan.finance_app.data.ServiceLocator
import com.quan.finance_app.data.remote.ApiClient
import com.quan.finance_app.data.remote.CreateTransactionDto
import com.quan.finance_app.ui.theme.MoneyFlowTheme
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class TransactionNavigationTest {
    @get:Rule val compose = createComposeRule()

    private fun waitFor(label: String) {
        compose.waitUntil(20000) { compose.onAllNodesWithText(label).fetchSemanticsNodes().isNotEmpty() }
    }

    @Test fun opensCorrectDuplicateFromHomeAndFilteredHistory() {
        val auth = ServiceLocator.authRepository
        runBlocking {
            check(!auth.hasSession()) { "Use a dedicated test emulator" }
            val key = UUID.randomUUID().toString()
            check(auth.register("nav-$key@example.invalid", key, "Ledger QA") is AuthResult.Success)
            auth.finishOnboarding()
        }
        try {
            runBlocking {
                val api = ApiClient.ledgerApi
                val wallet = api.getWallets().first()
                for (n in 1..2) {
                    api.createTransaction(CreateTransactionDto(wallet.id, null, "INCOME", n * 1000L,
                        "2026-11-28T0${n}:00:00Z", "note-$n", "Same title"))
                }
            }
            var loseReply = true
            var offline = true
            val detail = com.quan.finance_app.ui.transaction.TransactionDetailViewModel(remove = { id ->
                val outcome = ServiceLocator.ledgerRepository.deleteTransaction(id)
                if (outcome.isSuccess && loseReply) {
                    loseReply = false
                    Result.failure(java.io.IOException("Response lost after ledger commit"))
                } else outcome
            }, fetch = { id ->
                if (offline) {
                    offline = false
                    throw java.io.IOException("Offline before reading detail")
                }
                ServiceLocator.ledgerRepository.getTransaction(id)
            })
            val home = compose.runOnUiThread { com.quan.finance_app.ui.home.HomeViewModel() }
            compose.setContent { MoneyFlowTheme { MainApp(homeViewModel = home, detailViewModel = detail) } }
            compose.waitUntil(20000) { home.state.value.recentTransactions.size == 2 && !home.state.value.isLoading }
            compose.waitUntil(20000) { compose.onAllNodes(hasScrollToNodeAction()).fetchSemanticsNodes().isNotEmpty() }
            compose.onAllNodes(hasScrollToNodeAction()).onFirst().performScrollToNode(hasText("Tất cả >"))
            waitFor("Same title")
            compose.onAllNodesWithText("Same title").onFirst().performClick()
            waitFor("Không tải được chi tiết giao dịch.")
            compose.onNodeWithText("Thử lại").performClick()
            waitFor("note-2")
            val category = runBlocking { ApiClient.ledgerApi.getTransactions().content.first().categoryName }
            compose.onNodeWithText(category ?: "Chưa phân loại").assertExists()
            compose.onNodeWithContentDescription("Quay lại").performClick()
            compose.onAllNodes(hasScrollToNodeAction()).onFirst().performScrollToNode(hasText("Tất cả >"))
            compose.onNodeWithText("Tất cả >").performClick()
            compose.onNode(hasSetTextAction()).performTextInput("note-1")
            compose.onNode(hasSetTextAction()).performImeAction()
            waitFor("1 giao dịch")
            compose.onNode(hasScrollToNodeAction()).performScrollToNode(hasText("Same title"))
            compose.onNodeWithText("Same title").performClick()
            waitFor("note-1")
            compose.onNodeWithText("Xoá giao dịch").performScrollTo().performClick()
            compose.onNodeWithText("Huỷ").performClick()
            compose.onNodeWithText("note-1").assertExists()
            compose.onNodeWithText("Xoá giao dịch").performClick()
            compose.onNodeWithText("Xoá", substring = false).performClick()
            waitFor("Chưa xác nhận được kết quả xoá. Bạn có thể thử lại.")
            compose.onNodeWithText("Xoá giao dịch").performScrollTo().performClick()
            compose.onNodeWithText("Xoá", substring = false).performClick()
            waitFor("Không có giao dịch phù hợp.")

            compose.onNode(hasSetTextAction()).assertTextContains("note-1")
            compose.onNodeWithContentDescription("Quay lại").performClick()
            compose.onAllNodes(hasScrollToNodeAction()).onFirst().performScrollToNode(hasText("Tất cả >"))
            compose.onNodeWithText("Same title").performClick()
            waitFor("note-2")
            compose.onNodeWithText("Xoá giao dịch").performScrollTo().performClick()
            compose.onNodeWithText("Xoá", substring = false).performClick()
            compose.waitUntil(15000) { runBlocking { ApiClient.ledgerApi.getWallets().single().currentBalance == 0L } }
            check(runBlocking { ApiClient.ledgerApi.getTransactions().content.isEmpty() })
        } finally {
            runBlocking { auth.logout() }
        }
    }
}
