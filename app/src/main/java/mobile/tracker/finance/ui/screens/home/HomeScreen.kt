package mobile.tracker.finance.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import mobile.tracker.finance.R
import mobile.tracker.finance.data.models.CategoryExpense
import mobile.tracker.finance.data.models.FinanceStats
import mobile.tracker.finance.data.models.Transaction
import mobile.tracker.finance.navigation.Screen
import mobile.tracker.finance.ui.components.*
import mobile.tracker.finance.ui.components.AddTransactionBottomSheet
import mobile.tracker.finance.ui.components.pieChartColors
import mobile.tracker.finance.ui.screens.operations.DraftViewModel
import mobile.tracker.finance.ui.theme.*
import java.text.DecimalFormat

/**
 * Главный экран приложения Finance Tracker
 * Отображает финансовую статистику, диаграммы и последние транзакции
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
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

    GradientBackground {
    Scaffold(
        topBar = {
            HomeTopBar(
                selectedMonth  = uiState.selectedMonth,
                isNextDisabled = uiState.selectedMonth >= HomeViewModel.currentMonthString(),
                onPrevMonth    = viewModel::prevMonth,
                onNextMonth    = viewModel::nextMonth,
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
        containerColor = Color.Transparent
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
                    WideScreenContent(uiState = uiState, onSeeAllClick = {
                        navController.navigate(Screen.Operations.route) { launchSingleTop = true }
                    })
                } else {
                    CompactScreenContent(uiState = uiState, onSeeAllClick = {
                        navController.navigate(Screen.Operations.route) { launchSingleTop = true }
                    })
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
}

@Composable
private fun CompactScreenContent(uiState: HomeUiState, onSeeAllClick: () -> Unit) {
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
            if (uiState.categoryExpenses.isNotEmpty()) {
                TopCategoriesCard(expenses = uiState.categoryExpenses)
            }
        }
        item {
            uiState.stats?.let { SavingsRateCard(stats = it) }
        }
        item {
            if (uiState.monthlyStats.isNotEmpty()) {
                BarChartSection(monthlyStats = uiState.monthlyStats)
            }
        }
        item {
            RecentTransactionsCard(uiState.recentTransactions, onSeeAllClick)
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun WideScreenContent(uiState: HomeUiState, onSeeAllClick: () -> Unit) {
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
                RecentTransactionsCard(uiState.recentTransactions, onSeeAllClick)
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
                if (uiState.categoryExpenses.isNotEmpty()) {
                    TopCategoriesCard(expenses = uiState.categoryExpenses)
                }
            }
            item {
                uiState.stats?.let { SavingsRateCard(stats = it) }
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
private fun RecentTransactionsCard(transactions: List<Transaction>, onSeeAllClick: () -> Unit) {
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
                TextButton(onClick = onSeeAllClick) {
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
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .statusBarsPadding()
    ) {
        Text(
            text = "Главная",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = LocalAppColors.current.textPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

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

// ─── Топ расходов ─────────────────────────────────────────────────────────────

@Composable
private fun TopCategoriesCard(
    expenses: List<CategoryExpense>,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val fmt    = DecimalFormat("#,###")
    val top    = expenses.sortedByDescending { it.amount }.take(5)
    val maxAmt = top.firstOrNull()?.amount?.takeIf { it > 0 } ?: 1.0

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.cardBackground, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text       = "Топ расходов",
                fontSize   = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color      = colors.textPrimary
            )
            top.forEachIndexed { idx, expense ->
                val barColor = pieChartColors[idx % pieChartColors.size]
                val fill     = (expense.amount / maxAmt).toFloat().coerceIn(0f, 1f)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier              = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(barColor, CircleShape)
                            )
                            Text(
                                text     = expense.categoryName,
                                fontSize = 13.sp,
                                color    = colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text     = "₽${fmt.format(expense.amount.toLong())}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color    = colors.textPrimary
                        )
                    }
                    // Горизонтальный бар
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(barColor.copy(alpha = 0.15f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fill)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(3.dp))
                                .background(barColor)
                        )
                    }
                }
            }
        }
    }
}

// ─── Норма накоплений ─────────────────────────────────────────────────────────

@Composable
private fun SavingsRateCard(
    stats: FinanceStats,
    modifier: Modifier = Modifier
) {
    if (stats.income <= 0) return

    val colors   = LocalAppColors.current
    val fmt      = DecimalFormat("#,###")
    val rate     = (stats.savings / stats.income * 100).coerceIn(0.0, 100.0).toFloat()
    val barColor = when {
        rate >= 20f -> GreenPositive
        rate >= 10f -> Color(0xFFF59E0B)
        else        -> RedNegative
    }
    val rateLabel = when {
        rate >= 20f -> "Отлично"
        rate >= 10f -> "Хорошо"
        else        -> "Низкая"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.cardBackground, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = "Норма накоплений",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = colors.textPrimary
                )
                Box(
                    modifier = Modifier
                        .background(barColor.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text       = rateLabel,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color      = barColor
                    )
                }
            }

            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text       = "${"%.1f".format(rate)}%",
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color      = barColor
                )
                Text(
                    text     = "от дохода сохранено",
                    fontSize = 13.sp,
                    color    = colors.textSecondary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Прогресс-бар
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor.copy(alpha = 0.15f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(rate / 100f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(barColor)
                )
            }

            // Детали
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LabelValue("Накоплено", "₽${fmt.format(stats.savings.toLong())}", colors.textPrimary)
                LabelValue("Доход",    "₽${fmt.format(stats.income.toLong())}",   colors.textSecondary)
                LabelValue("Расход",   "₽${fmt.format(stats.expense.toLong())}",  colors.textSecondary)
            }
        }
    }
}

@Composable
private fun LabelValue(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = LocalAppColors.current.textSecondary)
        Spacer(Modifier.height(2.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}
