package com.quan.finance_app.ui.report

import com.quan.finance_app.data.MonthlyBarPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class ReportChartMapperTest {

    @Test
    fun averagePerDay_pastMonthDividesByFullMonthLength() {
        val ym = YearMonth.of(2026, 7) // 31 ngày
        val today = LocalDate.of(2026, 8, 15)
        assertEquals(310_000L, averagePerDay(9_610_000L, ym, today))
    }

    @Test
    fun averagePerDay_currentMonthDividesByElapsedDays() {
        val ym = YearMonth.of(2026, 8)
        val today = LocalDate.of(2026, 8, 10)
        assertEquals(300_000L, averagePerDay(3_000_000L, ym, today))
    }

    @Test
    fun averagePerDay_zeroOrFutureMonthReturnsZero() {
        val today = LocalDate.of(2026, 8, 10)
        assertEquals(0L, averagePerDay(0L, YearMonth.of(2026, 8), today))
        assertEquals(0L, averagePerDay(5_000_000L, YearMonth.of(2026, 9), today))
    }

    @Test
    fun forecastMonthEnd_onlyPresentForCurrentMonth() {
        val today = LocalDate.of(2026, 8, 10)
        // Tháng 8 có 31 ngày, 3tr trong 10 ngày → TB 300k/ngày → dự báo 9.300.000
        assertEquals(9_300_000L, forecastMonthEnd(3_000_000L, YearMonth.of(2026, 8), today))
        // Tháng quá khứ → null
        assertNull(forecastMonthEnd(9_610_000L, YearMonth.of(2026, 7), today))
        // Tháng tương lai → null
        assertNull(forecastMonthEnd(1_000_000L, YearMonth.of(2026, 9), today))
    }

    @Test
    fun buildFiveMonthBarTriples_emptyOrNullReturnsFiveZeroBarsEndingAtSelectedMonth() {
        val bars = buildFiveMonthBarTriples(null, 2026, 8)
        assertEquals(5, bars.size)
        assertEquals(listOf("T4", "T5", "T6", "T7", "T8"), bars.map { it.first })
        assertEquals(listOf(0f, 0f, 0f, 0f, 0f), bars.map { it.second })
        assertEquals(listOf(false, false, false, false, true), bars.map { it.third })
    }

    @Test
    fun buildFiveMonthBarTriples_normalizesByMaxAmount() {
        val points = listOf(
            MonthlyBarPoint("2026-04", 2026, 4, 2_000_000L, 1, false),
            MonthlyBarPoint("2026-05", 2026, 5, 4_000_000L, 1, false),
            MonthlyBarPoint("2026-06", 2026, 6, 0L, 0, false),
            MonthlyBarPoint("2026-07", 2026, 7, 8_000_000L, 2, false),
            MonthlyBarPoint("2026-08", 2026, 8, 6_000_000L, 2, true)
        )
        val bars = buildFiveMonthBarTriples(points, 2026, 8)
        assertEquals(0.25f, bars[0].second, 0.001f)
        assertEquals(0.50f, bars[1].second, 0.001f)
        assertEquals(0.00f, bars[2].second, 0.001f)
        assertEquals(1.00f, bars[3].second, 0.001f)
        assertEquals(0.75f, bars[4].second, 0.001f)
        assertEquals(true, bars[4].third)
    }
}
