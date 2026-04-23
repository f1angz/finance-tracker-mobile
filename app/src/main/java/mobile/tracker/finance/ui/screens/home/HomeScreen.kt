package mobile.tracker.finance.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import mobile.tracker.finance.R
import mobile.tracker.finance.data.models.Transaction
import mobile.tracker.finance.navigation.Screen
import mobile.tracker.finance.ui.components.*
import mobile.tracker.finance.ui.components.AddTransactionBottomSheet
import mobile.tracker.finance.ui.screens.operations.DraftViewModel
import mobile.tracker.finance.ui.theme.*

/**
 * Главный экран приложения Finance Tracker
 * Отображает финансовую статистику, диаграммы и последние транзакции
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val draftViewModel: DraftViewModel = viewModel(context as ViewModelStoreOwner)
    val draft by draftViewModel.draft.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    // Перезагружаем данные каждый раз, когда HomeScreen становится активным экраном
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    LaunchedEffect(currentRoute) {
        if (currentRoute == Screen.Home.route) {
            viewModel.loadData()
        }
    }

    if (showAddDialog) {
        AddTransactionBottomSheet(
            onDismiss    = { showAddDialog = false },
            onSave       = { viewModel.addTransaction(it); draftViewModel.clearDraft() },
            initialDraft = draft,
            onDraftSave  = draftViewModel::saveDraft
        )
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                selectedMonth    = uiState.selectedMonth,
                isNextDisabled   = uiState.selectedMonth >= HomeViewModel.currentMonthString(),
                onPrevMonth      = viewModel::prevMonth,
                onNextMonth      = viewModel::nextMonth,
                onAddClick       = { showAddDialog = true }
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = 0,
                onTabSelected = { tab ->
                    when (tab) {
                        1 -> navController.navigate(Screen.Operations.route) {
                            launchSingleTop = true
                        }
                        2 -> navController.navigate(Screen.Categories.route) {
                            launchSingleTop = true
                        }
                        3 -> navController.navigate(Screen.AiAssistant.route) {
                            launchSingleTop = true
                        }
                        4 -> navController.navigate(Screen.Goals.route) {
                            launchSingleTop = true
                        }
                        5 -> navController.navigate(Screen.Settings.route) {
                            launchSingleTop = true
                        }
                    }
                }
            )
        },
        containerColor = LocalAppColors.current.background
    ) { paddingValues ->
        if (uiState.isLoading && uiState.stats == null) {
            // Показываем индикатор загрузки только при первой загрузке
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val isWideScreen = maxWidth >= 600.dp
                if (isWideScreen) {
                    WideScreenContent(uiState = uiState)
                } else {
                    CompactScreenContent(uiState = uiState)
                }
            }
        }

        // Показываем ошибку если есть
        uiState.error?.let { error ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                containerColor = RedNegative
            ) {
                Text(text = error)
            }
        }
    }
}

@Composable
private fun CompactScreenContent(uiState: HomeUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            uiState.stats?.let { stats ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatsCard(
                        title = "Баланс",
                        amount = stats.balance,
                        changePercent = stats.balanceChange,
                        icon = painterResource(R.drawable.balance),
                        iconBackgroundColor = PrimaryBlue,
                        modifier = Modifier.fillMaxWidth(),
                        horizontal = true
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatsCard(
                            title = "Доходы",
                            amount = stats.income,
                            changePercent = stats.incomeChange,
                            icon = painterResource(R.drawable.incomes),
                            iconBackgroundColor = GreenPositive,
                            modifier = Modifier.weight(1f)
                        )
                        StatsCard(
                            title = "Расходы",
                            amount = stats.expense,
                            changePercent = stats.expenseChange,
                            icon = painterResource(R.drawable.exp),
                            iconBackgroundColor = RedNegative,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
        item {
            if (uiState.categoryExpenses.isNotEmpty()) {
                PieChartSection(expenses = uiState.categoryExpenses)
            }
        }
        item {
            if (uiState.monthlyStats.isNotEmpty()) {
                BarChartSection(monthlyStats = uiState.monthlyStats)
            }
        }
        item {
            RecentTransactionsCard(uiState.recentTransactions)
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun WideScreenContent(uiState: HomeUiState) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Левая колонка: статистика + последние операции
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                uiState.stats?.let { stats ->
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatsCard(
                            title = "Баланс",
                            amount = stats.balance,
                            changePercent = stats.balanceChange,
                            icon = painterResource(R.drawable.balance),
                            iconBackgroundColor = PrimaryBlue,
                            modifier = Modifier.fillMaxWidth(),
                            horizontal = true
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            StatsCard(
                                title = "Доходы",
                                amount = stats.income,
                                changePercent = stats.incomeChange,
                                icon = painterResource(R.drawable.incomes),
                                iconBackgroundColor = GreenPositive,
                                modifier = Modifier.weight(1f)
                            )
                            StatsCard(
                                title = "Расходы",
                                amount = stats.expense,
                                changePercent = stats.expenseChange,
                                icon = painterResource(R.drawable.exp),
                                iconBackgroundColor = RedNegative,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            item {
                RecentTransactionsCard(uiState.recentTransactions)
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        // Правая колонка: графики
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                if (uiState.categoryExpenses.isNotEmpty()) {
                    PieChartSection(expenses = uiState.categoryExpenses)
                }
            }
            item {
                if (uiState.monthlyStats.isNotEmpty()) {
                    BarChartSection(monthlyStats = uiState.monthlyStats)
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun RecentTransactionsCard(transactions: List<Transaction>) {
    if (transactions.isEmpty()) return
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalAppColors.current.cardBackground, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Последние операции",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LocalAppColors.current.textPrimary
                )
                TextButton(onClick = {}) {
                    Text(text = "Все", fontSize = 14.sp, color = PrimaryBlue)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            transactions.forEach { transaction ->
                TransactionItem(transaction = transaction)
            }
        }
    }
}

/**
 * Верхняя панель главного экрана
 */
@Composable
private fun HomeTopBar(
    selectedMonth: String,
    isNextDisabled: Boolean,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalAppColors.current.background)
            .statusBarsPadding()
    ) {
        // ── Строка заголовка ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Меню",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "Главная",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalAppColors.current.textPrimary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Уведомления",
                            tint = TextPrimary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-8).dp, y = 8.dp)
                            .size(8.dp)
                            .background(RedNegative, CircleShape)
                    )
                }
                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier.background(PrimaryBlue, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить",
                        tint = White
                    )
                }
            }
        }

        // ── Навигатор месяца ──
        if (selectedMonth.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevMonth) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Предыдущий месяц",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = HomeViewModel.formatMonthDisplay(selectedMonth),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LocalAppColors.current.textPrimary
                )
                IconButton(
                    onClick = onNextMonth,
                    enabled = !isNextDisabled
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Следующий месяц",
                        tint = if (isNextDisabled) TextPrimary.copy(alpha = 0.3f) else TextPrimary
                    )
                }
            }
        }
    }
}
