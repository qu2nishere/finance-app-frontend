package com.quan.finance_app.settings

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.quan.finance_app.MainActivity
import com.quan.finance_app.data.AccountTimeZone
import com.quan.finance_app.data.AuthResult
import com.quan.finance_app.data.ServiceLocator
import com.quan.finance_app.data.remote.ApiClient
import com.quan.finance_app.data.remote.CreateTransactionDto
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.UUID

class AccountTimezoneGatewayTest {
    @get:Rule val compose = createEmptyComposeRule()
    private val vn = "Asia/Ho_Chi_Minh"
    private val la = "America/Los_Angeles"

    private fun waitText(label: String) = compose.waitUntil(20000) {
        compose.onAllNodesWithText(label).fetchSemanticsNodes().isNotEmpty()
    }

    private suspend fun register(): String {
        val key = UUID.randomUUID().toString()
        val auth = ServiceLocator.authRepository
        val session = auth.register("tz-$key@example.invalid", key, "Timezone QA") as AuthResult.Success
        auth.finishOnboarding()
        return session.data.userId
    }

    private suspend fun transaction(date: String): String {
        val api = ApiClient.ledgerApi
        val wallet = api.getWallets().first()
        val txn = api.createTransaction(CreateTransactionDto(wallet.id, null, "EXPENSE", 1700,
            "2026-11-27T17:15:00Z", null, "Timezone QA"))
        check(txn.occurredDate == date)
        return txn.id
    }

    private fun openSettings() {
        compose.waitUntil(20000) {
            compose.onAllNodesWithContentDescription("Cài đặt").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithContentDescription("Cài đặt").performClick()
        compose.onNode(hasScrollToNodeAction()).performScrollToNode(hasText("Múi giờ"))
        compose.onNodeWithText("Múi giờ").performClick()
    }

    @Test fun savesRestoresAndUsesTheProfileAcrossAccounts() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        check(!runBlocking { ServiceLocator.authRepository.hasSession() }) { "Use a dedicated QA application" }
        try {
            val userId = runBlocking {
                val first = register()
                ServiceLocator.profileRepository.update(first, la)
                transaction("2026-11-27")
                ServiceLocator.authRepository.logout()
                check(AccountTimeZone.current.id == vn)
                val second = register()
                ServiceLocator.profileRepository.update(second, vn)
                second
            }
            val existing = runBlocking { transaction("2026-11-28") }
            ActivityScenario.launch(MainActivity::class.java).use {
                openSettings()
                waitText("Đang dùng · $vn")
                compose.onNode(hasSetTextAction()).performTextInput("Los Angeles")
                compose.onNodeWithText(la).performClick()
                compose.onNodeWithText("Lưu múi giờ").performClick()
                compose.waitUntil(20000) { AccountTimeZone.current.id == la &&
                    compose.onAllNodesWithText("Lưu múi giờ").fetchSemanticsNodes().isEmpty() }
                runBlocking {
                    check(ServiceLocator.profileRepository.load(userId) == la)
                    check(ApiClient.ledgerApi.getTransaction(existing).occurredDate == "2026-11-28")
                    transaction("2026-11-27")
                }
                compose.onNodeWithContentDescription("Ngân sách").performClick()
                waitText("Ngân sách")
                val today = LocalDate.now(ZoneId.of(la))
                val budget = runBlocking { ApiClient.budgetApi.getCurrentBudget().body()!! }
                check(budget.periodKey == YearMonth.from(today).toString())
                check(budget.daysLeft == today.lengthOfMonth() - today.dayOfMonth + 1)
                waitText("còn ${budget.daysLeft} ngày")
                compose.onNodeWithContentDescription("Thống kê").performClick()
                waitText("Tháng ${today.monthValue}, ${today.year}")
            }

            // Recreate all session dependencies, as happens after Android discards the process.
            AccountTimeZone.activate(null)
            ServiceLocator.init(context)
            check(ServiceLocator.tokenStore.accessToken == null && AccountTimeZone.current.id == la)
            ActivityScenario.launch(MainActivity::class.java).use {
                openSettings()
                waitText("Đang dùng · $la")
                check(ServiceLocator.tokenStore.userId == userId)
                runBlocking { transaction("2026-11-27") }
            }
        } finally {
            runBlocking { ServiceLocator.authRepository.logout() }
        }
    }

}
