package com.quan.finance_app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.components.forms.CalculatorKeypad
import com.quan.finance_app.ui.theme.MoneyFlowTheme
import com.quan.finance_app.ui.theme.FontCore
import com.quan.finance_app.ui.theme.FontMono
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onDismissRequest: () -> Unit,
    onOpenAutoSave: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isExpense by remember { mutableStateOf(true) }
    // 4b: mức trích cho chính khoản thu này — sửa tại chỗ bằng "Đổi %"
    var autoSaveEnabled by remember { mutableStateOf(true) }
    var autoSavePercent by remember { mutableIntStateOf(20) }
    var expression by remember { mutableStateOf("") }
    var showCalculator by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("vi", "VN")) }
    
    // Simple evaluator
    val calculatedAmount = remember(expression) {
        evaluateExpression(expression)
    }

    val formattedCalculated = try {
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN")).apply { maximumFractionDigits = 0 }
        formatter.format(calculatedAmount)
    } catch (e: Exception) {
        "0"
    }

    // Animations for 3D shrink effect
    val scale by animateFloatAsState(
        targetValue = if (showDatePicker) 0.92f else 1f, 
        animationSpec = tween(300),
        label = "scale"
    )
    val cornerRadius by animateDpAsState(
        targetValue = if (showDatePicker) 24.dp else 0.dp,
        animationSpec = tween(300),
        label = "cornerRadius"
    )

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        // Main Screen Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin(0.5f, 0.05f)
                    clip = true
                    shape = RoundedCornerShape(cornerRadius)
                }
                .background(MoneyFlowTheme.colors.surfaceCard)
                .navigationBarsPadding() // Thêm padding sau background để nền trắng phủ kín viền
                .statusBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismissRequest) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = MoneyFlowTheme.colors.textPrimary)
                }
                Text(
                    text = "Thêm giao dịch",
                    style = MoneyFlowTheme.typography.title2,
                    color = MoneyFlowTheme.colors.textPrimary,
                    fontFamily = FontCore
                )
                TextButton(onClick = { /* Repeat */ }) {
                    Text(
                        text = "Lặp lại",
                        style = MoneyFlowTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                        color = MoneyFlowTheme.colors.textBrand,
                        fontFamily = FontCore
                    )
                }
            }

            // Toggle Chi / Thu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Chi Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isExpense) MoneyFlowTheme.colors.flowOut else Color.White)
                        .border(1.dp, if (isExpense) Color.Transparent else MoneyFlowTheme.colors.borderSubtle, RoundedCornerShape(24.dp))
                        .clickable { isExpense = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Chi",
                        color = if (isExpense) Color.White else MoneyFlowTheme.colors.textSecondary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontCore
                    )
                }
                // Thu Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (!isExpense) MoneyFlowTheme.colors.flowIn else Color.White)
                        .border(1.dp, if (!isExpense) Color.Transparent else MoneyFlowTheme.colors.borderSubtle, RoundedCornerShape(24.dp))
                        .clickable { isExpense = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Thu",
                        color = if (!isExpense) Color.White else MoneyFlowTheme.colors.textSecondary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontCore
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Amount Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null
                    ) { showCalculator = !showCalculator },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = expression.ifEmpty { "0" },
                    color = MoneyFlowTheme.colors.textSecondary,
                    fontSize = 16.sp,
                    fontFamily = FontMono
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = formattedCalculated,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isExpense) MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn,
                        fontFamily = FontMono
                    )
                    Text(
                        text = "đ",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isExpense) MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn,
                        modifier = Modifier.padding(bottom = 6.dp),
                        fontFamily = FontCore
                    )
                }
                // Underline
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(2.dp)
                        .background(if (isExpense) MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Selectors
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isExpense) {
                    SelectorButton(
                        icon = Icons.Outlined.Storefront,
                        label = "Danh mục · ",
                        value = "Ăn uống",
                        valueBold = true,
                        brandColor = MoneyFlowTheme.colors.flowOut,
                        brandSoftColor = MoneyFlowTheme.colors.surfaceOutSoft
                    )
                    SelectorButton(
                        icon = Icons.Outlined.AccountBalanceWallet,
                        label = "Nguồn tiền · ",
                        value = "Ví Momo",
                        valueBold = false,
                        brandColor = MoneyFlowTheme.colors.flowOut,
                        brandSoftColor = MoneyFlowTheme.colors.surfaceOutSoft
                    )
                } else {
                    SelectorButton(
                        icon = Icons.Outlined.Payments,
                        label = "Nguồn thu · ",
                        value = "Lương",
                        valueBold = true,
                        brandColor = MoneyFlowTheme.colors.flowIn,
                        brandSoftColor = MoneyFlowTheme.colors.surfaceInSoft
                    )
                    
                    // Quick Chips Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(listOf("Lương", "Thưởng", "Freelance", "Đầu tư")) { chip ->
                            val isSelected = chip == "Lương"
                            Box(
                                modifier = Modifier
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(if (isSelected) MoneyFlowTheme.colors.surfaceInSoft else Color.White)
                                    .border(1.dp, if (isSelected) MoneyFlowTheme.colors.flowIn else MoneyFlowTheme.colors.borderSubtle, RoundedCornerShape(18.dp))
                                    .clickable { /* select chip */ }
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = chip,
                                    color = if (isSelected) MoneyFlowTheme.colors.flowIn else MoneyFlowTheme.colors.textSecondary,
                                    style = MoneyFlowTheme.typography.caption,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    
                    SelectorButton(
                        icon = Icons.Outlined.AccountBalanceWallet,
                        label = "Vào ví · ",
                        value = "Vietcombank ••42",
                        valueBold = false,
                        brandColor = MoneyFlowTheme.colors.borderSubtle,
                        brandSoftColor = MoneyFlowTheme.colors.surfacePage
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Actions Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date Action
                Row(
                    modifier = Modifier
                        .weight(1.5f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MoneyFlowTheme.colors.surfacePage)
                        .clickable { showDatePicker = true }
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = MoneyFlowTheme.colors.textSecondary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = selectedDate.format(dateFormatter), style = MoneyFlowTheme.typography.caption, color = MoneyFlowTheme.colors.textPrimary, fontFamily = FontCore)
                }

                if (isExpense) {
                    // Note Action
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .border(1.dp, MoneyFlowTheme.colors.surfaceOutSoft, RoundedCornerShape(12.dp))
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, tint = MoneyFlowTheme.colors.flowOut, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ghi chú", style = MoneyFlowTheme.typography.caption, color = MoneyFlowTheme.colors.flowOut, fontFamily = FontCore)
                    }

                    // Camera Action
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .border(1.dp, MoneyFlowTheme.colors.surfaceOutSoft, RoundedCornerShape(12.dp))
                            .background(Color.White, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = MoneyFlowTheme.colors.flowOut, modifier = Modifier.size(18.dp))
                    }
                } else {
                    // Repeat Action
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .border(1.dp, MoneyFlowTheme.colors.borderSubtle, RoundedCornerShape(12.dp))
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Outlined.Sync, contentDescription = null, tint = MoneyFlowTheme.colors.textSecondary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Lặp hằng tháng", style = MoneyFlowTheme.typography.caption, color = MoneyFlowTheme.colors.textSecondary, fontFamily = FontCore)
                    }
                }
            }
            
            if (!isExpense) {
                Spacer(modifier = Modifier.height(16.dp))
                AutoSaveIncomeCard(
                    enabled = autoSaveEnabled,
                    onEnabledChange = { autoSaveEnabled = it },
                    percent = autoSavePercent,
                    incomeAmount = calculatedAmount,
                    onChangePercent = {
                        autoSavePercent = if (autoSavePercent >= 50) 5 else autoSavePercent + 5
                    },
                    onOpenSettings = onOpenAutoSave,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Save Button
            Button(
                onClick = onDismissRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isExpense) MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (isExpense) "Lưu khoản chi" else "Lưu khoản thu",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = FontCore
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }

            // Calculator Keypad
            AnimatedVisibility(
                visible = showCalculator,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                CalculatorKeypad(
                    brandColor = if (isExpense) MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn,
                    brandSoftColor = if (isExpense) MoneyFlowTheme.colors.surfaceOutSoft else MoneyFlowTheme.colors.surfaceInSoft,
                    onKeyPressed = { key ->
                        when (key) {
                            "C" -> expression = ""
                            "⌫" -> {
                                if (expression.isNotEmpty()) {
                                    expression = expression.dropLast(1)
                                }
                            }
                            "=" -> {
                                expression = calculatedAmount.toLong().toString()
                                showCalculator = false // Tự động đóng bàn phím khi ấn =
                            }
                            else -> {
                                expression += key
                            }
                        }
                    }
                )
            }
            
            if (!showCalculator) {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Overlay scrim when date picker is shown
        if (showDatePicker) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable { showDatePicker = false }
            )
        }
    }

    if (showDatePicker) {
        com.quan.finance_app.ui.components.forms.DateTimePickerSheet(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { date -> selectedDate = date },
            brandColor = if (isExpense) MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn,
            brandSoftColor = if (isExpense) MoneyFlowTheme.colors.surfaceOutSoft else MoneyFlowTheme.colors.surfaceInSoft
        )
    }
}

@Composable
fun SelectorButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueBold: Boolean,
    brandColor: Color,
    brandSoftColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, brandColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(brandSoftColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = brandColor, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MoneyFlowTheme.typography.body,
            color = MoneyFlowTheme.colors.textSecondary,
            fontFamily = FontCore
        )
        Text(
            text = value,
            style = MoneyFlowTheme.typography.body.copy(fontWeight = if (valueBold) FontWeight.Bold else FontWeight.Normal),
            color = MoneyFlowTheme.colors.textPrimary,
            fontFamily = FontCore
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = MoneyFlowTheme.colors.textSecondary)
    }
}

// Very basic math expression evaluator
fun evaluateExpression(expr: String): Double {
    if (expr.isEmpty()) return 0.0
    // Replace visual operators with standard ones, remove formatting dots
    val cleanExpr = expr.replace("×", "*").replace("÷", "/").replace(".", "")
    
    // A simplified evaluator that processes operators sequentially for this demo
    return try {
        var currentNumber = ""
        var currentResult = 0.0
        var currentOp = '+'
        
        for (i in cleanExpr.indices) {
            val char = cleanExpr[i]
            if (char.isDigit() || char == ',') {
                // Ignore comma for now or replace with dot if it's decimal
                if (char != ',') {
                    currentNumber += char
                }
            } else if (char in listOf('+', '-', '*', '/')) {
                val num = currentNumber.toDoubleOrNull() ?: 0.0
                currentResult = applyOp(currentResult, num, currentOp)
                currentOp = char
                currentNumber = ""
            }
        }
        if (currentNumber.isNotEmpty()) {
            val num = currentNumber.toDoubleOrNull() ?: 0.0
            currentResult = applyOp(currentResult, num, currentOp)
        }
        currentResult
    } catch (e: Exception) {
        0.0
    }
}

fun applyOp(a: Double, b: Double, op: Char): Double {
    return when (op) {
        '+' -> a + b
        '-' -> a - b
        '*' -> a * b
        '/' -> if (b != 0.0) a / b else 0.0
        else -> b
    }
}
