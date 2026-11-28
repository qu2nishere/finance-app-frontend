package com.quan.finance_app.transactions

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import com.quan.finance_app.MainActivity
import com.quan.finance_app.data.AuthResult
import com.quan.finance_app.data.ServiceLocator
import com.quan.finance_app.data.remote.ApiClient
import com.quan.finance_app.data.remote.CreateTransactionDto
import com.quan.finance_app.data.remote.CreateWalletDto
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

class TransactionTimeGatewayTest {
    @get:Rule val compose = createEmptyComposeRule()
    private val api get() = ApiClient.ledgerApi
    private val zone = ZoneId.of("Asia/Ho_Chi_Minh")
    private val shortDate = DateTimeFormatter.ofPattern("dd/MM")
    private val longDate = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private fun waitText(value: String) = compose.waitUntil(20000) {
        compose.onAllNodesWithText(value).fetchSemanticsNodes().isNotEmpty()
    }

    private fun tap(value: String) = compose.onNodeWithText(value).performScrollTo().performClick()

    private fun openPicker() = compose.onNodeWithContentDescription("Chọn ngày giờ giao dịch")
        .performScrollTo().performClick()

    private fun enterTime(time: String) {
        compose.onNode(hasSetTextAction() and hasText("Giờ")).performTextReplacement(time.substringBefore(':'))
        compose.onNode(hasSetTextAction() and hasText("Phút")).performTextReplacement(time.substringAfter(':'))
    }

    @Test fun savesBothFlowsAtMidnightBoundariesAndRestoresDraft() {
        val auth = ServiceLocator.authRepository
        val key = UUID.randomUUID().toString()
        val today = LocalDate.now(zone)
        val date = today.minusDays(1)
        val prefix = "QA-time-${key.take(8)}"
        check(!runBlocking { auth.hasSession() }) { "Use a dedicated test application" }
        runBlocking {
            val session = auth.register("time-$key@example.invalid", key, "Time QA") as AuthResult.Success
            ServiceLocator.profileRepository.update(session.data.userId, zone.id)
            auth.finishOnboarding()
        }
        try {
            val legacy = runBlocking {
                api.createTransaction(CreateTransactionDto(api.getWallets().first().id, null, "INCOME", 1,
                    date.atStartOfDay(zone).toInstant().toString(), null, "midnight"))
            }
            val wallets = runBlocking {
                api.createWallet(CreateWalletDto("Ví dự phòng"))
                api.getWallets()
            }
            val incomeCategory = runBlocking { api.getCategories().filter { it.flow == "INCOME" }[1] }
            ActivityScenario.launch(MainActivity::class.java).use { activity ->
                val cases = listOf("EXPENSE" to "23:59", "INCOME" to "00:15",
                    "EXPENSE" to "00:15", "INCOME" to "23:59")
                for ((index, entry) in cases.withIndex()) {
                    val (kind, time) = entry
                    val title = "$prefix-$index"
                    compose.waitUntil(20000) {
                        compose.onAllNodesWithContentDescription("Thêm giao dịch").fetchSemanticsNodes().isNotEmpty()
                    }
                    compose.onNodeWithContentDescription("Thêm giao dịch").performClick()
                    if (kind == "INCOME") tap("Thu nhập")
                    if (index == 1) {
                        tap("Vào ví · ")
                        compose.onNodeWithContentDescription(wallets[1].name).performClick()
                        tap("Nguồn thu · ")
                        compose.onAllNodesWithText(incomeCategory.name).onLast().performClick()
                    }
                    tap("Chạm để nhập số tiền")
                    compose.onNodeWithText("7").performClick()
                    compose.onNodeWithText("=").performClick()
                    compose.onNodeWithTag("transactionName").performTextInput(title)
                    openPicker()
                    if (index == 0) {
                        compose.onNodeWithContentDescription("Tháng sau").performClick()
                        compose.onNodeWithTag("calendar-day-${YearMonth.from(today).plusMonths(1).atDay(15)}").performClick()
                        compose.onNodeWithText("Chọn 15/", substring = true).performScrollTo().performClick()
                        waitText("Thời điểm giao dịch phải ở hiện tại hoặc quá khứ.")
                        tap("Hôm nay")
                    }
                    if (YearMonth.from(today) != YearMonth.from(date))
                        compose.onNodeWithContentDescription("Tháng trước").performClick()
                    compose.onNodeWithTag("calendar-day-$date").performClick()
                    enterTime(time)
                    tap("Chọn ${date.format(shortDate)} · $time")
                    if (index == 1) {
                        activity.recreate()
                        waitText("${date.format(longDate)} $time")
                        compose.onNodeWithTag("transactionName").assertTextContains(title)
                        openPicker()
                        compose.onNodeWithText("Chọn ${date.format(shortDate)} · $time").assertExists()
                        enterTime("09:12")
                        tap("Huỷ")
                        waitText("${date.format(longDate)} $time")
                    }
                    tap(if (kind == "INCOME") "Lưu khoản thu" else "Lưu khoản chi")
                    compose.waitUntil(20000) { compose.onAllNodesWithTag("transactionName").fetchSemanticsNodes().isEmpty() }
                    val row = runBlocking {
                        val id = api.getTransactions().content.single { it.merchant == title }.id
                        api.getTransaction(id)
                    }
                    check(row.type == kind && row.amount == 7L)
                    check(row.walletId == wallets[if (index == 0) 0 else 1].id)
                    if (index == 1) check(row.categoryId == incomeCategory.id)
                    check(Instant.parse(row.occurredAt) == date.atTime(java.time.LocalTime.parse(time)).atZone(zone).toInstant())
                    check(row.occurredDate == date.toString())
                    if (index == 1) {
                        val list = compose.onNode(hasScrollToNodeAction())
                        list.performScrollToNode(hasText("Tất cả >"))
                        compose.onNodeWithText("Tất cả >").performClick()
                        compose.onNode(hasSetTextAction()).performTextInput(prefix)
                        compose.onNode(hasSetTextAction()).performImeAction()
                    }
                    if (index >= 1) {
                        waitText("${index + 1} giao dịch")
                        compose.onNode(hasScrollToNodeAction()).performScrollToNode(hasText(title))
                        compose.onNodeWithText(title).performClick()
                        waitText("${date.format(longDate)} $time")
                        compose.onNodeWithText("Chưa có ghi chú").assertExists()
                        compose.onNodeWithContentDescription("Quay lại").performClick()
                        compose.onNode(hasSetTextAction()).assertTextContains(prefix)
                    }
                }
                compose.onNodeWithContentDescription("Thống kê").performClick()
                if (YearMonth.from(today) != YearMonth.from(date))
                    compose.onNodeWithContentDescription("Tháng trước").performClick()
                val report = compose.onNode(hasScrollToNodeAction())
                report.performScrollToNode(hasText("Chọn ngày"))
                tap("Chọn ngày")
                compose.onNodeWithTag("calendar-day-$date").performClick()
                compose.onAllNodes(hasSetTextAction() and hasText("Giờ")).assertCountEquals(0)
                tap("Chọn ${date.format(shortDate)}")
                waitText("Ngày ${date.format(shortDate)}")
                waitText("2 giao dịch đã thực hiện")
                compose.onNodeWithText("$prefix-0").assertExists()
                compose.onNodeWithText("$prefix-2").assertExists()
                val unchanged = runBlocking { api.getTransaction(legacy.id) }
                check(unchanged.occurredAt == legacy.occurredAt && unchanged.occurredDate == legacy.occurredDate)
            }
        } finally {
            runBlocking { auth.logout() }
        }
    }
}
