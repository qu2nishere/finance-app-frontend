package com.quan.finance_app.ui.components.forms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.theme.FontCore
import com.quan.finance_app.ui.theme.MoneyFlowTheme
import com.quan.finance_app.ui.transaction.TransactionTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerSheet(
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    brandColor: Color,
    brandSoftColor: Color,
    modifier: Modifier = Modifier,
    initialDate: LocalDate = TransactionTime.now().toLocalDate()
) {
    CalendarSheet(initialDate, null, brandColor, brandSoftColor, modifier, onDismissRequest) { date, _ ->
        onDateSelected(date)
    }
}

@Composable
fun TransactionDateTimePickerSheet(
    initial: LocalDateTime,
    onDismissRequest: () -> Unit,
    onSelected: (LocalDateTime) -> Unit,
    brandColor: Color,
    brandSoftColor: Color
) {
    CalendarSheet(initial.toLocalDate(), initial.toLocalTime(), brandColor, brandSoftColor,
        Modifier, onDismissRequest) { date, time -> onSelected(date.atTime(requireNotNull(time))) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarSheet(
    initialDate: LocalDate,
    initialTime: LocalTime?,
    brandColor: Color,
    brandSoftColor: Color,
    modifier: Modifier,
    onDismissRequest: () -> Unit,
    onSelected: (LocalDate, LocalTime?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var currentMonth by rememberSaveable { mutableStateOf(YearMonth.from(initialDate)) }
    var selectedDate by rememberSaveable { mutableStateOf(initialDate) }
    val timed = initialTime != null
    val today = TransactionTime.now().toLocalDate()
    var hour by rememberSaveable { mutableStateOf(initialTime?.let { "%02d".format(Locale.ROOT, it.hour) }.orEmpty()) }
    var minute by rememberSaveable { mutableStateOf(initialTime?.let { "%02d".format(Locale.ROOT, it.minute) }.orEmpty()) }
    val selectedHour = hour.toIntOrNull()
    val selectedMinute = minute.toIntOrNull()
    val time = if (selectedHour != null && selectedHour in 0..23 &&
        selectedMinute != null && selectedMinute in 0..59
    ) LocalTime.of(selectedHour, selectedMinute) else null
    var futureError by rememberSaveable(selectedDate, hour, minute) { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("M, yyyy", Locale("vi", "VN"))

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = MoneyFlowTheme.colors.surfaceCard,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFCBD5E1))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (timed) "Chọn ngày và giờ" else "Chọn ngày",
                    style = MoneyFlowTheme.typography.title2,
                    color = MoneyFlowTheme.colors.textPrimary,
                    fontFamily = FontCore
                )
                IconButton(onClick = onDismissRequest) {
                    Icon(Icons.Default.Close, contentDescription = "Đóng", tint = MoneyFlowTheme.colors.textSecondary)
                }
            }

            // Quick Chips
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(brandSoftColor)
                        .clickable {
                            selectedDate = today
                            currentMonth = YearMonth.from(today)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Hôm nay",
                        color = brandColor,
                        style = MoneyFlowTheme.typography.caption,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Calendar Header (Month/Year)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(Icons.Outlined.ChevronLeft, contentDescription = "Tháng trước", tint = MoneyFlowTheme.colors.textSecondary)
                }
                Text(
                    text = "Tháng ${currentMonth.format(formatter)}",
                    style = MoneyFlowTheme.typography.body.copy(fontWeight = FontWeight.Bold),
                    color = MoneyFlowTheme.colors.textPrimary,
                    fontFamily = FontCore
                )
                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(Icons.Outlined.ChevronRight, contentDescription = "Tháng sau", tint = MoneyFlowTheme.colors.textSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calendar Grid
            CalendarGrid(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it },
                latestDate = if (timed) null else today,
                brandColor = brandColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (timed) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TimeField("Giờ", hour, { hour = it }, Modifier.weight(1f))
                    TimeField("Phút", minute, { minute = it }, Modifier.weight(1f))
                }
                val error = when {
                    time == null -> "Nhập giờ từ 00 đến 23 và phút từ 00 đến 59."
                    futureError -> "Thời điểm giao dịch phải ở hiện tại hoặc quá khứ."
                    else -> null
                }
                if (error != null) Text(error, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bottom Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MoneyFlowTheme.colors.borderSubtle)
                ) {
                    Text(
                        text = "Huỷ",
                        color = MoneyFlowTheme.colors.textSecondary,
                        style = MoneyFlowTheme.typography.body,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = { 
                        if (time != null && selectedDate.atTime(time) > TransactionTime.now()) {
                            futureError = true
                        } else {
                            onSelected(selectedDate, time)
                            onDismissRequest()
                        }
                    },
                    modifier = Modifier
                        .weight(2f)
                        .height(56.dp),
                    enabled = !timed || time != null,
                    colors = ButtonDefaults.buttonColors(containerColor = brandColor),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    val dateFormatted = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM", Locale("vi", "VN")))
                    Text(
                        text = if (time == null) "Chọn $dateFormatted" else "Chọn $dateFormatted · $time",
                        color = Color.White,
                        style = MoneyFlowTheme.typography.body,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeField(label: String, value: String, onChange: (String) -> Unit, modifier: Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.length <= 2 && it.all { digit -> digit in '0'..'9' }) onChange(it) },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = modifier
    )
}

@Composable
private fun CalendarGrid(
    currentMonth: YearMonth,
    latestDate: LocalDate?,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    brandColor: Color
) {
    val daysOfWeek = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
    
    // Calculate calendar days
    val firstDayOfMonth = currentMonth.atDay(1)
    val daysInMonth = currentMonth.lengthOfMonth()
    
    // Day of week of first day (1 = Monday, 7 = Sunday)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
    
    // Number of empty cells before the 1st
    val emptyDaysBefore = firstDayOfWeek - 1
    
    val totalCells = 42 // Always render 6 rows (42 days) to keep height fixed
    
    Column(modifier = Modifier.fillMaxWidth()) {
        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MoneyFlowTheme.typography.caption,
                    color = MoneyFlowTheme.colors.textSecondary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Calendar Grid
        val chunkedDays = (0 until totalCells).chunked(7)
        chunkedDays.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { cellIndex ->
                    val dayNum = cellIndex - emptyDaysBefore + 1
                    val isCurrentMonth = dayNum in 1..daysInMonth
                    
                    if (isCurrentMonth) {
                        val date = currentMonth.atDay(dayNum)
                        val isSelected = date == selectedDate
                        val isFuture = latestDate != null && date.isAfter(latestDate)
                        val hasEvent = dayNum % 5 == 0 && !isFuture // Mock event dot for visualization
                        
                        val baseModifier = Modifier.testTag("calendar-day-$date")
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) brandColor else Color.Transparent)
                            
                        Box(
                            modifier = if (isFuture) baseModifier else baseModifier.clickable { onDateSelected(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = dayNum.toString(),
                                    color = if (isSelected) Color.White 
                                            else if (isFuture) MoneyFlowTheme.colors.borderSubtle
                                            else MoneyFlowTheme.colors.textPrimary,
                                    fontSize = 16.sp,
                                    fontFamily = FontCore
                                )
                                if (hasEvent && !isSelected) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .background(brandColor.copy(alpha = 0.5f), CircleShape)
                                    )
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }
    }
}
