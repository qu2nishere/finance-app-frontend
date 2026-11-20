package com.quan.finance_app.ui.report

import com.quan.finance_app.data.MonthlyBarPoint
import com.quan.finance_app.data.MonthlyTrendData
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale
import kotlin.math.abs

data class DualBarData(
    val label: String,
    val inFraction: Float,
    val outFraction: Float,
    val isCurrent: Boolean
)

/**
 * Chuẩn hoá dữ liệu 5 tháng kết thúc ở (year, month) sang danh sách (nhãn, tỉ lệ [0..1], là tháng chọn).
 * Chỉ đổi sang Float ở bước vẽ cột; khi tất cả tháng bằng 0 thì trả tỉ lệ 0f để tránh chia 0.
 */
fun buildFiveMonthBarTriples(
    points: List<MonthlyBarPoint>?,
    year: Int,
    month: Int
): List<Triple<String, Float, Boolean>> {
    if (points.isNullOrEmpty()) {
        val baseYm = YearMonth.of(year, month)
        return (4 downTo 0).map { offset ->
            val ym = baseYm.minusMonths(offset.toLong())
            Triple("T${ym.monthValue}", 0f, offset == 0)
        }
    }
    val maxAmount = points.maxOfOrNull { it.totalAmount } ?: 0L
    return points.map { p ->
        val fraction = if (maxAmount > 0L) {
            (p.totalAmount.toFloat() / maxAmount.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
        Triple("T${p.month}", fraction, p.isSelected)
    }
}

/**
 * Trung bình chi tiêu 1 ngày (VND, Long):
 * - Tháng hiện tại: tổng / số ngày đã qua (tính cả hôm nay).
 * - Tháng quá khứ: tổng / số ngày của tháng đó.
 * - Tháng tương lai: 0.
 */
fun averagePerDay(
    total: Long,
    ym: YearMonth,
    today: LocalDate
): Long {
    if (total <= 0L) return 0L
    val currentYm = YearMonth.from(today)
    val days = when {
        ym == currentYm -> today.dayOfMonth.coerceAtLeast(1)
        ym.isBefore(currentYm) -> ym.lengthOfMonth()
        else -> return 0L
    }
    return (total + days / 2L) / days
}

/**
 * Dự báo tổng chi cuối tháng (VND, Long) = TB/ngày × số ngày của tháng.
 * Chỉ tính cho tháng hiện tại; trả null với tháng quá khứ/tương lai.
 */
fun forecastMonthEnd(
    total: Long,
    ym: YearMonth,
    today: LocalDate
): Long? {
    if (ym != YearMonth.from(today)) return null
    val avg = averagePerDay(total, ym, today)
    return avg * ym.lengthOfMonth()
}

/**
 * Ghép 2 chuỗi 5 tháng (Thu & Chi) kết thúc ở (year, month) thành 5 cặp cột Vào/Ra,
 * chuẩn hoá theo giá trị lớn nhất chung của cả 2 chuỗi; tháng thiếu dữ liệu tính là 0.
 */
fun buildFiveMonthDualBars(
    incomePoints: List<MonthlyBarPoint>?,
    expensePoints: List<MonthlyBarPoint>?,
    year: Int,
    month: Int
): List<DualBarData> {
    val baseYm = YearMonth.of(year, month)
    val incomeByPeriod = incomePoints?.associateBy { it.period } ?: emptyMap()
    val expenseByPeriod = expensePoints?.associateBy { it.period } ?: emptyMap()

    val rawWindow = (4 downTo 0).map { offset ->
        val ym = baseYm.minusMonths(offset.toLong())
        val period = String.format(Locale.ROOT, "%04d-%02d", ym.year, ym.monthValue)
        val inAmount = (incomeByPeriod[period]?.totalAmount ?: 0L).coerceAtLeast(0L)
        val outAmount = (expenseByPeriod[period]?.totalAmount ?: 0L).coerceAtLeast(0L)
        Triple(ym, inAmount to outAmount, offset == 0)
    }

    val maxAmount = rawWindow.maxOfOrNull { maxOf(it.second.first, it.second.second) } ?: 0L
    return rawWindow.map { (ym, amounts, isCurrent) ->
        val (inAmount, outAmount) = amounts
        val inFraction = if (maxAmount > 0L) {
            (inAmount.toFloat() / maxAmount.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
        val outFraction = if (maxAmount > 0L) {
            (outAmount.toFloat() / maxAmount.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
        DualBarData(
            label = "T${ym.monthValue}",
            inFraction = inFraction,
            outFraction = outFraction,
            isCurrent = isCurrent
        )
    }
}

/**
 * % thay đổi dòng tiền ròng so với tháng liền trước.
 * Trả null khi chưa có dữ liệu hoặc ròng tháng trước = 0 (D5).
 */
fun netMomPercent(
    incomeTrend: MonthlyTrendData?,
    expenseTrend: MonthlyTrendData?
): Double? {
    if (incomeTrend == null && expenseTrend == null) return null
    val currentNet = (incomeTrend?.selectedAmount ?: 0L) - (expenseTrend?.selectedAmount ?: 0L)
    val previousNet = (incomeTrend?.previousAmount ?: 0L) - (expenseTrend?.previousAmount ?: 0L)
    if (previousNet == 0L) return null
    return ((currentNet - previousNet).toDouble() / abs(previousNet).toDouble()) * 100.0
}
