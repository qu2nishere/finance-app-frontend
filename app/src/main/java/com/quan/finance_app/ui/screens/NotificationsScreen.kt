package com.quan.finance_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

private enum class NotifKind { LIMIT, TRANSACTION, GOAL, REPORT }

private data class AppNotification(
    val title: String,
    val body: String,
    val time: String,
    val icon: ImageVector,
    val tint: Color,
    val background: Color,
    val kind: NotifKind,
    val unread: Boolean = false
)

private data class NotifGroup(val label: String, val items: List<AppNotification>)

private val notificationGroups = listOf(
    NotifGroup(
        "HÔM NAY",
        listOf(
            AppNotification(
                "Ăn uống đã vượt hạn mức 302.000₫",
                "Đã chi 3.302.000₫ trên hạn mức 3.000.000₫. Xem lại hạn mức tháng này?",
                "09:20", Icons.Outlined.WarningAmber, Alert500, Alert50, NotifKind.LIMIT, unread = true
            ),
            AppNotification(
                "3 giao dịch chưa phân loại",
                "Highlands Coffee, VinMart và 1 giao dịch khác đang chờ bạn gắn danh mục.",
                "09:12", Icons.Outlined.GridView, Heat600, Heat50, NotifKind.TRANSACTION, unread = true
            ),
            AppNotification(
                "Đã ghi −72.000₫ · Highlands Coffee",
                "Trừ từ Ví Momo. Bấm để xem chi tiết hoặc hoàn tác.",
                "09:12", Icons.Outlined.NorthEast, Heat600, Heat50, NotifKind.TRANSACTION, unread = true
            )
        )
    ),
    NotifGroup(
        "HÔM QUA",
        listOf(
            AppNotification(
                "Đã nhận +18.000.000₫ · Lương tháng 8",
                "Đã tự trích 3.600.000₫ vào Quỹ dự phòng theo cài đặt 20%.",
                "20/08 · 08:03", Icons.Outlined.SouthWest, Grow600, Grow50, NotifKind.TRANSACTION
            ),
            AppNotification(
                "Tiền nhà sẽ trừ trong 3 ngày",
                "−2.000.000₫ vào ngày 25. Số dư Vietcombank sau khi trừ: 7.240.000₫.",
                "20/08 · 07:00", Icons.Outlined.CalendarMonth, Sun600, Sun50, NotifKind.TRANSACTION
            ),
            AppNotification(
                "Du lịch Đà Lạt đã đạt 45%",
                "Còn 5.500.000₫. Nạp thêm 1.400.000₫ tháng này để về đích đúng hạn.",
                "19/08", Icons.Outlined.CardGiftcard, Grow600, Grow50, NotifKind.GOAL
            )
        )
    ),
    NotifGroup(
        "TUẦN TRƯỚC",
        listOf(
            AppNotification(
                "Báo cáo tuần đã sẵn sàng",
                "Bạn chi 1.840.000₫ tuần rồi, thấp hơn tuần trước 12%.",
                "16/08", Icons.Outlined.BarChart, Blue600, Blue50, NotifKind.REPORT
            ),
            AppNotification(
                "Mục tiêu “Laptop mới” hoàn thành",
                "Đã đủ 22.000.000₫. Chúc mừng bạn.",
                "12/08", Icons.Outlined.CheckCircle, Grow600, Grow50, NotifKind.GOAL
            )
        )
    )
)

/**
 * 9c — Thông báo. Mở từ chuông ở trang chủ.
 */
@Composable
fun NotificationsScreen(
    onBack: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var filterIndex by remember { mutableIntStateOf(0) }
    // Đọc hết -> xoá chấm xanh; giữ theo tiêu đề để state sống qua recomposition
    val readTitles = remember { mutableStateListOf<String>() }

    val filterKind = when (filterIndex) {
        1 -> NotifKind.LIMIT
        2 -> NotifKind.TRANSACTION
        3 -> NotifKind.GOAL
        else -> null
    }

    val groups = notificationGroups
        .map { g -> g.copy(items = g.items.filter { filterKind == null || it.kind == filterKind }) }
        .filter { it.items.isNotEmpty() }

    val unreadCount = notificationGroups
        .flatMap { it.items }
        .count { it.unread && it.title !in readTitles }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage),
        contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header: back · Thông báo · Đọc hết · bánh răng
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = MoneyFlowTheme.colors.textSecondary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Thông báo",
                    modifier = Modifier.weight(1f),
                    fontFamily = FontCore,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = MoneyFlowTheme.colors.textPrimary
                )
                Text(
                    text = "Đọc hết",
                    fontFamily = FontCore,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Blue600,
                    modifier = Modifier.clickable {
                        notificationGroups.flatMap { it.items }.forEach {
                            if (it.title !in readTitles) readTitles.add(it.title)
                        }
                    }
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onOpenSettings, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Cài đặt thông báo",
                        tint = MoneyFlowTheme.colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Chip lọc
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val labels = listOf(
                    if (unreadCount > 0) "Tất cả · $unreadCount mới" else "Tất cả",
                    "Hạn mức", "Giao dịch", "Mục tiêu"
                )
                labels.forEachIndexed { index, label ->
                    MfPillChip(
                        label = label,
                        selected = filterIndex == index,
                        onClick = { filterIndex = index }
                    )
                }
            }
        }

        groups.forEach { group ->
            item {
                Spacer(modifier = Modifier.height(2.dp))
                MfSectionLabel(group.label)
            }
            item {
                MfCard {
                    group.items.forEachIndexed { index, notif ->
                        NotificationRow(
                            notif = notif,
                            unread = notif.unread && notif.title !in readTitles,
                            onClick = {
                                if (notif.title !in readTitles) readTitles.add(notif.title)
                            }
                        )
                        if (index != group.items.lastIndex) {
                            HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                        }
                    }
                }
            }
        }

        if (groups.isEmpty()) {
            item {
                Text(
                    text = "Không có thông báo nào trong mục này.",
                    fontFamily = FontCore,
                    fontSize = 13.sp,
                    color = MoneyFlowTheme.colors.textSecondary,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(2.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenSettings),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                color = Ink50,
                border = androidx.compose.foundation.BorderStroke(1.dp, MoneyFlowTheme.colors.borderDefault)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        tint = MoneyFlowTheme.colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(9.dp))
                    Text(
                        text = "Thông báo cũ hơn 30 ngày sẽ tự xoá. ",
                        fontFamily = FontCore,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                    Text(
                        text = "Cài đặt thông báo",
                        fontFamily = FontCore,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Blue600
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(
    notif: AppNotification,
    unread: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (unread) Blue50 else Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 13.dp),
        verticalAlignment = Alignment.Top
    ) {
        MfIconTile(
            icon = notif.icon,
            tint = notif.tint,
            background = notif.background,
            size = 34.dp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notif.title,
                fontFamily = FontCore,
                fontWeight = if (unread) FontWeight.SemiBold else FontWeight.Medium,
                fontSize = 13.5.sp,
                lineHeight = 19.sp,
                color = MoneyFlowTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notif.body,
                fontFamily = FontCore,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = MoneyFlowTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = notif.time,
                fontFamily = FontCore,
                fontSize = 10.sp,
                color = MoneyFlowTheme.colors.textTertiary
            )
        }
        if (unread) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .size(8.dp)
                    .background(Blue500, CircleShape)
            )
        }
    }
}
