package com.quan.finance_app.ui.transaction

import com.quan.finance_app.data.AccountTimeZone
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

internal object TransactionTime {
    val zone: ZoneId get() = AccountTimeZone.current

    fun now(): LocalDateTime = LocalDateTime.now(zone).truncatedTo(ChronoUnit.MINUTES)

    fun toInstant(local: LocalDateTime, accountZone: ZoneId = zone): Instant = local.atZone(accountZone).toInstant()
}
