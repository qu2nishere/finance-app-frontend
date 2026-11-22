package com.quan.finance_app.ui.budget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.data.CategoryLimitItem
import com.quan.finance_app.ui.components.core.MfInfoBanner
import com.quan.finance_app.ui.components.core.MfPillChip
import com.quan.finance_app.ui.screens.formatVnd
import com.quan.finance_app.ui.screens.getCategoryIcon
import com.quan.finance_app.ui.theme.*
import kotlin.math.roundToInt

private val QUICK_LIMIT_AMOUNTS = listOf(
    1_000_000L to "1tr",
    2_000_000L to "2tr",
    3_000_000L to "3tr",
    5_000_000L to "5tr"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryLimitSheet(
    availableCategories: List<ExpenseCategoryOption>,
    editingLimit: CategoryLimitItem?,
    submitting: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onCreate: (category: ExpenseCategoryOption, limitAmount: Long, alertThreshold: Int) -> Unit,
    onUpdate: (limitId: String, limitAmount: Long, alertThreshold: Int) -> Unit,
    onDelete: (limitId: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isEditMode = editingLimit != null

    var selectedCategory by remember(editingLimit, availableCategories) {
        mutableStateOf(availableCategories.firstOrNull())
    }
    var amountDigits by remember(editingLimit) {
        mutableStateOf(editingLimit?.limitAmount?.toString() ?: "")
    }
    var thresholdPercent by remember(editingLimit) {
        mutableFloatStateOf((editingLimit?.alertThreshold ?: 80).coerceIn(50, 100).toFloat())
    }

    val parsedAmount = amountDigits.toLongOrNull() ?: 0L
    val canSubmit = !submitting && parsedAmount > 0L && (isEditMode || selectedCategory != null)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MoneyFlowTheme.colors.surfaceCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = MoneyFlowTheme.spacing.gutterScreen)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isEditMode) "Sửa hạn mức danh mục" else "Đặt hạn mức danh mục",
                style = MoneyFlowTheme.typography.title2,
                color = MoneyFlowTheme.colors.textPrimary
            )

            if (editingLimit != null) {
                Surface(
                    shape = RoundedCornerShape(MoneyFlowTheme.radii.card),
                    color = MoneyFlowTheme.colors.surfacePage,
                    border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderCard)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(MoneyFlowTheme.colors.surfaceOutSoft, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getCategoryIcon(editingLimit.categoryName),
                                contentDescription = editingLimit.categoryName,
                                tint = MoneyFlowTheme.colors.flowOut,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = editingLimit.categoryName,
                                style = MoneyFlowTheme.typography.title3,
                                color = MoneyFlowTheme.colors.textPrimary
                            )
                            Text(
                                text = "Đã chi ${formatVnd(editingLimit.spentAmount)} / ${formatVnd(editingLimit.limitAmount)}",
                                style = MoneyFlowTheme.typography.caption,
                                color = MoneyFlowTheme.colors.textSecondary
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "Chọn danh mục chi tiêu",
                    style = MoneyFlowTheme.typography.bodySm,
                    fontWeight = FontWeight.SemiBold,
                    color = MoneyFlowTheme.colors.textSecondary
                )

                if (availableCategories.isEmpty()) {
                    MfInfoBanner(
                        text = "Tất cả danh mục chi tiêu của bạn đều đã có hạn mức trong tháng này.",
                        icon = Icons.Outlined.WarningAmber,
                        background = Sun50,
                        contentColor = Sun600,
                        borderColor = Sun100
                    )
                } else {
                    CategorySelectionGrid(
                        categories = availableCategories,
                        selectedId = selectedCategory?.id,
                        onSelect = { selectedCategory = it }
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Hạn mức tháng (VND)",
                    style = MoneyFlowTheme.typography.bodySm,
                    fontWeight = FontWeight.SemiBold,
                    color = MoneyFlowTheme.colors.textSecondary
                )
                OutlinedTextField(
                    value = amountDigits,
                    onValueChange = { raw ->
                        amountDigits = raw.filter { it.isDigit() }.take(12)
                    },
                    placeholder = { Text("VD: 3000000") },
                    suffix = { Text("₫", fontFamily = FontMono, fontWeight = FontWeight.Bold) },
                    supportingText = if (parsedAmount > 0L) {
                        { Text(formatVnd(parsedAmount), fontFamily = FontMono, color = MoneyFlowTheme.colors.textBrand) }
                    } else null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(MoneyFlowTheme.radii.control),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QUICK_LIMIT_AMOUNTS.forEach { (value, label) ->
                        MfPillChip(
                            label = label,
                            selected = parsedAmount == value,
                            selectedColor = Blue500,
                            onClick = { amountDigits = value.toString() },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ngưỡng cảnh báo",
                        style = MoneyFlowTheme.typography.bodySm,
                        fontWeight = FontWeight.SemiBold,
                        color = MoneyFlowTheme.colors.textSecondary
                    )
                    Text(
                        text = "${thresholdPercent.roundToInt()}%",
                        fontFamily = FontMono,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Sun600
                    )
                }
                Slider(
                    value = thresholdPercent,
                    onValueChange = { thresholdPercent = ((it / 5f).roundToInt() * 5).coerceIn(50, 100).toFloat() },
                    valueRange = 50f..100f,
                    steps = 9,
                    colors = SliderDefaults.colors(
                        thumbColor = Blue500,
                        activeTrackColor = Blue500,
                        inactiveTrackColor = Ink100
                    )
                )
                Text(
                    text = "Chuyển màu cảnh báo khi chi đạt ${thresholdPercent.roundToInt()}% hạn mức.",
                    style = MoneyFlowTheme.typography.caption,
                    color = MoneyFlowTheme.colors.textTertiary
                )
            }

            if (!errorMessage.isNullOrBlank()) {
                MfInfoBanner(
                    text = errorMessage,
                    icon = Icons.Outlined.WarningAmber,
                    background = Alert50,
                    contentColor = Alert600,
                    borderColor = Alert100
                )
            }

            if (editingLimit != null) {
                OutlinedButton(
                    onClick = { onDelete(editingLimit.id) },
                    enabled = !submitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(MoneyFlowTheme.radii.control),
                    border = BorderStroke(1.dp, Alert100),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Alert500)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Bỏ hạn mức",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Bỏ hạn mức",
                        fontFamily = FontCore,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    enabled = !submitting,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(MoneyFlowTheme.radii.control),
                    border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderSubtle),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MoneyFlowTheme.colors.textSecondary)
                ) {
                    Text("Huỷ", fontFamily = FontCore, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        val threshold = thresholdPercent.roundToInt()
                        if (editingLimit != null) {
                            onUpdate(editingLimit.id, parsedAmount, threshold)
                        } else {
                            selectedCategory?.let { cat -> onCreate(cat, parsedAmount, threshold) }
                        }
                    },
                    enabled = canSubmit,
                    modifier = Modifier
                        .weight(2f)
                        .height(48.dp),
                    shape = RoundedCornerShape(MoneyFlowTheme.radii.control),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MoneyFlowTheme.colors.surfaceBrand,
                        contentColor = Color.White
                    )
                ) {
                    if (submitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (isEditMode) "Lưu thay đổi" else "Lưu hạn mức",
                            fontFamily = FontCore,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategorySelectionGrid(
    categories: List<ExpenseCategoryOption>,
    selectedId: String?,
    onSelect: (ExpenseCategoryOption) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        categories.chunked(4).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { category ->
                    val isSelected = category.id == selectedId
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Blue50 else Color.Transparent)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) Blue500 else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelect(category) }
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Blue100 else MoneyFlowTheme.colors.surfaceOutSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getCategoryIcon(category.name),
                                contentDescription = category.name,
                                tint = if (isSelected) Blue600 else MoneyFlowTheme.colors.flowOut,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = category.name,
                            style = MoneyFlowTheme.typography.caption,
                            color = if (isSelected) Blue700 else MoneyFlowTheme.colors.textPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                repeat(4 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
