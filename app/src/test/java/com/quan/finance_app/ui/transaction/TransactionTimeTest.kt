package com.quan.finance_app.ui.transaction

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime

class TransactionTimeTest {
    @Test fun sameInstantHasDifferentLocalDatesAcrossAccounts() {
        val instant = Instant.parse("2026-11-27T17:15:00Z")
        assertEquals(instant, TransactionTime.toInstant(LocalDateTime.parse("2026-11-28T00:15"),
            java.time.ZoneId.of("Asia/Ho_Chi_Minh")))
        assertEquals(instant, TransactionTime.toInstant(LocalDateTime.parse("2026-11-27T09:15"),
            java.time.ZoneId.of("America/Los_Angeles")))
    }

    @Test fun convertsBothEndsOfVietnameseDayToUtc() {
        assertEquals(Instant.parse("2026-11-27T17:15:00Z"),
            TransactionTime.toInstant(LocalDateTime.parse("2026-11-28T00:15")))
        assertEquals(Instant.parse("2026-11-28T16:59:00Z"),
            TransactionTime.toInstant(LocalDateTime.parse("2026-11-28T23:59")))
    }
}
