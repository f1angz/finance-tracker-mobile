package mobile.tracker.finance.ui.screens.goals

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import mobile.tracker.finance.data.models.Debt
import mobile.tracker.finance.data.models.DebtType
import mobile.tracker.finance.data.models.Goal
import mobile.tracker.finance.navigation.Screen
import mobile.tracker.finance.ui.components.BottomNavBar
import mobile.tracker.finance.ui.theme.*

// ─── Цвета, специфичные для экрана Цели ──────────────────────────────────────

private val TabBg               = Color(0xFFECECF0)
private val CardBorder          = Color(0x1A000000)
private val DividerColor        = Color(0xFFF3F4F6)
private val SubtextColor        = Color(0xFF6A7282)
private val SectionHeaderColor  = Color(0xFF364153)
private val TrackColor          = Color(0xFFF3F4F6)
private val SavedGreen          = Color(0xFF00A63E)
private val HeaderBorder        = Color(0xFFE5E7EB)

private val DebtOweAvatarBg     = Color(0xFFFEF2F2)
private val DebtTheyAvatarBg    = Color(0xFFF0FDF4)
private val DebtOweBadgeBg      = Color(0xFFFEF2F2)
private val DebtOweBadgeBorder  = Color(0xFFFFC9C9)
private val DebtOweBadgeText    = Color(0xFFC10007)
private val DebtOweAmount       = Color(0xFFE7000B)
private val DebtTheyBadgeBg     = Color(0xFFF0FDF4)
private val DebtTheyBadgeBorder = Color(0xFFB9F8CF)
private val DebtTheyBadgeText   = Color(0xFF008236)
private val DebtTheyAmount      = Color(0xFF00A63E)

// ─── Экран ────────────────────────────────────────────────────────────────────

@Composable
fun GoalsScreen(
    navController: NavHostController,
    viewModel: GoalsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            GoalsTopBar(
                onAddClick = {
                    when (uiState.selectedTab) {
                        GoalsTab.GOALS -> viewModel.onAddGoal()
                        GoalsTab.DEBTS -> viewModel.onAddDebt()
                    }
                }
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = 4,
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
                        3 -> navController.navigate(Screen.AiAssistant.route) {
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
            GoalsTabSwitcher(
                selectedTab  = uiState.selectedTab,
                goalsCount   = uiState.goals.size,
                debtsCount   = uiState.activeDebts.size,
                onTabSelected = viewModel::selectTab
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }

                else -> {
                    AnimatedContent(
                        targetState = uiState.selectedTab,
                        label = "GoalsTabContent"
                    ) { tab ->
                        when (tab) {
                            GoalsTab.GOALS -> GoalsTabContent(
                                goals = uiState.goals,
                                onContributeClick = viewModel::onContribute
                            )
                            GoalsTab.DEBTS -> DebtsTabContent(
                                activeDebts = uiState.activeDebts,
                                paidDebts   = uiState.paidDebts,
                                onRepayClick = viewModel::onRepayDebt
                            )
                        }
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
private fun GoalsTopBar(onAddClick: () -> Unit) {
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
                    text = "Цели",
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
                        contentDescription = "Добавить",
                        tint = White
                    )
                }
            }
        }

        HorizontalDivider(color = HeaderBorder, thickness = 1.dp)
    }
}

// ─── Tab Switcher ─────────────────────────────────────────────────────────────

@Composable
private fun GoalsTabSwitcher(
    selectedTab: GoalsTab,
    goalsCount: Int,
    debtsCount: Int,
    onTabSelected: (GoalsTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(36.dp)
            .background(TabBg, RoundedCornerShape(14.dp))
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            GoalsTabItem(
                label      = "Цели",
                count      = goalsCount,
                isSelected = selectedTab == GoalsTab.GOALS,
                onClick    = { onTabSelected(GoalsTab.GOALS) },
                modifier   = Modifier.weight(1f)
            )
            GoalsTabItem(
                label      = "Долги",
                count      = debtsCount,
                isSelected = selectedTab == GoalsTab.DEBTS,
                onClick    = { onTabSelected(GoalsTab.DEBTS) },
                modifier   = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun GoalsTabItem(
    label: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(3.dp)
            .then(
                if (isSelected)
                    Modifier.background(CardBackground, RoundedCornerShape(12.dp))
                else
                    Modifier
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Box(
                modifier = Modifier
                    .border(1.dp, Color(0x1A000000), RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            }
        }
    }
}

// ─── Goals Tab ────────────────────────────────────────────────────────────────

@Composable
private fun GoalsTabContent(
    goals: List<Goal>,
    onContributeClick: (String) -> Unit
) {
    if (goals.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Нет целей", color = SubtextColor, fontSize = 16.sp)
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = goals, key = { it.id }) { goal ->
            GoalCard(
                goal = goal,
                onContributeClick = { onContributeClick(goal.id) }
            )
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun GoalCard(goal: Goal, onContributeClick: () -> Unit) {
    val accentColor = remember(goal.accentColor) {
        Color(android.graphics.Color.parseColor("#${goal.accentColor}"))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground, RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Строка: emoji | название + дни | процент + "готово"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = goal.emoji,
                fontSize = 30.sp,
                modifier = Modifier.width(36.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = goal.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBox,
                        contentDescription = null,
                        tint = SubtextColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "${goal.daysLeft} дней",
                        fontSize = 12.sp,
                        color = SubtextColor
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = goal.percentFormatted,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Text(
                    text = "готово",
                    fontSize = 12.sp,
                    color = SubtextColor,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Прогресс-бар
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(TrackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(goal.progressFraction)
                    .fillMaxHeight()
                    .background(accentColor, RoundedCornerShape(50.dp))
            )
        }

        // Статистика: Накоплено | Осталось | Цель
        Row(modifier = Modifier.fillMaxWidth()) {
            GoalStatColumn(
                label      = "Накоплено",
                value      = formatAmountK(goal.savedAmount),
                valueColor = SavedGreen,
                modifier   = Modifier.weight(1f)
            )
            GoalStatColumn(
                label      = "Осталось",
                value      = formatAmountK(goal.remainingAmount),
                valueColor = TextPrimary,
                modifier   = Modifier.weight(1f)
            )
            GoalStatColumn(
                label      = "Цель",
                value      = formatAmountK(goal.targetAmount),
                valueColor = TextPrimary,
                modifier   = Modifier.weight(1f)
            )
        }

        // Кнопка «Пополнить»
        Button(
            onClick = onContributeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "Пополнить",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = White
            )
        }
    }
}

@Composable
private fun GoalStatColumn(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(text = label, fontSize = 12.sp, color = SubtextColor)
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

// ─── Debts Tab ────────────────────────────────────────────────────────────────

@Composable
private fun DebtsTabContent(
    activeDebts: List<Debt>,
    paidDebts: List<Debt>,
    onRepayClick: (String) -> Unit
) {
    if (activeDebts.isEmpty() && paidDebts.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Нет долгов", color = SubtextColor, fontSize = 16.sp)
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (activeDebts.isNotEmpty()) {
            item {
                Text(
                    text = "Активные",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SectionHeaderColor,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
            items(items = activeDebts, key = { it.id }) { debt ->
                ActiveDebtCard(
                    debt = debt,
                    onRepayClick = { onRepayClick(debt.id) }
                )
            }
        }

        if (paidDebts.isNotEmpty()) {
            item {
                Text(
                    text = "Погашенные",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SectionHeaderColor,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                )
            }
            items(items = paidDebts, key = { it.id }) { debt ->
                PaidDebtCard(debt = debt)
            }
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun ActiveDebtCard(debt: Debt, onRepayClick: () -> Unit) {
    val isIOwe        = debt.type == DebtType.I_OWE
    val avatarBg      = if (isIOwe) DebtOweAvatarBg    else DebtTheyAvatarBg
    val badgeBg       = if (isIOwe) DebtOweBadgeBg     else DebtTheyBadgeBg
    val badgeBorder   = if (isIOwe) DebtOweBadgeBorder else DebtTheyBadgeBorder
    val badgeText     = if (isIOwe) DebtOweBadgeText    else DebtTheyBadgeText
    val badgeLabel    = if (isIOwe) "Я должен"          else "Мне должны"
    val amountColor   = if (isIOwe) DebtOweAmount       else DebtTheyAmount
    val iconTint      = if (isIOwe) DebtOweAmount       else DebtTheyAmount

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground, RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Шапка: аватар | имя + бейдж | сумма
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(avatarBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = debt.personName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Box(
                    modifier = Modifier
                        .background(badgeBg, RoundedCornerShape(8.dp))
                        .border(1.dp, badgeBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = badgeText
                    )
                }
            }

            Text(
                text = formatAmountK(debt.amount),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor,
                textAlign = TextAlign.End
            )
        }

        HorizontalDivider(color = DividerColor, thickness = 1.dp)

        // Срок возврата + кнопка «Погасить»
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = "Срок возврата", fontSize = 12.sp, color = SubtextColor)
                Text(
                    text = debt.dueDate,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            }

            OutlinedButton(
                onClick = onRepayClick,
                modifier = Modifier.height(32.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, CardBorder),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = CardBackground)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Погасить",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun PaidDebtCard(debt: Debt) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(0.6f)
            .background(CardBackground, RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = SubtextColor,
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = debt.personName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = "Возвращено",
                fontSize = 12.sp,
                color = SubtextColor
            )
        }
        Text(
            text = formatAmountK(debt.amount),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = SectionHeaderColor
        )
    }
}

// ─── Форматирование чисел ─────────────────────────────────────────────────────

private fun formatAmountK(amount: Double): String =
    if (amount >= 1_000) "₽ ${(amount / 1_000).toInt()}K"
    else "₽ ${amount.toInt()}"
