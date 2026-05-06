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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import mobile.tracker.finance.data.models.Debt
import mobile.tracker.finance.data.models.DebtType
import mobile.tracker.finance.data.models.Goal
import mobile.tracker.finance.navigation.Screen
import mobile.tracker.finance.ui.components.AddTransactionBottomSheet
import mobile.tracker.finance.ui.components.BottomNavBar
import mobile.tracker.finance.ui.components.GradientBackground
import mobile.tracker.finance.ui.screens.operations.DraftViewModel
import mobile.tracker.finance.ui.theme.*
import java.util.Calendar

// ─── Emoji-набор для целей ────────────────────────────────────────────────────

private val goalEmojis = listOf(
    "🎯", "🏠", "🚗", "✈️", "💻", "📱",
    "🎓", "💍", "🏖️", "💰", "🎸", "📚",
    "🏋️", "🌍", "🏆", "🛍️", "🎮", "📷",
    "🎊", "💎", "🌸", "⚽", "🚀", "🌟"
)

// ─── Цвета, специфичные для экрана Цели ──────────────────────────────────────

private val SavedGreen          = Color(0xFF00A63E)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    navController: NavHostController,
    viewModel: GoalsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val draftViewModel: DraftViewModel = viewModel(context as ViewModelStoreOwner)
    val draft by draftViewModel.draft.collectAsState()
    var showAddTransactionSheet by remember { mutableStateOf(false) }

    if (showAddTransactionSheet) {
        AddTransactionBottomSheet(
            onDismiss    = { showAddTransactionSheet = false },
            onSave       = { viewModel.addTransaction(it); draftViewModel.clearDraft() },
            initialDraft = draft,
            onDraftSave  = draftViewModel::saveDraft
        )
    }

    if (uiState.showAddGoalSheet) {
        AddGoalBottomSheet(
            onDismiss = viewModel::onDismissAddGoal,
            onCreate  = viewModel::createGoal
        )
    }

    if (uiState.showAddDebtSheet) {
        AddDebtBottomSheet(
            onDismiss = viewModel::onDismissAddDebt,
            onCreate  = viewModel::createDebt
        )
    }

    val contributeGoalId = uiState.contributeGoalId
    if (contributeGoalId != null) {
        val goal = uiState.goals.find { it.id == contributeGoalId }
        if (goal != null) {
            ContributeBottomSheet(
                goal         = goal,
                onDismiss    = viewModel::onDismissContribute,
                onContribute = viewModel::contribute
            )
        }
    }

    GradientBackground {
        Scaffold(
            topBar = { GoalsTopBar() },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        when (uiState.selectedTab) {
                            GoalsTab.GOALS -> viewModel.onAddGoal()
                            GoalsTab.DEBTS -> viewModel.onAddDebt()
                        }
                    },
                    containerColor = PrimaryBlue,
                    contentColor   = White,
                    shape          = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить")
                }
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
                            1 -> navController.navigate(Screen.Operations.route) { launchSingleTop = true }
                            2 -> navController.navigate(Screen.Categories.route) { launchSingleTop = true }
                            3 -> navController.navigate(Screen.AiAssistant.route) { launchSingleTop = true }
                            5 -> navController.navigate(Screen.Settings.route) { launchSingleTop = true }
                        }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                GoalsTabSwitcher(
                    selectedTab   = uiState.selectedTab,
                    goalsCount    = uiState.goals.size,
                    debtsCount    = uiState.activeDebts.size,
                    onTabSelected = viewModel::selectTab
                )

                when {
                    uiState.isLoading -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }

                    else -> AnimatedContent(
                        targetState = uiState.selectedTab,
                        label       = "GoalsTabContent"
                    ) { tab ->
                        when (tab) {
                            GoalsTab.GOALS -> GoalsTabContent(
                                goals             = uiState.goals,
                                onContributeClick = viewModel::onContribute,
                                onDeleteClick     = viewModel::deleteGoal
                            )
                            GoalsTab.DEBTS -> DebtsTabContent(
                                activeDebts  = uiState.activeDebts,
                                paidDebts    = uiState.paidDebts,
                                onRepayClick = viewModel::onRepayDebt,
                                onDeleteClick = viewModel::deleteDebt
                            )
                        }
                    }
                }
            }

            uiState.error?.let { error ->
                Snackbar(
                    modifier       = Modifier.padding(16.dp),
                    containerColor = RedNegative
                ) { Text(text = error) }
            }
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────

@Composable
private fun GoalsTopBar() {
    val colors = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .statusBarsPadding()
    ) {
        Text(
            text       = "Цели",
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold,
            color      = colors.textPrimary,
            modifier   = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        HorizontalDivider(color = colors.cardBorder, thickness = 1.dp)
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
            .background(LocalAppColors.current.inputBackground, RoundedCornerShape(14.dp))
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
    val colors = LocalAppColors.current
    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(3.dp)
            .then(
                if (isSelected) Modifier.background(colors.cardBackground, RoundedCornerShape(12.dp))
                else Modifier
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text       = label,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Medium,
                color      = colors.textPrimary
            )
            Box(
                modifier = Modifier
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = count.toString(),
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color      = colors.textPrimary
                )
            }
        }
    }
}

// ─── Goals Tab ────────────────────────────────────────────────────────────────

@Composable
private fun GoalsTabContent(
    goals: List<Goal>,
    onContributeClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    if (goals.isEmpty()) {
        EmptyState(text = "Нет целей.\nНажмите + чтобы добавить новую цель")
        return
    }
    LazyColumn(
        contentPadding     = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = goals, key = { it.id }) { goal ->
            GoalCard(
                goal              = goal,
                onContributeClick = { onContributeClick(goal.id) },
                onDeleteClick     = { onDeleteClick(goal.id) }
            )
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun GoalCard(
    goal: Goal,
    onContributeClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val accentColor = remember(goal.accentColor) {
        goal.accentColor?.let { hex ->
            runCatching { Color(android.graphics.Color.parseColor("#$hex")) }.getOrNull()
        } ?: PrimaryBlue
    }
    val colors = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.cardBackground, RoundedCornerShape(14.dp))
            .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Шапка: emoji | название + дни | процент + delete
        Row(
            modifier             = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment    = Alignment.Top
        ) {
            Text(text = goal.emoji, fontSize = 30.sp, modifier = Modifier.width(36.dp))
            Column(
                modifier             = Modifier.weight(1f),
                verticalArrangement  = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text       = goal.title,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = colors.textPrimary
                )
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector    = Icons.Outlined.AccountBox,
                        contentDescription = null,
                        tint           = colors.textSecondary,
                        modifier       = Modifier.size(12.dp)
                    )
                    Text(text = "${goal.daysLeft} дней", fontSize = 12.sp, color = colors.textSecondary)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text       = goal.percentFormatted,
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color      = accentColor
                )
                Text(text = "готово", fontSize = 12.sp, color = colors.textSecondary, textAlign = TextAlign.Center)
            }
            IconButton(
                onClick  = onDeleteClick,
                modifier = Modifier.size(28.dp).offset(x = 4.dp)
            ) {
                Icon(
                    imageVector    = Icons.Default.Close,
                    contentDescription = "Удалить цель",
                    tint           = colors.textSecondary,
                    modifier       = Modifier.size(14.dp)
                )
            }
        }

        // Прогресс-бар
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(colors.inputBackground)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(goal.progressFraction)
                    .fillMaxHeight()
                    .background(accentColor, RoundedCornerShape(50.dp))
            )
        }

        // Статистика
        Row(modifier = Modifier.fillMaxWidth()) {
            GoalStatColumn("Накоплено",  formatAmountK(goal.savedAmount),     SavedGreen,        Modifier.weight(1f))
            GoalStatColumn("Осталось",   formatAmountK(goal.remainingAmount), colors.textPrimary, Modifier.weight(1f))
            GoalStatColumn("Цель",       formatAmountK(goal.targetAmount),    colors.textPrimary, Modifier.weight(1f))
        }

        // Кнопка «Пополнить»
        Button(
            onClick         = onContributeClick,
            modifier        = Modifier.fillMaxWidth().height(36.dp),
            colors          = ButtonDefaults.buttonColors(containerColor = accentColor),
            shape           = RoundedCornerShape(8.dp),
            contentPadding  = PaddingValues(0.dp)
        ) {
            Text(text = "Пополнить", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = White)
        }
    }
}

@Composable
private fun GoalStatColumn(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = label, fontSize = 12.sp, color = LocalAppColors.current.textSecondary)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}

// ─── Debts Tab ────────────────────────────────────────────────────────────────

@Composable
private fun DebtsTabContent(
    activeDebts: List<Debt>,
    paidDebts: List<Debt>,
    onRepayClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    if (activeDebts.isEmpty() && paidDebts.isEmpty()) {
        EmptyState(text = "Нет долгов.\nНажмите + чтобы добавить запись")
        return
    }
    LazyColumn(
        contentPadding     = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (activeDebts.isNotEmpty()) {
            item {
                Text(
                    text       = "Активные",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = LocalAppColors.current.textPrimary,
                    modifier   = Modifier.padding(horizontal = 4.dp)
                )
            }
            items(items = activeDebts, key = { it.id }) { debt ->
                ActiveDebtCard(
                    debt         = debt,
                    onRepayClick = { onRepayClick(debt.id) },
                    onDeleteClick = { onDeleteClick(debt.id) }
                )
            }
        }
        if (paidDebts.isNotEmpty()) {
            item {
                Text(
                    text       = "Погашенные",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = LocalAppColors.current.textPrimary,
                    modifier   = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                )
            }
            items(items = paidDebts, key = { it.id }) { debt ->
                PaidDebtCard(debt = debt, onDeleteClick = { onDeleteClick(debt.id) })
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun ActiveDebtCard(
    debt: Debt,
    onRepayClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isIOwe      = debt.type == DebtType.I_OWE
    val avatarBg    = if (isIOwe) DebtOweAvatarBg    else DebtTheyAvatarBg
    val badgeBg     = if (isIOwe) DebtOweBadgeBg     else DebtTheyBadgeBg
    val badgeBorder = if (isIOwe) DebtOweBadgeBorder else DebtTheyBadgeBorder
    val badgeText   = if (isIOwe) DebtOweBadgeText   else DebtTheyBadgeText
    val badgeLabel  = if (isIOwe) "Я должен"         else "Мне должны"
    val amountColor = if (isIOwe) DebtOweAmount      else DebtTheyAmount
    val iconTint    = if (isIOwe) DebtOweAmount      else DebtTheyAmount
    val colors      = LocalAppColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.cardBackground, RoundedCornerShape(14.dp))
            .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Шапка: аватар | имя + бейдж | сумма | delete
        Row(
            modifier             = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment    = Alignment.Top
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(avatarBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = debt.personName, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
                Box(
                    modifier = Modifier
                        .background(badgeBg, RoundedCornerShape(8.dp))
                        .border(1.dp, badgeBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = badgeLabel, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = badgeText)
                }
            }
            Text(
                text       = formatAmountK(debt.amount),
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                color      = amountColor,
                textAlign  = TextAlign.End
            )
            IconButton(onClick = onDeleteClick, modifier = Modifier.size(28.dp).offset(x = 4.dp)) {
                Icon(Icons.Default.Close, "Удалить", tint = colors.textSecondary, modifier = Modifier.size(14.dp))
            }
        }

        HorizontalDivider(color = colors.cardBorder, thickness = 1.dp)

        // Срок возврата + кнопка «Погасить»
        Row(
            modifier             = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment    = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = "Срок возврата", fontSize = 12.sp, color = colors.textSecondary)
                Text(text = formatIsoDate(debt.dueDate), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.textPrimary)
            }
            OutlinedButton(
                onClick        = onRepayClick,
                modifier       = Modifier.height(32.dp),
                shape          = RoundedCornerShape(8.dp),
                border         = BorderStroke(1.dp, colors.cardBorder),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                colors         = ButtonDefaults.outlinedButtonColors(containerColor = colors.cardBackground)
            ) {
                Icon(Icons.Outlined.CheckCircle, null, tint = colors.textPrimary, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(6.dp))
                Text(text = "Погасить", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = colors.textPrimary)
            }
        }
    }
}

@Composable
private fun PaidDebtCard(debt: Debt, onDeleteClick: () -> Unit) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(0.6f)
            .background(colors.cardBackground, RoundedCornerShape(14.dp))
            .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.CheckCircle, null, tint = colors.textSecondary, modifier = Modifier.size(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = debt.personName, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = colors.textPrimary)
            Text(text = "Возвращено", fontSize = 12.sp, color = colors.textSecondary)
        }
        Text(text = formatAmountK(debt.amount), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
        IconButton(onClick = onDeleteClick, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, "Удалить", tint = colors.textSecondary, modifier = Modifier.size(14.dp))
        }
    }
}

// ─── Добавить цель (bottom sheet) ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddGoalBottomSheet(
    onDismiss: () -> Unit,
    onCreate: (emoji: String, title: String, targetAmount: Double, targetDate: String) -> Unit
) {
    val sheetState  = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors      = LocalAppColors.current
    val context     = LocalContext.current

    var selectedEmoji by remember { mutableStateOf("🎯") }
    var title        by remember { mutableStateOf("") }
    var amountText   by remember { mutableStateOf("") }
    var targetDate   by remember { mutableStateOf("") }

    val isValid = title.isNotBlank() && amountText.toDoubleOrNull() != null && targetDate.isNotEmpty()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        containerColor   = colors.cardBackground,
        dragHandle       = null,
        shape            = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Заголовок
            Row(
                modifier             = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment    = Alignment.CenterVertically
            ) {
                Text("Новая цель", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, null, tint = colors.textSecondary, modifier = Modifier.size(18.dp))
                }
            }

            // Emoji-пикер
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SheetFieldLabel("Emoji")
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    goalEmojis.chunked(6).forEach { row ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            row.forEach { emoji ->
                                val isSelected = emoji == selectedEmoji
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) PrimaryBlue.copy(alpha = 0.12f)
                                            else colors.inputBackground
                                        )
                                        .then(
                                            if (isSelected) Modifier.border(2.dp, PrimaryBlue, RoundedCornerShape(8.dp))
                                            else Modifier
                                        )
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { selectedEmoji = emoji },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = emoji, fontSize = 20.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Название
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SheetFieldLabel("Название")
                OutlinedTextField(
                    value       = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Например, новый ноутбук", color = colors.textSecondary, fontSize = 14.sp) },
                    modifier    = Modifier.fillMaxWidth(),
                    shape       = RoundedCornerShape(8.dp),
                    singleLine  = true,
                    colors      = sheetTextFieldColors(colors)
                )
            }

            // Целевая сумма
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SheetFieldLabel("Целевая сумма")
                OutlinedTextField(
                    value       = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                    placeholder = { Text("0", color = colors.textSecondary, fontSize = 14.sp) },
                    leadingIcon = { Text("₽", fontSize = 16.sp, color = colors.textSecondary, modifier = Modifier.padding(start = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier    = Modifier.fillMaxWidth(),
                    shape       = RoundedCornerShape(8.dp),
                    singleLine  = true,
                    colors      = sheetTextFieldColors(colors)
                )
            }

            // Дата достижения
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SheetFieldLabel("Дата достижения")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.inputBackground, RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            val cal = Calendar.getInstance()
                            android.app.DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    targetDate = "%d-%02d-%02d".format(year, month + 1, day)
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                        .padding(horizontal = 13.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text  = if (targetDate.isEmpty()) "Выберите дату" else formatIsoDate(targetDate),
                        color = if (targetDate.isEmpty()) colors.textSecondary else colors.textPrimary,
                        fontSize = 14.sp
                    )
                    Icon(Icons.Default.CalendarToday, null, tint = colors.textSecondary, modifier = Modifier.size(16.dp))
                }
            }

            // Кнопка создать
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: return@Button
                    onCreate(selectedEmoji, title.trim(), amount, targetDate)
                },
                enabled        = isValid,
                modifier       = Modifier.fillMaxWidth().height(44.dp),
                shape          = RoundedCornerShape(10.dp),
                colors         = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = White)
            ) {
                Text("Создать цель", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─── Добавить долг (bottom sheet) ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddDebtBottomSheet(
    onDismiss: () -> Unit,
    onCreate: (personName: String, type: DebtType, amount: Double, dueDate: String) -> Unit
) {
    val sheetState   = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors       = LocalAppColors.current
    val context      = LocalContext.current

    var personName   by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(DebtType.I_OWE) }
    var amountText   by remember { mutableStateOf("") }
    var dueDate      by remember { mutableStateOf("") }

    val isValid = personName.isNotBlank() && amountText.toDoubleOrNull() != null && dueDate.isNotEmpty()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        containerColor   = colors.cardBackground,
        dragHandle       = null,
        shape            = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Заголовок
            Row(
                modifier             = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment    = Alignment.CenterVertically
            ) {
                Text("Новый долг", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, null, tint = colors.textSecondary, modifier = Modifier.size(18.dp))
                }
            }

            // Имя человека
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SheetFieldLabel("Имя человека")
                OutlinedTextField(
                    value         = personName,
                    onValueChange = { personName = it },
                    placeholder   = { Text("Например, Иван Иванов", color = colors.textSecondary, fontSize = 14.sp) },
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(8.dp),
                    singleLine    = true,
                    colors        = sheetTextFieldColors(colors)
                )
            }

            // Тип долга
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SheetFieldLabel("Тип")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.inputBackground, RoundedCornerShape(8.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(DebtType.I_OWE to "Я должен", DebtType.THEY_OWE to "Мне должны").forEach { (type, label) ->
                        val isSelected = selectedType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) PrimaryBlue else Color.Transparent)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { selectedType = type }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text       = label,
                                fontSize   = 14.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color      = if (isSelected) White else colors.textSecondary
                            )
                        }
                    }
                }
            }

            // Сумма
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SheetFieldLabel("Сумма")
                OutlinedTextField(
                    value         = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                    placeholder   = { Text("0", color = colors.textSecondary, fontSize = 14.sp) },
                    leadingIcon   = { Text("₽", fontSize = 16.sp, color = colors.textSecondary, modifier = Modifier.padding(start = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(8.dp),
                    singleLine    = true,
                    colors        = sheetTextFieldColors(colors)
                )
            }

            // Срок возврата
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SheetFieldLabel("Срок возврата")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.inputBackground, RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            val cal = Calendar.getInstance()
                            android.app.DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    dueDate = "%d-%02d-%02d".format(year, month + 1, day)
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                        .padding(horizontal = 13.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text  = if (dueDate.isEmpty()) "Выберите дату" else formatIsoDate(dueDate),
                        color = if (dueDate.isEmpty()) colors.textSecondary else colors.textPrimary,
                        fontSize = 14.sp
                    )
                    Icon(Icons.Default.CalendarToday, null, tint = colors.textSecondary, modifier = Modifier.size(16.dp))
                }
            }

            // Кнопка создать
            val debtAccentColor = if (selectedType == DebtType.I_OWE) DebtOweAmount else DebtTheyAmount
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: return@Button
                    onCreate(personName.trim(), selectedType, amount, dueDate)
                },
                enabled  = isValid,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape    = RoundedCornerShape(10.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = debtAccentColor, contentColor = White)
            ) {
                Text("Добавить долг", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─── Пополнить цель (bottom sheet) ────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContributeBottomSheet(
    goal: Goal,
    onDismiss: () -> Unit,
    onContribute: (Double) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors     = LocalAppColors.current

    val accentColor = remember(goal.accentColor) {
        goal.accentColor?.let { hex ->
            runCatching { Color(android.graphics.Color.parseColor("#$hex")) }.getOrNull()
        } ?: PrimaryBlue
    }

    var amountText by remember { mutableStateOf("") }
    val isValid    = amountText.toDoubleOrNull()?.let { it > 0 } == true

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        containerColor   = colors.cardBackground,
        dragHandle       = null,
        shape            = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Заголовок
            Row(
                modifier             = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment    = Alignment.CenterVertically
            ) {
                Column {
                    Text("Пополнить цель", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
                    Text(
                        text = "${goal.emoji} ${goal.title}",
                        fontSize = 13.sp,
                        color = colors.textSecondary
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, null, tint = colors.textSecondary, modifier = Modifier.size(18.dp))
                }
            }

            // Прогресс-бар с текущим состоянием
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier             = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text     = "Накоплено: ${formatAmountK(goal.savedAmount)}",
                        fontSize = 13.sp,
                        color    = SavedGreen,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text     = "Цель: ${formatAmountK(goal.targetAmount)}",
                        fontSize = 13.sp,
                        color    = colors.textSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(colors.inputBackground)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(goal.progressFraction)
                            .fillMaxHeight()
                            .background(accentColor, RoundedCornerShape(50.dp))
                    )
                }
            }

            // Сумма пополнения
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SheetFieldLabel("Сумма пополнения")
                OutlinedTextField(
                    value         = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                    placeholder   = { Text("0", color = colors.textSecondary, fontSize = 14.sp) },
                    leadingIcon   = { Text("₽", fontSize = 16.sp, color = colors.textSecondary, modifier = Modifier.padding(start = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(8.dp),
                    singleLine    = true,
                    colors        = sheetTextFieldColors(colors)
                )
            }

            // Кнопка
            Button(
                onClick  = { amountText.toDoubleOrNull()?.let { onContribute(it) } },
                enabled  = isValid,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape    = RoundedCornerShape(10.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = White)
            ) {
                Text("Пополнить", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─── Вспомогательные composable ───────────────────────────────────────────────

@Composable
private fun EmptyState(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text      = text,
            color     = LocalAppColors.current.textSecondary,
            fontSize  = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier  = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
private fun SheetFieldLabel(text: String) {
    Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = LocalAppColors.current.textPrimary)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun sheetTextFieldColors(colors: AppColors) = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = colors.inputBackground,
    focusedContainerColor   = colors.inputBackground,
    unfocusedBorderColor    = Color.Transparent,
    focusedBorderColor      = PrimaryBlue,
    unfocusedTextColor      = colors.textPrimary,
    focusedTextColor        = colors.textPrimary
)

// ─── Форматирование ───────────────────────────────────────────────────────────

private fun formatAmountK(amount: Double): String =
    if (amount >= 1_000) "₽ ${(amount / 1_000).toInt()}K"
    else "₽ ${amount.toInt()}"

private fun formatIsoDate(isoDate: String): String =
    try {
        val parts = isoDate.split("-")
        val day   = parts[2].toInt()
        val month = parts[1].toInt()
        val year  = parts[0]
        val months = arrayOf(
            "", "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря"
        )
        "$day ${months.getOrElse(month) { "" }} $year"
    } catch (e: Exception) { isoDate }
