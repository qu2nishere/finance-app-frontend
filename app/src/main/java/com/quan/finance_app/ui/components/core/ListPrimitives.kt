package com.quan.finance_app.ui.components.core

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.theme.*

/**
 * Các mảnh giao diện lặp lại nhiều lần trong wireframe: card trắng bo góc,
 * ô icon vuông, hàng trong danh sách, chip pill, header màn, footer Huỷ/Lưu.
 */

@Composable
fun MfCard(
    modifier: Modifier = Modifier,
    borderColor: Color = MoneyFlowTheme.colors.borderSubtle,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(content = content)
    }
}

@Composable
fun MfIconTile(
    icon: ImageVector,
    tint: Color = Blue600,
    background: Color = Blue50,
    size: Dp = 34.dp
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
            modifier = Modifier.size(size * 0.53f)
        )
    }
}

/** Header chuẩn: nút back · tiêu đề · hành động phụ bên phải (text hoặc icon). */
@Composable
fun MfScreenHeader(
    title: String,
    onBack: () -> Unit,
    actionText: String? = null,
    actionColor: Color = Blue600,
    onAction: () -> Unit = {},
    actionIcon: ImageVector? = null
) {
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
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontCore,
            color = MoneyFlowTheme.colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        when {
            actionIcon != null -> IconButton(onClick = onAction, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = null,
                    tint = MoneyFlowTheme.colors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            actionText != null -> Text(
                text = actionText,
                fontFamily = FontCore,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = actionColor,
                modifier = Modifier.clickable(onClick = onAction)
            )
        }
    }
}

/** Nhãn nhóm chữ hoa nhỏ, ví dụ "VÍ CỦA TÔI". */
@Composable
fun MfSectionLabel(text: String, trailing: String? = null) {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(
            text = text,
            fontFamily = FontCore,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.9.sp,
            color = MoneyFlowTheme.colors.textSecondary
        )
        if (trailing != null) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = trailing,
                fontFamily = FontMono,
                fontSize = 11.sp,
                color = MoneyFlowTheme.colors.textTertiary
            )
        }
    }
}

/**
 * Hàng danh sách: ô icon · tiêu đề (+ phụ đề) · nội dung bên phải · chevron.
 */
@Composable
fun MfRow(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color = Blue600,
    iconBackground: Color = Blue50,
    iconSize: Dp = 34.dp,
    subtitle: String? = null,
    titleColor: Color = MoneyFlowTheme.colors.textPrimary,
    titleWeight: FontWeight = FontWeight.Medium,
    showChevron: Boolean = false,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (RowScope.() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            MfIconTile(icon = icon, tint = iconTint, background = iconBackground, size = iconSize)
            Spacer(modifier = Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = FontCore,
                fontWeight = titleWeight,
                fontSize = 14.sp,
                lineHeight = 19.sp,
                color = titleColor
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontFamily = FontCore,
                    fontSize = 11.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
            }
        }
        if (trailing != null) {
            Spacer(modifier = Modifier.width(10.dp))
            trailing()
        }
        if (showChevron) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = MoneyFlowTheme.colors.textTertiary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun MfToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color = Blue600,
    iconBackground: Color = Blue50,
    subtitle: String? = null,
    accent: Color = Blue500
) {
    MfRow(
        title = title,
        modifier = modifier,
        icon = icon,
        iconTint = iconTint,
        iconBackground = iconBackground,
        subtitle = subtitle,
        trailing = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = accent,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Ink200,
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    )
}

/** Số tiền canh phải kèm dòng chú thích nhỏ bên dưới. */
@Composable
fun MfAmountTrailing(
    amount: String,
    caption: String? = null,
    amountColor: Color = MoneyFlowTheme.colors.textPrimary
) {
    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = amount,
            fontFamily = FontMono,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = amountColor
        )
        if (caption != null) {
            Text(
                text = caption,
                fontFamily = FontCore,
                fontSize = 10.sp,
                color = MoneyFlowTheme.colors.textTertiary
            )
        }
    }
}

@Composable
fun MfPillChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = Blue500,
    fillWhenSelected: Boolean = true
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = when {
            !selected -> Color.White
            fillWhenSelected -> selectedColor
            else -> selectedColor.copy(alpha = 0.10f)
        },
        border = BorderStroke(
            1.dp,
            if (selected) selectedColor else MoneyFlowTheme.colors.borderDefault
        )
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp),
            fontFamily = FontCore,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = when {
                !selected -> MoneyFlowTheme.colors.textSecondary
                fillWhenSelected -> Color.White
                else -> selectedColor
            }
        )
    }
}

/** Banner thông tin nền nhạt, dùng cho gợi ý / cảnh báo nhẹ. */
@Composable
fun MfInfoBanner(
    text: String,
    icon: ImageVector,
    background: Color,
    contentColor: Color,
    borderColor: Color = Color.Transparent,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = background,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
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
fun MfDashedAction(
    text: String,
    color: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
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

/** Cặp nút cuối màn: Huỷ (1 phần) + hành động chính (2 phần). */
@Composable
fun MfFooterActions(
    cancelText: String,
    confirmText: String,
    confirmColor: Color,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onCancel),
            shape = RoundedCornerShape(12.dp),
            color = Color.Transparent,
            border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderDefault)
        ) {
            Text(
                text = cancelText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                textAlign = TextAlign.Center,
                fontFamily = FontCore,
                fontSize = 15.sp,
                color = MoneyFlowTheme.colors.textSecondary
            )
        }
        Surface(
            modifier = Modifier
                .weight(2f)
                .clickable(onClick = onConfirm),
            shape = RoundedCornerShape(12.dp),
            color = confirmColor
        ) {
            Text(
                text = confirmText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                textAlign = TextAlign.Center,
                fontFamily = FontCore,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
            )
        }
    }
}

/** Thanh tiến độ mảnh bo tròn. */
@Composable
fun MfProgressBar(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    track: Color = Ink100
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(track, RoundedCornerShape(99.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .background(color, RoundedCornerShape(99.dp))
        )
    }
}
