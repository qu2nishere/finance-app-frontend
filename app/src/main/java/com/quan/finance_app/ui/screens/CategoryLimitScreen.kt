package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.theme.*

// -------------------------------------------------------------------------
// Model
// -------------------------------------------------------------------------

enum class CategoryFlow { EXPENSE, INCOME }

/** Danh mục chi đã có hạn mức. */
data class LimitedCategory(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val progress: Float,
    val note: String,
    val isOverspent: Boolean,
    val limitText: String
)

/** Danh mục chi chưa đặt hạn mức. */
data class UnlimitedCategory(
    val name: String,
    val icon: ImageVector,
    val note: String
)

/** Danh mục thu — không có hạn mức, chỉ để phân loại. */
data class IncomeCategory(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val note: String,
    val amountText: String
)

private val limitedCategories = listOf(
    LimitedCategory("Ăn uống", Icons.Outlined.Storefront, Alert500, 1.0f, "Đã chi 3,3tr · vượt 302k", true, "3,0tr"),
    LimitedCategory("Di chuyển", Icons.Outlined.LocalShipping, Heat500, 0.80f, "Đã chi 2,0tr · còn 500k", false, "2,5tr"),
    LimitedCategory("Mua sắm", Icons.Outlined.ShoppingBag, Sun500, 0.75f, "Đã chi 1,5tr · còn 500k", false, "2,0tr"),
    LimitedCategory("Giải trí", Icons.Outlined.LocalFireDepartment, Grow500, 0.43f, "Đã chi 640k · còn 860k", false, "1,5tr"),
    LimitedCategory("Sức khoẻ", Icons.Outlined.FavoriteBorder, Heat500, 0.16f, "Đã chi 480k · còn 2,5tr", false, "3,0tr")
)

private val unlimitedCategories = listOf(
    UnlimitedCategory("Hoá đơn & nhà ở", Icons.AutoMirrored.Outlined.ReceiptLong, "Chi TB 3 tháng: 1,41tr"),
    UnlimitedCategory("Điện thoại & internet", Icons.Outlined.PhoneIphone, "Chi TB 3 tháng: 320k"),
    UnlimitedCategory("Cà phê", Icons.Outlined.LocalCafe, "Chi TB 3 tháng: 640k"),
    UnlimitedCategory("Đi ăn ngoài", Icons.Outlined.Restaurant, "Chi TB 3 tháng: 480k"),
    UnlimitedCategory("Quà tặng", Icons.Outlined.CardGiftcard, "Chi TB 3 tháng: 210k"),
    UnlimitedCategory("Thú cưng", Icons.Outlined.Inventory2, "Chi TB 3 tháng: 150k"),
    UnlimitedCategory("Nhà ở", Icons.Outlined.Home, "Chi TB 3 tháng: 120k"),
    UnlimitedCategory("Giáo dục", Icons.Outlined.Category, "Chưa phát sinh"),
    UnlimitedCategory("Làm đẹp", Icons.Outlined.StarOutline, "Chưa phát sinh")
)

private val fixedIncomeCategories = listOf(
    IncomeCategory("Lương", Icons.Outlined.Payments, Grow500, "Ngày 20 · Vietcombank ●●42", "18,0tr"),
    IncomeCategory("Cho thuê", Icons.Outlined.Home, Blue500, "Ngày 5 · chưa nhận tháng này", "0₫")
)

private val variableIncomeCategories = listOf(
    IncomeCategory("Thưởng", Icons.Outlined.CardGiftcard, Sun500, "2 lần trong 3 tháng", "200k"),
    IncomeCategory("Freelance", Icons.Outlined.Edit, CatBills, "TB 3 tháng: 1,2tr", "0₫"),
    IncomeCategory("Đầu tư", Icons.Outlined.BarChart, Grow600, "Cổ tức, lãi tiết kiệm", "0₫"),
    IncomeCategory("Khác", Icons.Outlined.MoreHoriz, Ink400, "Hoàn tiền, quà tặng…", "0₫")
)

// -------------------------------------------------------------------------
// Screen
// -------------------------------------------------------------------------

@Composable
fun CategoryLimitScreen(
    onBack: () -> Unit = {},
    onEditLimit: (String) -> Unit = {},
    onEditIncomeCategory: (String) -> Unit = {},
    onViewIncomeReport: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var flow by remember { mutableStateOf(CategoryFlow.EXPENSE) }
    var query by remember { mutableStateOf("") }
    var filterIndex by remember { mutableIntStateOf(0) }

    val accentStrong = if (flow == CategoryFlow.EXPENSE) Heat600 else Grow600

    fun matches(name: String) = query.isBlank() || name.contains(query.trim(), ignoreCase = true)

    val limited = limitedCategories.filter { matches(it.name) }
    val unlimited = unlimitedCategories.filter { matches(it.name) }
    val fixedIncome = fixedIncomeCategories.filter { matches(it.name) }
    val variableIncome = variableIncomeCategories.filter { matches(it.name) }

    val showLimited = flow == CategoryFlow.EXPENSE && filterIndex != 2 && limited.isNotEmpty()
    val showUnlimited = flow == CategoryFlow.EXPENSE && filterIndex != 1 && unlimited.isNotEmpty()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfacePage),
        contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header: back · tiêu đề · "Sửa"
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = MoneyFlowTheme.colors.textSecondary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Danh mục và Hạn mức",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontCore,
                    color = MoneyFlowTheme.colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Sửa",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontCore,
                    color = if (flow == CategoryFlow.EXPENSE) Heat600 else Blue600,
                    modifier = Modifier.clickable { }
                )
            }
        }

        // 2. Chuyển tab Chi / Thu
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FlowSwitchPill(
                    label = "Chi",
                    selected = flow == CategoryFlow.EXPENSE,
                    selectedColor = Heat500,
                    modifier = Modifier.weight(1f)
                ) {
                    flow = CategoryFlow.EXPENSE
                    filterIndex = 0
                }
                FlowSwitchPill(
                    label = "Thu",
                    selected = flow == CategoryFlow.INCOME,
                    selectedColor = Grow500,
                    modifier = Modifier.weight(1f)
                ) {
                    flow = CategoryFlow.INCOME
                }
            }
        }

        // 3. Ô tìm kiếm
        item {
            CategorySearchField(
                query = query,
                onQueryChange = { query = it },
                placeholder = if (flow == CategoryFlow.EXPENSE) "Tìm danh mục…" else "Tìm danh mục thu…"
            )
        }

        // 4a. Chip lọc (chỉ tab Chi)
        if (flow == CategoryFlow.EXPENSE) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val counts = listOf(
                        "Tất cả (${limitedCategories.size + unlimitedCategories.size})",
                        "Có hạn mức (${limitedCategories.size})",
                        "Chưa đặt (${unlimitedCategories.size})"
                    )
                    counts.forEachIndexed { index, label ->
                        FilterChipPill(
                            label = label,
                            selected = filterIndex == index,
                            selectedColor = Heat500,
                            onClick = { filterIndex = index }
                        )
                    }
                }
            }

            item {
                CategoryHintBanner(
                    text = "Bấm vào một danh mục để sửa hạn mức. Vạch màu = đã có hạn mức.",
                    background = Ink50,
                    borderColor = MoneyFlowTheme.colors.borderDefault,
                    contentColor = MoneyFlowTheme.colors.textSecondary
                )
            }
        } else {
            // 4b. Nhắc: danh mục thu không có hạn mức
            item {
                CategoryHintBanner(
                    text = "Danh mục thu không đặt hạn mức — chỉ dùng để phân loại & xem báo cáo.",
                    background = Grow50,
                    borderColor = Grow100,
                    contentColor = Grow600
                )
            }
        }

        // 5. Tab Chi — nhóm "Có hạn mức"
        if (showLimited) {
            item {
                CategoryGroupLabel(title = "CÓ HẠN MỨC", trailing = "${limited.size} · tổng 12,0tr")
            }
            item {
                CategoryGroupCard {
                    limited.forEachIndexed { index, category ->
                        LimitedCategoryRow(category = category, onClick = { onEditLimit(category.name) })
                        if (index != limited.lastIndex) {
                            HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                        }
                    }
                }
            }
        }

        // 6. Tab Chi — nhóm "Chưa đặt hạn mức"
        if (showUnlimited) {
            item {
                CategoryGroupLabel(title = "CHƯA ĐẶT HẠN MỨC", trailing = "${unlimited.size}")
            }
            item {
                CategoryGroupCard {
                    unlimited.forEachIndexed { index, category ->
                        UnlimitedCategoryRow(category = category, onClick = { onEditLimit(category.name) })
                        if (index != unlimited.lastIndex) {
                            HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                        }
                    }
                }
            }
        }

        // 7. Tab Thu — nhóm "Thu cố định" / "Thu biến động"
        if (flow == CategoryFlow.INCOME) {
            if (fixedIncome.isNotEmpty()) {
                item { CategoryGroupLabel(title = "THU CỐ ĐỊNH", trailing = "${fixedIncome.size}") }
                item {
                    CategoryGroupCard {
                        fixedIncome.forEachIndexed { index, category ->
                            IncomeCategoryRow(category = category, onClick = { onEditIncomeCategory(category.name) })
                            if (index != fixedIncome.lastIndex) {
                                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                            }
                        }
                    }
                }
            }

            if (variableIncome.isNotEmpty()) {
                item { CategoryGroupLabel(title = "THU BIẾN ĐỘNG", trailing = "${variableIncome.size}") }
                item {
                    CategoryGroupCard {
                        variableIncome.forEachIndexed { index, category ->
                            IncomeCategoryRow(category = category, onClick = { onEditIncomeCategory(category.name) })
                            if (index != variableIncome.lastIndex) {
                                HorizontalDivider(color = MoneyFlowTheme.colors.borderSubtle)
                            }
                        }
                    }
                }
            }

            item {
                CategoryGroupCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onViewIncomeReport)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryIconTile(
                            icon = Icons.Outlined.BarChart,
                            tint = Grow600,
                            background = Grow50,
                            size = 32.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Xem báo cáo thu nhập",
                            fontFamily = FontCore,
                            fontSize = 14.sp,
                            color = MoneyFlowTheme.colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MoneyFlowTheme.colors.textTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 8. Không tìm thấy kết quả
        val isEmpty = if (flow == CategoryFlow.EXPENSE) !showLimited && !showUnlimited
        else fixedIncome.isEmpty() && variableIncome.isEmpty()
        if (isEmpty) {
            item {
                Text(
                    text = "Không tìm thấy danh mục nào khớp “$query”.",
                    fontFamily = FontCore,
                    fontSize = 13.sp,
                    color = MoneyFlowTheme.colors.textSecondary,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
        }

        // 9. Thêm danh mục
        item {
            DashedActionButton(
                text = if (flow == CategoryFlow.EXPENSE) "Thêm danh mục" else "Thêm danh mục thu",
                color = accentStrong,
                borderColor = if (flow == CategoryFlow.EXPENSE) Heat200 else Grow200,
                onClick = { }
            )
        }
    }
}

// -------------------------------------------------------------------------
// Sub-components
// -------------------------------------------------------------------------

@Composable
private fun FlowSwitchPill(
    label: String,
    selected: Boolean,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = if (selected) selectedColor else Color.White,
        border = if (selected) null else BorderStroke(1.dp, MoneyFlowTheme.colors.borderDefault)
    ) {
        Text(
            text = label,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontFamily = FontCore,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color.White else MoneyFlowTheme.colors.textSecondary
        )
    }
}

@Composable
private fun CategorySearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderSubtle)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = MoneyFlowTheme.colors.textTertiary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontFamily = FontCore,
                        fontSize = 14.sp,
                        color = MoneyFlowTheme.colors.textTertiary
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = FontCore,
                        fontSize = 14.sp,
                        color = MoneyFlowTheme.colors.textPrimary
                    ),
                    cursorBrush = SolidColor(MoneyFlowTheme.colors.textPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun FilterChipPill(
    label: String,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = if (selected) selectedColor else Color.Transparent,
        border = if (selected) null else BorderStroke(1.dp, MoneyFlowTheme.colors.borderDefault)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            fontFamily = FontCore,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color.White else MoneyFlowTheme.colors.textSecondary
        )
    }
}

@Composable
private fun CategoryHintBanner(
    text: String,
    background: Color,
    borderColor: Color,
    contentColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = background,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(9.dp))
            Text(
                text = text,
                fontFamily = FontCore,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = contentColor
            )
        }
    }
}

@Composable
private fun CategoryGroupLabel(title: String, trailing: String) {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(
            text = title,
            fontFamily = FontCore,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
            color = MoneyFlowTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = trailing,
            fontFamily = FontCore,
            fontSize = 11.sp,
            color = MoneyFlowTheme.colors.textTertiary
        )
    }
}

@Composable
private fun CategoryGroupCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderSubtle)
    ) {
        Column(content = content)
    }
}

@Composable
private fun CategoryIconTile(
    icon: ImageVector,
    tint: Color,
    background: Color,
    size: androidx.compose.ui.unit.Dp = 34.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(background, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}

/** Vạch màu bên trái — dấu hiệu "đã có hạn mức". */
@Composable
private fun CategoryColorBar(color: Color) {
    Box(
        modifier = Modifier
            .width(4.dp)
            .height(38.dp)
            .background(color, RoundedCornerShape(99.dp))
    )
}

@Composable
private fun LimitedCategoryRow(category: LimitedCategory, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryColorBar(category.color)
        Spacer(modifier = Modifier.width(10.dp))
        CategoryIconTile(icon = category.icon, tint = category.color, background = Ink50)
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = category.name,
                fontFamily = FontCore,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MoneyFlowTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { category.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(99.dp)),
                color = category.color,
                trackColor = Ink100,
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = category.note,
                fontFamily = FontCore,
                fontSize = 11.sp,
                fontWeight = if (category.isOverspent) FontWeight.SemiBold else FontWeight.Normal,
                color = if (category.isOverspent) Alert500 else MoneyFlowTheme.colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.width(10.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = category.limitText,
                fontFamily = FontMono,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MoneyFlowTheme.colors.textPrimary
            )
            Text(
                text = "/ tháng",
                fontFamily = FontCore,
                fontSize = 11.sp,
                color = MoneyFlowTheme.colors.textSecondary
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = "Sửa hạn mức",
            tint = MoneyFlowTheme.colors.textTertiary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun UnlimitedCategoryRow(category: UnlimitedCategory, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryColorBar(Ink200)
        Spacer(modifier = Modifier.width(10.dp))
        CategoryIconTile(
            icon = category.icon,
            tint = MoneyFlowTheme.colors.textTertiary,
            background = Ink100
        )
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = category.name,
                fontFamily = FontCore,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MoneyFlowTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = category.note,
                fontFamily = FontCore,
                fontSize = 11.sp,
                color = MoneyFlowTheme.colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.width(10.dp))
        Surface(
            shape = RoundedCornerShape(999.dp),
            color = Heat50,
            border = BorderStroke(1.dp, Heat200)
        ) {
            Text(
                text = "Đặt hạn mức",
                modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp),
                fontFamily = FontCore,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Heat600
            )
        }
    }
}

@Composable
private fun IncomeCategoryRow(category: IncomeCategory, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryColorBar(category.color)
        Spacer(modifier = Modifier.width(10.dp))
        CategoryIconTile(icon = category.icon, tint = category.color, background = Ink50)
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = category.name,
                fontFamily = FontCore,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MoneyFlowTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = category.note,
                fontFamily = FontCore,
                fontSize = 11.sp,
                color = MoneyFlowTheme.colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.width(10.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = category.amountText,
                fontFamily = FontMono,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Grow600
            )
            Text(
                text = "tháng 8",
                fontFamily = FontCore,
                fontSize = 11.sp,
                color = MoneyFlowTheme.colors.textSecondary
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = "Sửa danh mục",
            tint = MoneyFlowTheme.colors.textTertiary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun DashedActionButton(
    text: String,
    color: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = text,
                fontFamily = FontCore,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}
