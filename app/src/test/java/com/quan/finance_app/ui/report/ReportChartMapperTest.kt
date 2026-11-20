package com.quan.finance_app.ui.report

import com.quan.finance_app.data.MonthlyBarPoint
import com.quan.finance_app.data.MonthlyTrendData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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

    @Test
    fun buildFiveMonthDualBars_normalizesBySharedMaxAndTreatsMissingMonthsAsZero() {
        val incomePoints = listOf(
            MonthlyBarPoint("2026-06", 2026, 6, 10_000_000L, 1, false),
            MonthlyBarPoint("2026-08", 2026, 8, 20_000_000L, 2, true)
        )
        val expensePoints = listOf(
            MonthlyBarPoint("2026-07", 2026, 7, 5_000_000L, 1, false),
            MonthlyBarPoint("2026-08", 2026, 8, 10_000_000L, 3, true)
        )

        val bars = buildFiveMonthDualBars(incomePoints, expensePoints, 2026, 8)
        assertEquals(5, bars.size)
        assertEquals(listOf("T4", "T5", "T6", "T7", "T8"), bars.map { it.label })
        // T4 & T5 thiếu ở cả 2 chuỗi → 0
        assertEquals(0f, bars[0].inFraction, 0.001f)
        assertEquals(0f, bars[0].outFraction, 0.001f)
        // T6 chỉ có thu 10tr / max 20tr = 0.5
        assertEquals(0.5f, bars[2].inFraction, 0.001f)
        assertEquals(0f, bars[2].outFraction, 0.001f)
        // T7 chỉ có chi 5tr / max 20tr = 0.25
        assertEquals(0f, bars[3].inFraction, 0.001f)
        assertEquals(0.25f, bars[3].outFraction, 0.001f)
        // T8 thu 20tr (1.0), chi 10tr (0.5), isCurrent = true
        assertEquals(1.0f, bars[4].inFraction, 0.001f)
        assertEquals(0.5f, bars[4].outFraction, 0.001f)
        assertEquals(true, bars[4].isCurrent)
    }

    @Test
    fun buildFiveMonthDualBars_allZeroReturnsZeroFractionsWithoutCrash() {
        val bars = buildFiveMonthDualBars(emptyList(), emptyList(), 2026, 8)
        assertEquals(5, bars.size)
        assertEquals(listOf(0f, 0f, 0f, 0f, 0f), bars.map { it.inFraction })
        assertEquals(listOf(0f, 0f, 0f, 0f, 0f), bars.map { it.outFraction })
    }

    @Test
    fun netMomPercent_returnsNullWhenPreviousNetIsZeroOrBothNull() {
        assertNull(netMomPercent(null, null))
        val income = trendOf(selected = 5_000_000L, previous = 2_000_000L, type = "INCOME")
        val expense = trendOf(selected = 3_000_000L, previous = 2_000_000L, type = "EXPENSE")
        // previousNet = 2tr - 2tr = 0 → null
        assertNull(netMomPercent(income, expense))
    }

    @Test
    fun netMomPercent_calculatesPositiveAndNegativeChangesCorrectly() {
        // Tháng trước ròng = +5tr, tháng này ròng = +6tr → +20%
        val pctUp = netMomPercent(
            trendOf(selected = 10_000_000L, previous = 8_000_000L, type = "INCOME"),
            trendOf(selected = 4_000_000L, previous = 3_000_000L, type = "EXPENSE")
        )
        assertNotNull(pctUp)
        assertEquals(20.0, pctUp!!, 0.001)

        // Tháng trước ròng = -2tr, tháng này ròng = +2tr → cải thiện +200%
        val pctRecover = netMomPercent(
            trendOf(selected = 5_000_000L, previous = 1_000_000L, type = "INCOME"),
            trendOf(selected = 3_000_000L, previous = 3_000_000L, type = "EXPENSE")
        )
        assertNotNull(pctRecover)
        assertEquals(200.0, pctRecover!!, 0.001)
    }

    private fun trendOf(selected: Long, previous: Long, type: String) = MonthlyTrendData(
        period = "2026-08",
        type = type,
        categoryId = null,
        currencyCode = "VND",
        selectedAmount = selected,
        previousAmount = previous,
        momChangeAmount = selected - previous,
        momChangePercent = null,
        points = emptyList(),
        categoryBreakdown = emptyList()
    )
}
