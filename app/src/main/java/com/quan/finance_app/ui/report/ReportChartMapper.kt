package com.quan.finance_app.ui.report

import com.quan.finance_app.data.MonthlyBarPoint
import java.time.LocalDate
import java.time.YearMonth

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
