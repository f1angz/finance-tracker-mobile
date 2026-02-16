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
import androidx.lifecycle.viewmodel.compose.viewModel
import mobile.tracker.finance.R
import mobile.tracker.finance.ui.components.*
import mobile.tracker.finance.ui.theme.*

/**
 * Главный экран приложения Finance Tracker
 * Отображает финансовую статистику, диаграммы и последние транзакции
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            HomeTopBar()
        },
        bottomBar = {
            BottomNavBar()
        },
        containerColor = BackgroundLight
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Карточки статистики (2x2 сетка)
                item {
                    uiState.stats?.let { stats ->
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                StatsCard(
                                    title = "Баланс",
                                    amount = stats.balance,
                                    changePercent = stats.balanceChange,
                                    icon = painterResource(R.drawable.balance),
                                    iconBackgroundColor = PrimaryBlue,
                                    modifier = Modifier.weight(1f)
                                )
                                StatsCard(
                                    title = "Доходы",
                                    amount = stats.income,
                                    changePercent = stats.incomeChange,
                                    icon = painterResource(R.drawable.incomes),
                                    iconBackgroundColor = GreenPositive,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                StatsCard(
                                    title = "Расходы",
                                    amount = stats.expense,
                                    changePercent = stats.expenseChange,
                                    icon = painterResource(R.drawable.exp),
                                    iconBackgroundColor = RedNegative,
                                    modifier = Modifier.weight(1f)
                                )
                                StatsCard(
                                    title = "Экономия",
                                    amount = stats.savings,
                                    changePercent = stats.savingsChange,
                                    icon = painterResource(R.drawable.economy),
                                    iconBackgroundColor = Color(0xFF9C27B0),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Круговая диаграмма расходов по категориям
                item {
                    if (uiState.categoryExpenses.isNotEmpty()) {
                        PieChartSection(expenses = uiState.categoryExpenses)
                    }
                }

                // Столбчатая диаграмма
                item {
                    if (uiState.monthlyStats.isNotEmpty()) {
                        BarChartSection(monthlyStats = uiState.monthlyStats)
                    }
                }

                // Последние операции
                item {
                    if (uiState.recentTransactions.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CardBackground, RoundedCornerShape(16.dp))
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
                                        color = TextPrimary
                                    )
                                    TextButton(onClick = { /* TODO: Навигация к полному списку */ }) {
                                        Text(
                                            text = "Все",
                                            fontSize = 14.sp,
                                            color = PrimaryBlue
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                uiState.recentTransactions.forEach { transaction ->
                                    TransactionItem(transaction = transaction)
                                }
                            }
                        }
                    }
                }

                // Отступ снизу
                item {
                    Spacer(modifier = Modifier.height(16.dp))
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

/**
 * Верхняя панель главного экрана
 */
@Composable
private fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = { /* TODO: Открыть меню */ }) {
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
                color = TextPrimary
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            // Иконка уведомлений с бейджем
            Box {
                IconButton(onClick = { /* TODO: Открыть уведомления */ }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Уведомления",
                        tint = TextPrimary
                    )
                }
                // Бейдж
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-8).dp, y = 8.dp)
                        .size(8.dp)
                        .background(RedNegative, CircleShape)
                )
            }

            // Кнопка добавления транзакции
            IconButton(
                onClick = { /* TODO: Добавить транзакцию */ },
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
}
