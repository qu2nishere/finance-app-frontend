package com.quan.finance_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.components.forms.CustomKeypad
import com.quan.finance_app.ui.theme.MoneyFlowTheme
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExpenseScreen(
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var amountStr by remember { mutableStateOf("0") }
    
    val formattedAmount = try {
        val amount = amountStr.toDouble()
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN")).apply { maximumFractionDigits = 0 }
        formatter.format(amount) + "₫"
    } catch (e: Exception) {
        "0₫"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thêm chi tiêu", style = MoneyFlowTheme.typography.title3) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = "Hủy")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MoneyFlowTheme.colors.surfacePage
                )
            )
        },
        containerColor = MoneyFlowTheme.colors.surfacePage
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Amount Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MoneyFlowTheme.spacing.gutterScreen),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = formattedAmount,
                    style = MoneyFlowTheme.typography.amountHero,
                    color = MoneyFlowTheme.colors.flowOut,
                    textAlign = TextAlign.Right
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Form Fields
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MoneyFlowTheme.colors.surfaceCard)
                    .padding(horizontal = MoneyFlowTheme.spacing.gutterScreen)
            ) {
                FormRow(icon = Icons.Default.Edit, label = "Chọn danh mục", value = "")
                Divider(color = MoneyFlowTheme.colors.borderSubtle)
                FormRow(icon = Icons.Default.Edit, label = "Thêm ghi chú...", value = "")
                Divider(color = MoneyFlowTheme.colors.borderSubtle)
                FormRow(icon = Icons.Default.DateRange, label = "Hôm nay, 14:30", value = "")
                Divider(color = MoneyFlowTheme.colors.borderSubtle)
                FormRow(icon = Icons.Default.Edit, label = "Ví", value = "Tiền mặt")
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            CustomKeypad(
                onKeyPressed = { key ->
                    when (key) {
                        "C" -> amountStr = "0"
                        else -> {
                            if (amountStr == "0" && key != ".") {
                                amountStr = key
                            } else {
                                amountStr += key
                            }
                        }
                    }
                },
                onConfirm = { onCancel() }
            )
        }
    }
}

@Composable
fun FormRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MoneyFlowTheme.colors.textSecondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MoneyFlowTheme.typography.body,
            color = if (value.isEmpty()) MoneyFlowTheme.colors.textSecondary else MoneyFlowTheme.colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        if (value.isNotEmpty()) {
            Text(
                text = value,
                style = MoneyFlowTheme.typography.body,
                color = MoneyFlowTheme.colors.textPrimary
            )
        }
    }
}
