package com.quan.finance_app.ui.components.core

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.theme.*

/**
 * Turn 10 của wireframe đổi mọi số tiền / phần trăm cố định thành ô sửa được.
 * Đây là các mảnh dùng lại cho kiểu đó.
 */

/** Chip nhỏ viền màu, hiện số + bút chì. Bấm vào để sửa. */
@Composable
fun MfEditableChip(
    value: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        border = BorderStroke(1.dp, accent)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                fontFamily = FontMono,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = accent
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Sửa",
                tint = accent,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

/**
 * Ô nhập số lớn (tiền hoặc %) kèm cặp nút tăng/giảm bên phải.
 * Gõ trực tiếp được; nút chỉ cộng/trừ theo [step].
 */
@Composable
fun MfAmountStepperField(
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String,
    accent: Color,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.5.dp, accent)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(
                        fontFamily = FontMono,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        color = accent
                    ),
                    cursorBrush = SolidColor(accent),
                    modifier = Modifier.weight(1f, fill = false).widthIn(min = 24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = suffix,
                    fontFamily = FontCore,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = accent,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            MfStepperButton(increment = true, onClick = onIncrement)
            MfStepperButton(increment = false, onClick = onDecrement)
        }
    }
}

@Composable
fun MfStepperButton(increment: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(width = 40.dp, height = 28.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(7.dp),
        color = Color.White,
        border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderDefault)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (increment) Icons.Outlined.KeyboardArrowUp
                else Icons.Outlined.KeyboardArrowDown,
                contentDescription = if (increment) "Tăng" else "Giảm",
                tint = MoneyFlowTheme.colors.textSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/** Ô vuông tick (checkbox bo góc) dùng trong danh sách chọn nhiều. */
@Composable
fun MfCheckSquare(
    checked: Boolean,
    accent: Color = Blue500,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .background(if (checked) accent else Color.White, RoundedCornerShape(6.dp))
            .then(
                if (checked) Modifier
                else Modifier.border(1.5.dp, MoneyFlowTheme.colors.borderStrong, RoundedCornerShape(6.dp))
            )
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

/** Nút tròn chọn một (radio). */
@Composable
fun MfRadioDot(
    selected: Boolean,
    accent: Color = Blue500,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .background(if (selected) accent else Color.White, CircleShape)
            .then(
                if (selected) Modifier
                else Modifier.border(1.5.dp, MoneyFlowTheme.colors.borderStrong, CircleShape)
            )
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

/** Nhóm pill chọn một trong nhiều, dùng cho "Tuần / Tháng / Năm" kiểu hàng cài đặt. */
@Composable
fun MfSegmentedPills(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    accent: Color = Blue500,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        options.forEachIndexed { index, label ->
            MfPillChip(
                label = label,
                selected = selectedIndex == index,
                selectedColor = accent,
                onClick = { onSelect(index) }
            )
        }
    }
}

/** Thanh chia hai màu: phần chi (trái) và phần tiết kiệm (phải). */
@Composable
fun MfSplitBar(
    leftFraction: Float,
    leftColor: Color,
    rightColor: Color,
    modifier: Modifier = Modifier,
    height: Dp = 10.dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(Ink100, RoundedCornerShape(99.dp))
    ) {
        Box(
            modifier = Modifier
                .weight(leftFraction.coerceIn(0.01f, 0.99f))
                .fillMaxHeight()
                .background(leftColor, RoundedCornerShape(topStart = 99.dp, bottomStart = 99.dp))
        )
        Box(
            modifier = Modifier
                .weight((1f - leftFraction).coerceIn(0.01f, 0.99f))
                .fillMaxHeight()
                .background(rightColor, RoundedCornerShape(topEnd = 99.dp, bottomEnd = 99.dp))
        )
    }
}
