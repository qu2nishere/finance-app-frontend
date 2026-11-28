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
            compose.setContent { MoneyFlowTheme { MainApp() } }
            compose.waitUntil(20000) { compose.onAllNodes(hasScrollToNodeAction()).fetchSemanticsNodes().isNotEmpty() }
            compose.onAllNodes(hasScrollToNodeAction()).onFirst().performScrollToNode(hasText("Tất cả >"))
            waitFor("Same title")
            compose.onAllNodesWithText("Same title").onFirst().performClick()
            waitFor("note-2")
            compose.onNodeWithContentDescription("Quay lại").performClick()
            compose.onAllNodes(hasScrollToNodeAction()).onFirst().performScrollToNode(hasText("Tất cả >"))
            compose.onNodeWithText("Tất cả >").performClick()
            compose.onNode(hasSetTextAction()).performTextInput("note-1")
            compose.onNode(hasSetTextAction()).performImeAction()
            waitFor("1 giao dịch")
            compose.onNode(hasScrollToNodeAction()).performScrollToNode(hasText("Same title"))
            compose.onNodeWithText("Same title").performClick()
            waitFor("note-1")
            compose.onNodeWithContentDescription("Quay lại").performClick()
            waitFor("1 giao dịch")
            compose.onNode(hasSetTextAction()).assertTextContains("note-1")
        } finally {
            runBlocking { auth.logout() }
        }
    }
}
