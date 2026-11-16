package com.quan.finance_app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.core.*
import com.quan.finance_app.ui.theme.*

private val deletionItems = listOf(
    "42 giao dịch",
    "14 danh mục & 5 hạn mức",
    "3 mục tiêu tiết kiệm · 18,5tr",
    "3 ví & toàn bộ ảnh hoá đơn"
)

/**
 * 10i — Xoá toàn bộ dữ liệu, hộp thoại xác nhận. Mở từ Cài đặt (3e).
 * Nút "Xoá hết" chỉ bật khi gõ đúng chữ XOA.
 */
@Composable
fun DeleteDataDialog(
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    var confirmText by remember { mutableStateOf("") }
    var backupFirst by remember { mutableStateOf(true) }

    val canDelete = confirmText.trim().equals("XOA", ignoreCase = false)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MoneyFlowTheme.colors.surfaceOverlay)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismissRequest
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { }
                ),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp)
            ) {
                MfIconTile(
                    icon = Icons.Outlined.WarningAmber,
                    tint = Alert500,
                    background = Alert50,
                    size = 46.dp
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Xoá toàn bộ dữ liệu?",
                    fontFamily = FontCore,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MoneyFlowTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Không thể hoàn tác. Tài khoản vẫn giữ, nhưng mọi dữ liệu chi tiêu sẽ mất.",
                    fontFamily = FontCore,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )

                // Những gì sẽ mất
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderSubtle)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        deletionItems.forEachIndexed { index, item ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = null,
                                    tint = Alert500,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(9.dp))
                                Text(
                                    text = item,
                                    fontFamily = FontCore,
                                    fontSize = 12.sp,
                                    color = MoneyFlowTheme.colors.textSecondary
                                )
                            }
                            if (index != deletionItems.lastIndex) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                // Gõ XOA để xác nhận
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "NHẬP “XOA” ĐỂ XÁC NHẬN",
                    fontFamily = FontCore,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.9.sp,
                    color = MoneyFlowTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(7.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, if (canDelete) Alert500 else MoneyFlowTheme.colors.borderDefault)
                ) {
                    Box(modifier = Modifier.padding(horizontal = 13.dp, vertical = 13.dp)) {
                        if (confirmText.isEmpty()) {
                            Text(
                                text = "XOA",
                                fontFamily = FontCore,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MoneyFlowTheme.colors.textTertiary
                            )
                        }
                        BasicTextField(
                            value = confirmText,
                            onValueChange = { confirmText = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontFamily = FontCore,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MoneyFlowTheme.colors.textPrimary
                            ),
                            cursorBrush = SolidColor(Alert500),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Sao lưu trước khi xoá
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderSubtle)
                ) {
                    MfToggleRow(
                        title = "Tải bản sao lưu trước khi xoá",
                        checked = backupFirst,
                        onCheckedChange = { backupFirst = it }
                    )
                }

                // Huỷ / Xoá hết
                Spacer(modifier = Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onDismissRequest),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderDefault)
                    ) {
                        Text(
                            text = "Huỷ",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 15.dp),
                            textAlign = TextAlign.Center,
                            fontFamily = FontCore,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = MoneyFlowTheme.colors.textSecondary
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = canDelete, onClick = onConfirm),
                        shape = RoundedCornerShape(12.dp),
                        color = if (canDelete) Alert500 else Ink300
                    ) {
                        Text(
                            text = "Xoá hết",
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
        }
    }
}
