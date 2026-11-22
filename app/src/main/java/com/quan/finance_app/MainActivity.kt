package com.quan.finance_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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

import com.quan.finance_app.ui.budget.BudgetViewModel
import com.quan.finance_app.ui.home.HomeViewModel
import com.quan.finance_app.ui.report.ReportViewModel

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
fun MainApp(
    authViewModel: AuthViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel(),
    reportViewModel: ReportViewModel = viewModel(),
    budgetViewModel: BudgetViewModel = viewModel()
) {
    val auth by authViewModel.state.collectAsState()
    val context = LocalContext.current

    var isLoggedIn by remember { mutableStateOf(false) }
    var authScreen by remember { mutableStateOf(AuthScreen.LOGIN) }

    // Gọi API xong thì mới điều hướng. consumeNavigation() tắt cờ để không chạy lại.
    LaunchedEffect(auth.loginSuccess, auth.registerSuccess) {
        when {
            auth.loginSuccess -> {
                isLoggedIn = true
                authViewModel.consumeNavigation()
                homeViewModel.loadData()
                budgetViewModel.load()
            }
            auth.registerSuccess -> { authScreen = AuthScreen.REGISTER_2; authViewModel.consumeNavigation() }
        }
    }
    var selectedTab by remember { mutableIntStateOf(0) }
    val backStack = remember { mutableStateListOf<Route>() }

    // Lớp phủ toàn màn — không nằm trong back stack vì chúng đóng bằng nút riêng
    var showAddSheet by remember { mutableStateOf(false) }
    // Mở màn thêm giao dịch ở sẵn tab Thu nhập (nút "+ Thu", sửa nguồn thu)
    var addSheetStartsWithIncome by remember { mutableStateOf(false) }
    // Tự trích mở từ màn thêm giao dịch: phủ lên trên chứ không đóng màn đó,
    // để back quay lại đúng màn thu với số tiền/danh mục đang nhập dở
    var showAutoSaveOverAdd by remember { mutableStateOf(false) }
    var showScanReceipt by remember { mutableStateOf(false) }
    var showGoalDeposit by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showDeleteData by remember { mutableStateOf(false) }

    fun push(route: Route) = backStack.add(route)
    fun openAddSheet(income: Boolean = false) {
        addSheetStartsWithIncome = income
        showAddSheet = true
    }
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
        enabled = backStack.isNotEmpty() || showAddSheet || showAutoSaveOverAdd || showScanReceipt ||
            showGoalDeposit || showCategoryPicker || showDeleteData
    ) {
        when {
            showAutoSaveOverAdd -> showAutoSaveOverAdd = false
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
                    onAddClick = { openAddSheet() }
                )
            }
        ) { innerPadding ->
            AnimatedContent(
                targetState = NavEntry(selectedTab, backStack.lastOrNull(), backStack.size),
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                transitionSpec = { screenTransition(initialState, targetState) },
                label = "screen"
            ) { entry ->
              // Nền đặc để màn đang trượt không lộ màn phía dưới
              Box(modifier = Modifier.fillMaxSize().background(MoneyFlowTheme.colors.surfacePage)) {
                val route = entry.route
                if (route == null) {
                    when (entry.tab) {
                        0 -> HomeScreen(
                            homeViewModel = homeViewModel,
                            onSeeAllTransactions = { push(Route.AllTransactions) },
                            onScanBill = { showScanReceipt = true },
                            onAddExpense = { openAddSheet() },
                            onAddIncome = { openAddSheet(income = true) },
                            onClassifyTransaction = { showCategoryPicker = true },
                            onOpenTransaction = { push(Route.TransactionDetail(it)) },
                            onOpenGoal = { push(Route.SavingsGoals) },
                            onOpenNotifications = { push(Route.Notifications) }
                        )
                        1 -> ReportRoute(reportViewModel = reportViewModel)
                        2 -> BudgetRoute(
                            budgetViewModel = budgetViewModel,
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
                            onAddTransaction = { openAddSheet() }
                        )
                        is Route.EditIncomeCategory -> EditIncomeCategoryScreen(
                            categoryName = route.category,
                            onBack = { pop() },
                            onAddTransaction = { openAddSheet(income = true) },
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
                            onEdit = { openAddSheet() }
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
        }

        AnimatedVisibility(
            visible = showAddSheet,
            enter = overlaySlideUp,
            exit = overlaySlideDown
        ) {
            AddTransactionScreen(
                onDismissRequest = { showAddSheet = false },
                startWithIncome = addSheetStartsWithIncome,
                onOpenAutoSave = { showAutoSaveOverAdd = true },
                onTransactionCreated = {
                    homeViewModel.loadData()
                    budgetViewModel.load()
                }
            )
        }
        AnimatedVisibility(
            visible = showAddSheet && showAutoSaveOverAdd,
            enter = screenPushIn,
            exit = screenPopOut
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MoneyFlowTheme.colors.surfacePage)
                    .systemBarsPadding()
            ) {
                AutoSaveSettingScreen(onBack = { showAutoSaveOverAdd = false })
            }
        }
        AnimatedVisibility(
            visible = showScanReceipt,
            enter = overlaySlideUp,
            exit = overlaySlideDown
        ) {
            ScanReceiptScreen(onDismissRequest = { showScanReceipt = false })
        }
        // Hai lớp này tự vẽ scrim, nên chỉ fade + nhích nhẹ thay vì trượt cả màn
        AnimatedVisibility(
            visible = showGoalDeposit,
            enter = scrimSheetIn,
            exit = scrimSheetOut
        ) {
            GoalDepositSheet(onDismissRequest = { showGoalDeposit = false })
        }
        AnimatedVisibility(
            visible = showDeleteData,
            enter = scrimSheetIn,
            exit = scrimSheetOut
        ) {
            DeleteDataDialog(
                onDismissRequest = { showDeleteData = false },
                onConfirm = { showDeleteData = false }
            )
        }
        if (showCategoryPicker) {
            CategoryPickerSheet(
                onDismissRequest = { showCategoryPicker = false },
                categories = com.quan.finance_app.data.LedgerRepository.DEFAULT_CATEGORIES,
                onCategorySelected = { showCategoryPicker = false }
            )
        }
    }
}

/** Trạng thái điều hướng hiện tại — đổi giá trị này là AnimatedContent chạy transition. */
private data class NavEntry(val tab: Int, val route: Route?, val depth: Int)

// Thời lượng & easing theo Material motion: vào chậm dần, ra nhanh hơn một chút
private const val NAV_DURATION = 320
private val navEasing = FastOutSlowInEasing

private val screenPushIn: EnterTransition =
    slideInHorizontally(tween(NAV_DURATION, easing = navEasing)) { it } +
        fadeIn(tween(NAV_DURATION / 2))
private val screenPushOut: ExitTransition =
    slideOutHorizontally(tween(NAV_DURATION, easing = navEasing)) { -it / 4 } +
        fadeOut(tween(NAV_DURATION))
private val screenPopIn: EnterTransition =
    slideInHorizontally(tween(NAV_DURATION, easing = navEasing)) { -it / 4 } +
        fadeIn(tween(NAV_DURATION))
private val screenPopOut: ExitTransition =
    slideOutHorizontally(tween(NAV_DURATION, easing = navEasing)) { it } +
        fadeOut(tween(NAV_DURATION / 2, delayMillis = NAV_DURATION / 2))

private val overlaySlideUp: EnterTransition =
    slideInVertically(tween(NAV_DURATION, easing = navEasing)) { it }
private val overlaySlideDown: ExitTransition =
    slideOutVertically(tween(260, easing = FastOutLinearInEasing)) { it }

private val scrimSheetIn: EnterTransition =
    fadeIn(tween(220)) + slideInVertically(tween(NAV_DURATION, easing = navEasing)) { it / 10 }
private val scrimSheetOut: ExitTransition =
    fadeOut(tween(180)) + slideOutVertically(tween(180)) { it / 10 }

/**
 * Vào màn con: màn mới trượt từ phải đè lên. Back: màn hiện tại trượt ra phải, lộ màn cũ.
 * Đổi tab: chỉ fade chéo, giống bottom navigation của các app phổ biến.
 */
private fun screenTransition(from: NavEntry, to: NavEntry): ContentTransform {
    if (from.tab != to.tab || (from.depth == 0 && to.depth == 0)) {
        return fadeIn(tween(220, delayMillis = 60)) togetherWith fadeOut(tween(120))
    }
    return if (to.depth > from.depth) {
        (screenPushIn togetherWith screenPushOut).apply { targetContentZIndex = 1f }
    } else {
        (screenPopIn togetherWith screenPopOut).apply { targetContentZIndex = -1f }
    }
}

@Composable
fun CustomBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onAddClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MoneyFlowTheme.colors.surfaceCard,
        shadowElevation = 16.dp,
        tonalElevation = 0.dp,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(72.dp)
                .padding(horizontal = 8.dp),
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
