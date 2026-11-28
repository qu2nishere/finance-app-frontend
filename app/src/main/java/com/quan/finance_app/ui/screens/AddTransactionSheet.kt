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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quan.finance_app.data.remote.CategoryDto
import com.quan.finance_app.data.remote.WalletDto
import com.quan.finance_app.ui.components.core.SkeletonBox
import com.quan.finance_app.ui.components.forms.CalculatorKeypad
import com.quan.finance_app.ui.theme.FontCore
import com.quan.finance_app.ui.theme.FontMono
import com.quan.finance_app.ui.theme.MoneyFlowTheme
import com.quan.finance_app.ui.transaction.TransactionTime
import com.quan.finance_app.ui.transaction.TransactionViewModel
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onDismissRequest: () -> Unit,
    startWithIncome: Boolean = false,
    onOpenAutoSave: () -> Unit = {},
    transactionViewModel: TransactionViewModel = viewModel(),
    onTransactionCreated: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by transactionViewModel.state.collectAsState()

    var isExpense by rememberSaveable { mutableStateOf(!startWithIncome) }
    // Mọi nút trên màn này theo loại giao dịch: chi màu cam, thu màu xanh lá
    val flowColor = if (isExpense) MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn
    val flowSoftColor = if (isExpense) MoneyFlowTheme.colors.surfaceOutSoft else MoneyFlowTheme.colors.surfaceInSoft
    // ViewModel sống theo Activity nên còn giữ loại thu/chi của lần mở trước —
    // đồng bộ lại mỗi lần mở để giao diện và dữ liệu gửi lên luôn khớp nhau
    LaunchedEffect(Unit) { transactionViewModel.setExpense(isExpense) }
    var autoSaveEnabled by rememberSaveable { mutableStateOf(true) }
    var autoSavePercent by rememberSaveable { mutableIntStateOf(20) }
    var expression by rememberSaveable { mutableStateOf("") }
    var noteText by rememberSaveable { mutableStateOf("") }
    var nameText by rememberSaveable { mutableStateOf("") }
    var showCalculator by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showCategoryPicker by rememberSaveable { mutableStateOf(false) }
    var showWalletPicker by rememberSaveable { mutableStateOf(false) }
    var showNoteDialog by rememberSaveable { mutableStateOf(false) }
    var selectedDateTime by rememberSaveable { mutableStateOf(TransactionTime.now()) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale("vi", "VN")) }

    // Evaluator
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
                .navigationBarsPadding()
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
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = flowColor)
                    }
                    Text(
                        text = "Thêm giao dịch",
                        style = MoneyFlowTheme.typography.title2,
                        color = MoneyFlowTheme.colors.textPrimary,
                        fontFamily = FontCore
                    )
                    Spacer(modifier = Modifier.size(48.dp))
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
                            .clickable {
                                isExpense = true
                                transactionViewModel.setExpense(true)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Chi tiêu",
                            color = if (isExpense) Color.White else MoneyFlowTheme.colors.textSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
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
                            .clickable {
                                isExpense = false
                                transactionViewModel.setExpense(false)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Thu nhập",
                            color = if (!isExpense) Color.White else MoneyFlowTheme.colors.textSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            fontFamily = FontCore
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Amount Display Area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCalculator = !showCalculator }
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (showCalculator && expression.isNotEmpty()) expression else "Chạm để nhập số tiền",
                        style = MoneyFlowTheme.typography.caption,
                        color = MoneyFlowTheme.colors.textSecondary,
                        fontFamily = FontCore
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = formattedCalculated,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isExpense) MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn,
                            fontFamily = FontMono
                        )
                        Text(
                            text = " ₫",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isExpense) MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn,
                            modifier = Modifier.padding(bottom = 6.dp),
                            fontFamily = FontCore
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .height(2.dp)
                            .background(if (isExpense) MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tên giao dịch (khớp cột transactions.merchant VARCHAR(150)) — ngay dưới số tiền
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { if (it.length <= 150) nameText = it },
                    placeholder = {
                        Text(if (isExpense) "Tên khoản chi (VD: Cà phê Highlands)" else "Tên khoản thu (VD: Lương tháng 8)")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = flowColor,
                        unfocusedBorderColor = flowColor,
                        cursorColor = flowColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transactionName")
                        .padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Selectors & Quick Chips
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (uiState.isLoading) {
                        // Skeleton loading for selectors
                        SkeletonBox(modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp))
                        SkeletonBox(modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp))
                    } else {
                        val currentCategoryName = uiState.selectedCategory?.name ?: "Chọn danh mục"
                        val currentWalletName = uiState.selectedWallet?.name ?: "Chọn ví"

                        if (isExpense) {
                            SelectorButton(
                                icon = getCategoryIcon(uiState.selectedCategory?.name),
                                label = "Danh mục · ",
                                value = currentCategoryName,
                                valueBold = true,
                                brandColor = MoneyFlowTheme.colors.flowOut,
                                brandSoftColor = MoneyFlowTheme.colors.surfaceOutSoft,
                                onClick = { showCategoryPicker = true }
                            )

                            // Quick expense chips
                            val expenseCats = uiState.categories.filter { it.flow == "EXPENSE" }
                            if (expenseCats.isNotEmpty()) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(expenseCats.take(5)) { cat ->
                                        val isSelected = uiState.selectedCategory?.id == cat.id
                                        Box(
                                            modifier = Modifier
                                                .height(36.dp)
                                                .clip(RoundedCornerShape(18.dp))
                                                .background(if (isSelected) MoneyFlowTheme.colors.surfaceOutSoft else Color.White)
                                                .border(1.dp, if (isSelected) MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.borderSubtle, RoundedCornerShape(18.dp))
                                                .clickable { transactionViewModel.selectCategory(cat) }
                                                .padding(horizontal = 16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = cat.name,
                                                color = if (isSelected) MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.textSecondary,
                                                style = MoneyFlowTheme.typography.caption,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }

                            SelectorButton(
                                icon = Icons.Outlined.AccountBalanceWallet,
                                label = "Nguồn tiền · ",
                                value = currentWalletName,
                                valueBold = false,
                                brandColor = MoneyFlowTheme.colors.flowOut,
                                brandSoftColor = MoneyFlowTheme.colors.surfaceOutSoft,
                                onClick = { showWalletPicker = true }
                            )
                        } else {
                            SelectorButton(
                                icon = getCategoryIcon(uiState.selectedCategory?.name),
                                label = "Nguồn thu · ",
                                value = currentCategoryName,
                                valueBold = true,
                                brandColor = MoneyFlowTheme.colors.flowIn,
                                brandSoftColor = MoneyFlowTheme.colors.surfaceInSoft,
                                onClick = { showCategoryPicker = true }
                            )

                            // Quick Income Chips
                            val incomeCats = uiState.categories.filter { it.flow == "INCOME" }
                            if (incomeCats.isNotEmpty()) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(incomeCats) { chip ->
                                        val isSelected = uiState.selectedCategory?.id == chip.id
                                        Box(
                                            modifier = Modifier
                                                .height(36.dp)
                                                .clip(RoundedCornerShape(18.dp))
                                                .background(if (isSelected) MoneyFlowTheme.colors.surfaceInSoft else Color.White)
                                                .border(1.dp, if (isSelected) MoneyFlowTheme.colors.flowIn else MoneyFlowTheme.colors.borderSubtle, RoundedCornerShape(18.dp))
                                                .clickable { transactionViewModel.selectCategory(chip) }
                                                .padding(horizontal = 16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = chip.name,
                                                color = if (isSelected) MoneyFlowTheme.colors.flowIn else MoneyFlowTheme.colors.textSecondary,
                                                style = MoneyFlowTheme.typography.caption,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }

                            SelectorButton(
                                icon = Icons.Outlined.AccountBalanceWallet,
                                label = "Vào ví · ",
                                value = currentWalletName,
                                valueBold = false,
                                brandColor = MoneyFlowTheme.colors.flowIn,
                                brandSoftColor = MoneyFlowTheme.colors.surfaceInSoft,
                                onClick = { showWalletPicker = true }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date & Note Action Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Date Action
                    Row(
                        modifier = Modifier
                            .weight(1.2f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, flowSoftColor, RoundedCornerShape(12.dp))
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.CalendarToday, contentDescription = "Chọn ngày giờ giao dịch", tint = flowColor, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = selectedDateTime.format(dateFormatter), style = MoneyFlowTheme.typography.caption, color = flowColor, fontFamily = FontCore)
                    }

                    // Note Action
                    Row(
                        modifier = Modifier
                            .weight(1.5f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (noteText.isBlank()) Color.White else flowSoftColor)
                            .border(1.dp, flowSoftColor, RoundedCornerShape(12.dp))
                            .clickable { showNoteDialog = true }
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = if (isExpense) MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (noteText.isBlank()) "Ghi chú" else noteText,
                            style = MoneyFlowTheme.typography.caption,
                            color = if (isExpense) MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn,
                            fontFamily = FontCore,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
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

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.errorMessage!!,
                        color = Color.Red,
                        style = MoneyFlowTheme.typography.caption,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Primary Save Button
                Button(
                    onClick = {
                        val amountLong = calculatedAmount.toLong()
                        transactionViewModel.submitTransaction(
                            amount = amountLong,
                            dateTime = selectedDateTime,
                            note = noteText,
                            name = nameText,
                            onSuccess = {
                                onTransactionCreated()
                                onDismissRequest()
                            }
                        )
                    },
                    enabled = !uiState.isSubmitting && calculatedAmount > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isExpense) MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = if (isExpense) "Lưu khoản chi" else "Lưu khoản thu",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            fontFamily = FontCore
                        )
                    }
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
                                showCalculator = false
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
        com.quan.finance_app.ui.components.forms.TransactionDateTimePickerSheet(
            initial = selectedDateTime,
            onDismissRequest = { showDatePicker = false },
            onSelected = { selectedDateTime = it },
            brandColor = if (isExpense) MoneyFlowTheme.colors.flowOut else MoneyFlowTheme.colors.flowIn,
            brandSoftColor = if (isExpense) MoneyFlowTheme.colors.surfaceOutSoft else MoneyFlowTheme.colors.surfaceInSoft
        )
    }

    if (showCategoryPicker) {
        val flowStr = if (isExpense) "EXPENSE" else "INCOME"
        val filteredCats = uiState.categories.filter { it.flow == flowStr }
        CategoryPickerSheet(
            onDismissRequest = { showCategoryPicker = false },
            categories = filteredCats,
            isLoading = uiState.isLoading,
            currentFlow = flowStr,
            onCategorySelected = { cat ->
                transactionViewModel.selectCategory(cat)
                showCategoryPicker = false
            },
            onAddCategory = { name, flow ->
                transactionViewModel.createCategory(name, flow) { createdCat ->
                    transactionViewModel.selectCategory(createdCat)
                }
            }
        )
    }

    if (showWalletPicker) {
        WalletPickerSheet(
            onDismissRequest = { showWalletPicker = false },
            wallets = uiState.wallets,
            selectedWallet = uiState.selectedWallet,
            isLoading = uiState.isLoading,
            onWalletSelected = { wallet ->
                transactionViewModel.selectWallet(wallet)
                showWalletPicker = false
            },
            onAddWallet = { name, kind, initialBalance ->
                transactionViewModel.createWallet(name, kind, initialBalance) { newWallet ->
                    transactionViewModel.selectWallet(newWallet)
                }
            },
            brandColor = flowColor
        )
    }

    if (showNoteDialog) {
        NoteSheet(
            isExpense = isExpense,
            initialNote = noteText,
            flowColor = flowColor,
            flowSoftColor = flowSoftColor,
            onDismissRequest = { showNoteDialog = false },
            onSave = { note ->
                noteText = note
                showNoteDialog = false
            }
        )
    }
}

// Khớp với cột transactions.note VARCHAR(500) ở ledger-service
private const val NOTE_MAX_LENGTH = 500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteSheet(
    isExpense: Boolean,
    initialNote: String,
    flowColor: Color,
    flowSoftColor: Color,
    onDismissRequest: () -> Unit,
    onSave: (String) -> Unit
) {
    // Chỉ ghi vào giao dịch khi bấm Lưu, nên Huỷ giữ nguyên ghi chú cũ
    var draft by remember { mutableStateOf(TextFieldValue(initialNote, TextRange(initialNote.length))) }
    val focusRequester = remember { FocusRequester() }
    val suggestions = if (isExpense) {
        listOf("Ăn trưa", "Cà phê", "Đổ xăng", "Đi chợ", "Trả nợ")
    } else {
        listOf("Lương tháng", "Thưởng", "Bán hàng", "Hoàn tiền", "Được tặng")
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MoneyFlowTheme.colors.surfaceCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isExpense) "Ghi chú khoản chi" else "Ghi chú khoản thu",
                    style = MoneyFlowTheme.typography.title2,
                    color = MoneyFlowTheme.colors.textPrimary,
                    fontFamily = FontCore,
                    modifier = Modifier.weight(1f)
                )
                if (draft.text.isNotEmpty()) {
                    Text(
                        text = "Xoá",
                        style = MoneyFlowTheme.typography.bodySm,
                        fontWeight = FontWeight.SemiBold,
                        color = flowColor,
                        fontFamily = FontCore,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { draft = TextFieldValue("") }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = draft,
                onValueChange = { if (it.text.length <= NOTE_MAX_LENGTH) draft = it },
                placeholder = {
                    Text(if (isExpense) "Khoản chi này dùng cho việc gì?" else "Khoản thu này đến từ đâu?")
                },
                minLines = 3,
                maxLines = 5,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = flowColor,
                    cursorColor = flowColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${draft.text.length}/$NOTE_MAX_LENGTH",
                style = MoneyFlowTheme.typography.caption,
                color = MoneyFlowTheme.colors.textTertiary,
                fontFamily = FontMono,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(suggestions) { suggestion ->
                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(flowSoftColor)
                            .clickable {
                                val current = draft.text.trimEnd()
                                val next = (if (current.isEmpty()) suggestion else "$current, $suggestion")
                                    .take(NOTE_MAX_LENGTH)
                                draft = TextFieldValue(next, TextRange(next.length))
                            }
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = suggestion,
                            style = MoneyFlowTheme.typography.caption,
                            fontWeight = FontWeight.Medium,
                            color = flowColor,
                            fontFamily = FontCore
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderSubtle)
                ) {
                    Text("Huỷ", color = MoneyFlowTheme.colors.textSecondary, fontFamily = FontCore)
                }
                Button(
                    onClick = { onSave(draft.text.trim()) },
                    modifier = Modifier
                        .weight(2f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = flowColor)
                ) {
                    Text("Lưu ghi chú", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontCore)
                }
            }
        }
    }
}

@Composable
fun SelectorButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueBold: Boolean,
    brandColor: Color,
    brandSoftColor: Color,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, brandColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
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
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = brandColor)
    }
}

fun evaluateExpression(expr: String): Double {
    if (expr.isEmpty()) return 0.0
    val cleanExpr = expr.replace("×", "*").replace("÷", "/").replace(".", "")
    return try {
        var currentNumber = ""
        var currentResult = 0.0
        var currentOp = '+'

        for (i in cleanExpr.indices) {
            val char = cleanExpr[i]
            if (char.isDigit() || char == ',') {
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
