package com.quan.finance_app.transactions

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.quan.finance_app.data.LedgerRepository
import com.quan.finance_app.data.remote.*
import com.quan.finance_app.ui.screens.AllTransactionsScreen
import com.quan.finance_app.ui.theme.MoneyFlowTheme
import com.quan.finance_app.ui.transaction.AllTransactionsViewModel
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID

/** Runs against the development gateway from an emulator. Each run owns its test account. */
class HistoryGatewayTest {
    @get:Rule val compose = createComposeRule()

    @Test fun searchesBeyondFiftyRows() {
        val retrofit = Retrofit.Builder().baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val ledger = runBlocking {
            val id = UUID.randomUUID().toString()
            val session = requireNotNull(retrofit.create(AuthApi::class.java)
                .register(RegisterRequestDto("history-$id@example.invalid", id, "History QA")).body())
            val client = OkHttpClient.Builder().addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder()
                    .header("Authorization", "Bearer ${session.accessToken}").build())
            }.build()
            val api = retrofit.newBuilder().client(client).build().create(LedgerApi::class.java)
            val wallet = api.getWallets().first()
            repeat(55) { n ->
                val time = if (n == 0) "2026-11-27T17:15:00Z" else "2026-11-28T02:00:00Z"
                api.createTransaction(CreateTransactionDto(wallet.id, null, "EXPENSE", 1000, time,
                    null, if (n == 0) "oldest-unique" else "Later"))
            }
            LedgerRepository(api)
        }
        var failSearch = true
        var failNextPage = true
        val vm = AllTransactionsViewModel({ ledger }) { page, filter ->
            if (filter.q == "oldest" && failSearch) {
                failSearch = false
                throw java.io.IOException("Offline while searching")
            }
            if (page == 1 && failNextPage) {
                failNextPage = false
                throw java.io.IOException("Offline while loading more")
            }
            ledger.history(page, filter)
        }
        compose.setContent { MoneyFlowTheme { AllTransactionsScreen(vm) } }
        compose.waitUntil(20000) { compose.onAllNodesWithText("55 giao dịch").fetchSemanticsNodes().isNotEmpty() }
        compose.onNode(hasSetTextAction()).performTextInput("oldest")
        compose.onNode(hasSetTextAction()).performImeAction()
        compose.waitUntil(15000) { vm.state.value.error != null }
        compose.onNodeWithText("Thử lại", substring = true).performScrollTo().performClick()
        compose.waitUntil(20000) { compose.onAllNodesWithText("1 giao dịch").fetchSemanticsNodes().isNotEmpty() }
        compose.onNode(hasScrollToNodeAction()).performScrollToNode(hasText("oldest-unique"))
        compose.onNodeWithText("oldest-unique").assertIsDisplayed()
        compose.onNode(hasSetTextAction()).performTextClearance()
        compose.waitUntil(15000) { vm.state.value.transactions.size == 20 }
        val list = compose.onNode(hasScrollToNodeAction())
        list.performScrollToKey(vm.state.value.transactions.last().id)
        compose.waitUntil(15000) { vm.state.value.error != null }
        check(vm.state.value.transactions.size == 20)
        list.performScrollToNode(hasText("Thử lại", substring = true))
        compose.onNodeWithText("Thử lại", substring = true).performClick()
        compose.waitUntil(15000) { vm.state.value.transactions.size >= 40 }
        list.performScrollToKey(vm.state.value.transactions.last().id)
        compose.waitUntil(15000) { vm.state.value.transactions.size == 55 }
        check(vm.state.value.transactions.distinctBy { it.id }.size == 55)
    }
}
