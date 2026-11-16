package com.quan.finance_app.ui.components.forms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quan.finance_app.ui.theme.MoneyFlowTheme
import com.quan.finance_app.ui.theme.FontCore
import java.time.LocalDate
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
    initialDate: LocalDate = LocalDate.now()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var currentMonth by remember { mutableStateOf(YearMonth.from(initialDate)) }
    var selectedDate by remember { mutableStateOf(initialDate) }
    val formatter = DateTimeFormatter.ofPattern("M, yyyy", Locale("vi", "VN"))

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
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
                .wrapContentHeight()
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
                    text = "Chọn ngày & giờ",
                    style = MoneyFlowTheme.typography.title2,
                    color = MoneyFlowTheme.colors.textPrimary,
                    fontFamily = FontCore
                )
                IconButton(onClick = onDismissRequest) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = MoneyFlowTheme.colors.textSecondary)
                }
            }

            // Quick Chips
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(brandColor)
                        .clickable { selectedDate = LocalDate.now() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Hôm nay",
                        color = Color.White,
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
                    Icon(Icons.Outlined.ChevronLeft, contentDescription = "Previous Month", tint = MoneyFlowTheme.colors.textSecondary)
                }
                Text(
                    text = "Tháng ${currentMonth.format(formatter)}",
                    style = MoneyFlowTheme.typography.body.copy(fontWeight = FontWeight.Bold),
                    color = MoneyFlowTheme.colors.textPrimary,
                    fontFamily = FontCore
                )
                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(Icons.Outlined.ChevronRight, contentDescription = "Next Month", tint = MoneyFlowTheme.colors.textSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calendar Grid
            CalendarGrid(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it },
                brandColor = brandColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Time Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(1.dp, MoneyFlowTheme.colors.borderSubtle, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(brandSoftColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = brandColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Giờ",
                    style = MoneyFlowTheme.typography.body,
                    color = MoneyFlowTheme.colors.textSecondary,
                    fontFamily = FontCore
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "09:12",
                    style = MoneyFlowTheme.typography.body.copy(fontWeight = FontWeight.Bold),
                    color = MoneyFlowTheme.colors.textPrimary,
                    fontFamily = FontCore
                )
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
                        onDateSelected(selectedDate)
                        onDismissRequest()
                    },
                    modifier = Modifier
                        .weight(2f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = brandColor),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    val dateFormatted = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM", Locale("vi", "VN")))
                    Text(
                        text = "Chọn $dateFormatted - 09:12",
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
fun CalendarGrid(
    currentMonth: YearMonth,
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
                        val isFuture = date.isAfter(LocalDate.now())
                        val hasEvent = dayNum % 5 == 0 && !isFuture // Mock event dot for visualization
                        
                        val baseModifier = Modifier
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
