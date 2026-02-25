package mobile.tracker.finance.ui.screens.limits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import mobile.tracker.finance.data.models.Limit
import mobile.tracker.finance.data.models.LimitStatus
import mobile.tracker.finance.navigation.Screen
import mobile.tracker.finance.ui.components.BottomNavBar
import mobile.tracker.finance.ui.theme.*

// ─── Цвета, специфичные для экрана Лимиты ────────────────────────────────────

private val WarningBg       = Color(0xFFFFF7ED)
private val WarningBorder   = Color(0xFFFFD6A8)
private val WarningText     = Color(0xFFCA3500)
private val ExceededBg      = Color(0xFFFFF1F2)
private val ExceededBorder  = Color(0xFFFFB3B3)
private val ExceededText    = Color(0xFFDC2626)
private val OrangeFill      = Color(0xFFFF6900)
private val TrackColor      = Color(0xFFF3F4F6)
private val PercentColor    = Color(0xFFF54900)
private val SpentTextColor  = Color(0xFF364153)
private val SubtextColor    = Color(0xFF6A7282)
private val RemainingGreen  = Color(0xFF00A63E)
private val SpentRed        = Color(0xFFE7000B)
private val DividerColor    = Color(0xFFF3F4F6)
private val CardBorder      = Color(0x1A000000)   // rgba(0,0,0,0.1)
private val HeaderBorder    = Color(0xFFE5E7EB)

// ─── Экран ────────────────────────────────────────────────────────────────────

@Composable
fun LimitsScreen(
    navController: NavHostController,
    viewModel: LimitsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { LimitsTopBar(onAddClick = viewModel::onAddLimit) },
        bottomBar = {
            BottomNavBar(
                selectedTab = 3,
                onTabSelected = { tab ->
                    when (tab) {
                        0 -> navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                        1 -> navController.navigate(Screen.Operations.route) {
                            launchSingleTop = true
                        }
                        2 -> navController.navigate(Screen.Categories.route) {
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
        containerColor = Color(0xFFF9FAFB)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!uiState.isLoading) {
                LimitsSummaryRow(
                    totalLimit     = uiState.totalLimit,
                    totalSpent     = uiState.totalSpent,
                    totalRemaining = uiState.totalRemaining
                )
            }

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }

                uiState.limits.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Нет лимитов",
                            color = TextSecondary,
                            fontSize = 16.sp
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items = uiState.limits, key = { it.id }) { limit ->
                            LimitCard(limit = limit)
                        }
                        item { Spacer(Modifier.height(8.dp)) }
                    }
                }
            }
        }

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

// ─── Top Bar ─────────────────────────────────────────────────────────────────

@Composable
private fun LimitsTopBar(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground)
    ) {
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
                IconButton(onClick = { /* TODO: боковое меню */ }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Меню",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "Лимиты",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box {
                    IconButton(onClick = { /* TODO: уведомления */ }) {
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
                        contentDescription = "Добавить лимит",
                        tint = White
                    )
                }
            }
        }

        HorizontalDivider(color = HeaderBorder, thickness = 1.dp)
    }
}

// ─── Summary Row ─────────────────────────────────────────────────────────────

@Composable
private fun LimitsSummaryRow(
    totalLimit: Double,
    totalSpent: Double,
    totalRemaining: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryStatCard(
            label       = "Лимит",
            amount      = totalLimit,
            amountColor = TextPrimary,
            modifier    = Modifier.weight(1f)
        )
        SummaryStatCard(
            label       = "Потрачено",
            amount      = totalSpent,
            amountColor = SpentRed,
            modifier    = Modifier.weight(1f)
        )
        SummaryStatCard(
            label       = "Осталось",
            amount      = totalRemaining,
            amountColor = RemainingGreen,
            modifier    = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryStatCard(
    label: String,
    amount: Double,
    amountColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(CardBackground, RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Text(
            text     = label,
            fontSize = 12.sp,
            color    = SubtextColor
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text       = formatAmountK(amount),
            fontSize   = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color      = amountColor
        )
    }
}

// ─── Limit Card ──────────────────────────────────────────────────────────────

@Composable
private fun LimitCard(limit: Limit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground, RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Заголовок: название категории + период | Бейдж статуса
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text       = limit.categoryName,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary
                )
                Text(
                    text     = limit.period,
                    fontSize = 12.sp,
                    color    = SubtextColor
                )
            }

            if (limit.status != LimitStatus.OK) {
                StatusBadge(status = limit.status)
            }
        }

        // Прогресс: суммы + прогресс-бар
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text       = formatAmount(limit.spentAmount),
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color      = SpentTextColor
                )
                Text(
                    text     = formatAmount(limit.limitAmount),
                    fontSize = 14.sp,
                    color    = SubtextColor
                )
            }

            // Прогресс-бар
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(TrackColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(limit.progressFraction)
                        .fillMaxHeight()
                        .background(OrangeFill, RoundedCornerShape(50))
                )
            }
        }

        // Подвал: процент и остаток (с разделителем)
        Column {
            HorizontalDivider(color = DividerColor, thickness = 1.dp)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = "Процент", fontSize = 12.sp, color = SubtextColor)
                    Text(
                        text       = limit.percentFormatted,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = PercentColor
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(text = "Осталось", fontSize = 12.sp, color = SubtextColor)
                    Text(
                        text       = formatAmount(limit.remainingAmount),
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = RemainingGreen
                    )
                }
            }
        }

        // Предупреждающая полоса
        if (limit.status != LimitStatus.OK) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WarningBg, RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Text(
                    text     = "Осталось ${formatAmount(limit.remainingAmount)}",
                    fontSize = 12.sp,
                    color    = WarningText
                )
            }
        }
    }
}

// ─── Status Badge ─────────────────────────────────────────────────────────────

@Composable
private fun StatusBadge(status: LimitStatus) {
    val (text, bgColor, borderColor, textColor) = when (status) {
        LimitStatus.CLOSE    -> StatusBadgeConfig("Близко",   WarningBg,  WarningBorder,  WarningText)
        LimitStatus.EXCEEDED -> StatusBadgeConfig("Превышен", ExceededBg, ExceededBorder, ExceededText)
        LimitStatus.OK       -> return
    }

    Row(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector    = Icons.Default.Warning,
            contentDescription = null,
            tint           = textColor,
            modifier       = Modifier.size(12.dp)
        )
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = textColor)
    }
}

private data class StatusBadgeConfig(
    val text: String,
    val bgColor: Color,
    val borderColor: Color,
    val textColor: Color
)

// ─── Форматирование чисел ─────────────────────────────────────────────────────

/** ₽ 78K / ₽ 500 — для сводных карточек */
private fun formatAmountK(amount: Double): String =
    if (amount >= 1_000) "₽ ${(amount / 1_000).toInt()}K"
    else "₽ ${amount.toInt()}"

/** ₽ 18 500 — пробел как разделитель тысяч */
private fun formatAmount(amount: Double): String {
    val raw = amount.toLong().toString()
    val spaced = raw.reversed().chunked(3).joinToString(" ").reversed()
    return "₽ $spaced"
}
