package com.quan.finance_app.ui.transaction

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

internal object TransactionTime {
    val zone: ZoneId = ZoneId.of("Asia/Ho_Chi_Minh")

    fun now(): LocalDateTime = LocalDateTime.now(zone).truncatedTo(ChronoUnit.MINUTES)

    fun toInstant(local: LocalDateTime): Instant = local.atZone(zone).toInstant()
}
