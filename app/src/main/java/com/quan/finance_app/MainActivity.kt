package com.quan.finance_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quan.finance_app.ui.auth.AuthViewModel
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

/**
 * Các màn con mở chồng lên tab hiện tại. Tab nào cũng dùng chung một back stack,
 * nên "Cài đặt → Danh mục → Sửa hạn mức" và "Trang chủ → Tất cả giao dịch → Chi tiết"
 * đều lùi lại được bằng nút back.
 */
sealed interface Route {
    data object Categories : Route
    data class EditLimit(val category: String) : Route
    data class EditIncomeCategory(val category: String) : Route
    data object AutoSave : Route
    data object SavingsGoals : Route
    data object EditGoal : Route
    data object AllTransactions : Route
    data class TransactionDetail(val title: String) : Route
    data object Wallets : Route
    data object Recurring : Route
    data object Export : Route
    data object Notifications : Route
    data object NotificationSettings : Route
    data object Account : Route
    data object Security : Route
    data object Currency : Route
    data object Backup : Route
    data object AddWallet : Route
    data object AdjustBalance : Route
}

/** Hai man truoc khi dang nhap, nam ngoai khung tab. */
enum class AuthScreen { LOGIN, VERIFY_EMAIL, REGISTER, REGISTER_2, REGISTER_3 }

@Composable
fun MainApp(authViewModel: AuthViewModel = viewModel()) {
    val auth by authViewModel.state.collectAsState()
    val context = LocalContext.current

    var isLoggedIn by remember { mutableStateOf(false) }
    var authScreen by remember { mutableStateOf(AuthScreen.LOGIN) }

    // Gọi API xong thì mới điều hướng. consumeNavigation() tắt cờ để không chạy lại.
    LaunchedEffect(auth.loginSuccess, auth.registerSuccess) {
        when {
            auth.loginSuccess -> { isLoggedIn = true; authViewModel.consumeNavigation() }
            auth.registerSuccess -> { authScreen = AuthScreen.REGISTER_2; authViewModel.consumeNavigation() }
        }
    }
    var selectedTab by remember { mutableIntStateOf(0) }
    val backStack = remember { mutableStateListOf<Route>() }

    // Lớp phủ toàn màn — không nằm trong back stack vì chúng đóng bằng nút riêng
    var showAddSheet by remember { mutableStateOf(false) }
    var showScanReceipt by remember { mutableStateOf(false) }
    var showGoalDeposit by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showDeleteData by remember { mutableStateOf(false) }

    fun push(route: Route) = backStack.add(route)
    fun pop() {
        if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex)
    }

    if (!isLoggedIn) {
        BackHandler(enabled = authScreen != AuthScreen.LOGIN) {
            authScreen = when (authScreen) {
                AuthScreen.REGISTER_3 -> AuthScreen.REGISTER_2
                AuthScreen.REGISTER_2 -> AuthScreen.REGISTER
                else -> AuthScreen.LOGIN
            }
        }
        when (authScreen) {
            AuthScreen.LOGIN -> LoginScreen(
                onLogin = { email, password -> authViewModel.login(email, password) },
                onGoogleLogin = { authViewModel.onGoogleSignInClicked(context) },
                onGoToRegister = { authViewModel.clearErrors(); authScreen = AuthScreen.REGISTER },
                onForgotPassword = { authScreen = AuthScreen.VERIFY_EMAIL },
                loading = auth.loading,
                errorMessage = auth.errorMessage,
                fieldErrors = auth.fieldErrors
            )
            AuthScreen.VERIFY_EMAIL -> VerifyEmailScreen(
                onBack = { authScreen = AuthScreen.LOGIN },
                onVerified = { isLoggedIn = true }
            )
            AuthScreen.REGISTER -> RegisterScreen(
                onBack = { authViewModel.clearErrors(); authScreen = AuthScreen.LOGIN },
                onContinue = { fullName, email, password ->
                    authViewModel.register(email, password, fullName)
                },
                onGoogleLogin = { authViewModel.onGoogleSignInClicked(context) },
                onGoToLogin = { authViewModel.clearErrors(); authScreen = AuthScreen.LOGIN },
                loading = auth.loading,
                errorMessage = auth.errorMessage,
                fieldErrors = auth.fieldErrors
            )
            AuthScreen.REGISTER_2 -> RegisterStep2Screen(
                onBack = { authScreen = AuthScreen.REGISTER },
                onContinue = { authScreen = AuthScreen.REGISTER_3 },
                onSkip = { isLoggedIn = true }
            )
            AuthScreen.REGISTER_3 -> RegisterStep3Screen(
                onBack = { authScreen = AuthScreen.REGISTER_2 },
                onFinish = { isLoggedIn = true },
                onSkip = { isLoggedIn = true }
            )
        }
        return
    }

    // Nút back của máy: đóng lớp phủ trước, sau đó mới lùi back stack
    BackHandler(
        enabled = backStack.isNotEmpty() || showAddSheet || showScanReceipt ||
            showGoalDeposit || showCategoryPicker || showDeleteData
    ) {
        when {
            showDeleteData -> showDeleteData = false
            showCategoryPicker -> showCategoryPicker = false
            showGoalDeposit -> showGoalDeposit = false
            showScanReceipt -> showScanReceipt = false
            showAddSheet -> showAddSheet = false
            else -> pop()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MoneyFlowTheme.colors.surfacePage,
            bottomBar = {
                CustomBottomNav(
                    selectedTab = selectedTab,
                    onTabSelected = {
                        selectedTab = it
                        backStack.clear()
                    },
                    onAddClick = { showAddSheet = true }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                val route = backStack.lastOrNull()
                if (route == null) {
                    when (selectedTab) {
                        0 -> HomeScreen(
                            onSeeAllTransactions = { push(Route.AllTransactions) },
                            onScanBill = { showScanReceipt = true },
                            onAddExpense = { showAddSheet = true },
                            onAddIncome = { showAddSheet = true },
                            onClassifyTransaction = { showCategoryPicker = true },
                            onOpenTransaction = { push(Route.TransactionDetail(it)) },
                            onOpenGoal = { push(Route.SavingsGoals) },
                            onOpenNotifications = { push(Route.Notifications) }
                        )
                        1 -> ReportScreen()
                        2 -> BudgetScreen(
                            onNavigateToSavingsGoals = { push(Route.SavingsGoals) }
                        )
                        3 -> ProfileScreen(
                            onNavigateToCategories = { push(Route.Categories) },
                            onNavigateToSavingsGoals = { push(Route.SavingsGoals) },
                            onNavigateToWallets = { push(Route.Wallets) },
                            onNavigateToRecurring = { push(Route.Recurring) },
                            onNavigateToExport = { push(Route.Export) },
                            onNavigateToAccount = { push(Route.Account) },
                            onNavigateToCurrency = { push(Route.Currency) },
                            onNavigateToBackup = { push(Route.Backup) },
                            onDeleteAllData = { showDeleteData = true },
                            onNavigateToAutoSave = { push(Route.AutoSave) },
                            onNavigateToNotificationSettings = { push(Route.NotificationSettings) }
                        )
                    }
                } else {
                    when (route) {
                        Route.Categories -> CategoryLimitScreen(
                            onBack = { pop() },
                            onEditLimit = { push(Route.EditLimit(it)) },
                            onEditIncomeCategory = { push(Route.EditIncomeCategory(it)) },
                            onViewIncomeReport = {
                                backStack.clear()
                                selectedTab = 1
                            }
                        )
                        is Route.EditLimit -> EditBudgetScreen(
                            categoryName = route.category,
                            onBack = { pop() },
                            onAddTransaction = { showAddSheet = true }
                        )
                        is Route.EditIncomeCategory -> EditIncomeCategoryScreen(
                            categoryName = route.category,
                            onBack = { pop() },
                            onAddTransaction = { showAddSheet = true },
                            onOpenAutoSave = { push(Route.AutoSave) }
                        )
                        Route.AutoSave -> AutoSaveSettingScreen(onBack = { pop() })
                        Route.SavingsGoals -> SavingsGoalsScreen(
                            onBack = { pop() },
                            onNavigateToEditGoal = { push(Route.EditGoal) },
                            onDeposit = { showGoalDeposit = true }
                        )
                        Route.EditGoal -> EditGoalScreen(onBack = { pop() })
                        Route.AllTransactions -> AllTransactionsScreen(
                            onBack = { pop() },
                            onOpenTransaction = { push(Route.TransactionDetail(it)) }
                        )
                        is Route.TransactionDetail -> TransactionDetailScreen(
                            title = route.title,
                            onBack = { pop() },
                            onEdit = { showAddSheet = true }
                        )
                        Route.Wallets -> WalletsScreen(
                            onBack = { pop() },
                            onAddWallet = { push(Route.AddWallet) },
                            onAdjustBalance = { push(Route.AdjustBalance) }
                        )
                        Route.Recurring -> RecurringTransactionsScreen(onBack = { pop() })
                        Route.Export -> ExportDataScreen(onBack = { pop() })
                        Route.Notifications -> NotificationsScreen(
                            onBack = { pop() },
                            onOpenSettings = { push(Route.NotificationSettings) }
                        )
                        Route.NotificationSettings -> NotificationSettingsScreen(onBack = { pop() })
                        Route.Security -> SecurityScreen(onBack = { pop() })
                        Route.Currency -> CurrencySettingsScreen(onBack = { pop() })
                        Route.Backup -> BackupSyncScreen(onBack = { pop() })
                        Route.AddWallet -> AddWalletScreen(onBack = { pop() })
                        Route.AdjustBalance -> AdjustBalanceScreen(onBack = { pop() })
                        Route.Account -> AccountScreen(
                            onBack = { pop() },
                            onOpenSecurity = { push(Route.Security) },
                            onLogout = {
                                backStack.clear()
                                selectedTab = 0
                                authScreen = AuthScreen.LOGIN
                                isLoggedIn = false
                            }
                        )
                    }
                }
            }
        }

        if (showAddSheet) {
            AddTransactionScreen(
                onDismissRequest = { showAddSheet = false },
                onOpenAutoSave = {
                    showAddSheet = false
                    push(Route.AutoSave)
                }
            )
        }
        if (showScanReceipt) {
            ScanReceiptScreen(onDismissRequest = { showScanReceipt = false })
        }
        if (showGoalDeposit) {
            GoalDepositSheet(onDismissRequest = { showGoalDeposit = false })
        }
        if (showDeleteData) {
            DeleteDataDialog(
                onDismissRequest = { showDeleteData = false },
                onConfirm = { showDeleteData = false }
            )
        }
        if (showCategoryPicker) {
            CategoryPickerSheet(
                onDismissRequest = { showCategoryPicker = false },
                onCategorySelected = { showCategoryPicker = false }
            )
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
