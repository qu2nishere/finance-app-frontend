package com.quan.finance_app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.DateTimeException
import java.time.ZoneId

internal object AccountTimeZone {
    private val fallback = ZoneId.of("Asia/Ho_Chi_Minh")
    private val active = MutableStateFlow(fallback)
    val state = active.asStateFlow()
    val current: ZoneId get() = active.value

    val regionIds = ZoneId.getAvailableZoneIds().filter { '/' in it }.sorted()

    fun deviceRegion(): String? {
        val zone = ZoneId.systemDefault()
        return when {
            zone.id in regionIds -> zone.id
            zone.normalized() == java.time.ZoneOffset.UTC -> "Etc/UTC"
            else -> null
        }
    }

    fun activate(id: String?) {
        active.value = try {
            id?.let(ZoneId::of) ?: fallback
        } catch (_: DateTimeException) {
            fallback
        }
    }
}
