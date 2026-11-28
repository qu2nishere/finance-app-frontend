package com.quan.finance_app.data.local

import android.content.Context
import android.content.ContextWrapper
import androidx.test.platform.app.InstrumentationRegistry
import com.quan.finance_app.data.AccountTimeZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.UUID

class TokenStoreTimezoneTest {
    @Test fun missingTimezonePreservesOnlyTheSameRememberedAccount() {
        val app = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "qa-zone-" + UUID.randomUUID()
        val isolated = object : ContextWrapper(app) {
            override fun getSharedPreferences(ignored: String?, mode: Int) = app.getSharedPreferences(name, mode)
        }
        val initial = AccountTimeZone.current.id
        try {
            val store = TokenStore(isolated)
            store.save("first", "remembered", userId = "A", timezone = "America/Los_Angeles")
            store.save("next", "rotated", userId = "A")
            assertEquals("America/Los_Angeles", AccountTimeZone.current.id)
            AccountTimeZone.activate(null)
            val restored = TokenStore(isolated)
            assertNull(restored.accessToken)
            assertEquals("America/Los_Angeles", AccountTimeZone.current.id)
            restored.save("another", "rotated", userId = "B")
            assertEquals("Asia/Ho_Chi_Minh", AccountTimeZone.current.id)
            restored.save("temporary", "ram", persist = false, userId = "B", timezone = "Europe/Paris")
            assertEquals("Europe/Paris", AccountTimeZone.current.id)
            assertNull(TokenStore(isolated).refreshToken)
            assertEquals("Asia/Ho_Chi_Minh", AccountTimeZone.current.id)
        } finally {
            app.deleteSharedPreferences(name)
            AccountTimeZone.activate(initial)
        }
    }
}
