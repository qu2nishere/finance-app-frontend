package com.quan.finance_app.settings

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.quan.finance_app.data.AccountTimeZone
import com.quan.finance_app.ui.screens.TimezoneSettingsScreen
import com.quan.finance_app.ui.theme.MoneyFlowTheme
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class TimezoneSettingsErrorTest {
    @get:Rule val compose = createComposeRule()

    @Test fun retriesOfflineProfileAndKeepsCurrentZoneAfterRejectedSave() {
        val original = AccountTimeZone.current.id
        var offline = true
        val other = if (original == "Pacific/Auckland") "Europe/Paris" else "Pacific/Auckland"
        compose.setContent {
            MoneyFlowTheme {
                TimezoneSettingsScreen("qa", onBack = { error("Unexpected navigation") },
                    loadProfile = {
                        if (offline) throw IOException()
                        original
                    },
                    saveProfile = { _, _ -> throw HttpException(Response.error<Unit>(400, "".toResponseBody())) })
            }
        }
        compose.onNodeWithText("Thử lại").assertExists()
        compose.runOnIdle { offline = false }
        compose.onNodeWithText("Thử lại").performClick()
        compose.onNode(hasSetTextAction()).performTextInput(other.substringAfter('/'))
        compose.onNodeWithText(other).performClick()
        compose.onNodeWithText("Lưu múi giờ").performClick()
        compose.onNodeWithText("Máy chủ từ chối múi giờ này. Hãy chọn múi giờ khác.").assertExists()
        compose.onNodeWithText("Đang dùng · $original").assertExists()
        check(AccountTimeZone.current.id == original)
    }
}
