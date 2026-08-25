package com.quan.finance_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.quan.finance_app.ui.screens.*
import com.quan.finance_app.ui.theme.MoneyFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoneyFlowTheme {
                MainApp()
            }
        }
    }
}

enum class BudgetNavState {
    MAIN,
    SAVINGS_GOALS,
    EDIT_GOAL
}

@Composable
fun MainApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddSheet by remember { mutableStateOf(false) }
    var budgetNavState by remember { mutableStateOf(BudgetNavState.MAIN) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MoneyFlowTheme.colors.surfacePage,
            bottomBar = {
                CustomBottomNav(
                    selectedTab = selectedTab,
                    onTabSelected = { 
                        selectedTab = it
                        if (it != 2) {
                            budgetNavState = BudgetNavState.MAIN
                        }
                    },
                    onAddClick = { showAddSheet = true }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                when (selectedTab) {
                    0 -> HomeScreen()
                    1 -> ReportScreen()
                    2 -> {
                        when (budgetNavState) {
                            BudgetNavState.MAIN -> BudgetScreen(
                                onNavigateToSavingsGoals = { budgetNavState = BudgetNavState.SAVINGS_GOALS }
                            )
                            BudgetNavState.SAVINGS_GOALS -> SavingsGoalsScreen(
                                onBack = { budgetNavState = BudgetNavState.MAIN },
                                onNavigateToEditGoal = { budgetNavState = BudgetNavState.EDIT_GOAL }
                            )
                            BudgetNavState.EDIT_GOAL -> EditGoalScreen(
                                onBack = { budgetNavState = BudgetNavState.SAVINGS_GOALS }
                            )
                        }
                    }
                    3 -> ProfileScreen()
                }
            }
        }

        if (showAddSheet) {
            AddTransactionScreen(onDismissRequest = { showAddSheet = false })
        }
    }
}

@Composable
fun CustomBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onAddClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(80.dp),
        color = MoneyFlowTheme.colors.surfaceCard,
        shadowElevation = 16.dp,
        tonalElevation = 0.dp,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Outlined.Home,
                label = "Trang chủ",
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                modifier = Modifier.weight(1f)
            )
            NavItem(
                icon = Icons.Outlined.BarChart,
                label = "Thống kê",
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier.weight(1f)
            )
            
            // Nút Add
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = MoneyFlowTheme.colors.surfaceBrand,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp).border(4.dp, Color.White, CircleShape),
                    elevation = FloatingActionButtonDefaults.elevation(4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm giao dịch", modifier = Modifier.size(28.dp))
                }
            }
            
            NavItem(
                icon = Icons.Outlined.AccountBalanceWallet,
                label = "Ngân sách",
                isSelected = selectedTab == 2,
                onClick = { onTabSelected(2) },
                modifier = Modifier.weight(1f)
            )
            NavItem(
                icon = Icons.Outlined.Settings,
                label = "Cài đặt",
                isSelected = selectedTab == 3,
                onClick = { onTabSelected(3) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun NavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = if (isSelected) MoneyFlowTheme.colors.textBrand else MoneyFlowTheme.colors.textSecondary
    
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label, 
            style = MoneyFlowTheme.typography.caption.copy(
                fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
            ), 
            color = color
        )
    }
}