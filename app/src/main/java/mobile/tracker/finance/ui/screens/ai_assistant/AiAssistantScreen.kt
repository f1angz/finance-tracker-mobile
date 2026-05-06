package mobile.tracker.finance.ui.screens.ai_assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import mobile.tracker.finance.data.models.AiHealthScore
import mobile.tracker.finance.data.models.AiInsight
import mobile.tracker.finance.data.models.AiTip
import mobile.tracker.finance.data.models.ChatMessage
import mobile.tracker.finance.data.models.InsightType
import mobile.tracker.finance.navigation.Screen
import mobile.tracker.finance.ui.components.AddTransactionBottomSheet
import mobile.tracker.finance.ui.components.BottomNavBar
import mobile.tracker.finance.ui.components.GradientBackground
import mobile.tracker.finance.ui.screens.operations.DraftViewModel
import mobile.tracker.finance.ui.theme.*

// ─── Цвета экрана ────────────────────────────────────────────────────────────

private val AiPurple            = Color(0xFF9810FA)
private val GradientPurple      = Color(0xFFAD46FF)
private val GradientBlue        = Color(0xFF2B7FFF)

private val InsightRedBg        = Color(0xFFFEF2F2)
private val InsightRedBorder    = Color(0xFFFFC9C9)
private val InsightRedIconBg    = Color(0xFFFFE2E2)
private val InsightRedIcon      = Color(0xFFEF4444)

private val InsightGreenBg      = Color(0xFFF0FDF4)
private val InsightGreenBorder  = Color(0xFFB9F8CF)
private val InsightGreenIconBg  = Color(0xFFDCFCE7)
private val InsightGreenIcon    = Color(0xFF10B981)

private val InsightOrangeBg     = Color(0xFFFFF7ED)
private val InsightOrangeBorder = Color(0xFFFFD6A8)
private val InsightOrangeIconBg = Color(0xFFFFEDD4)
private val InsightOrangeIcon   = Color(0xFFF97316)

private val InsightInfoBg       = Color(0xFFEFF6FF)
private val InsightInfoBorder   = Color(0xFFBFDBFE)
private val InsightInfoIconBg   = Color(0xFFDBEAFE)

private val TabBarBg            = Color(0xFFECECF0)
private val TitleText           = Color(0xFF101828)
private val BodyText            = Color(0xFF364153)
private val SubtextGray         = Color(0xFF6A7282)
private val HeaderBorderColor   = Color(0xFFE5E7EB)

// Экран

@Composable
fun AiAssistantScreen(
    navController: NavHostController,
    viewModel: AiAssistantViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val context = LocalContext.current
    val draftViewModel: DraftViewModel = viewModel(context as ViewModelStoreOwner)
    val draft by draftViewModel.draft.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

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
        topBar = { AiTopBar() },
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
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AiInfoCard(healthScore = uiState.healthScore)

            Spacer(Modifier.height(16.dp))

            AiTabBar(
                selectedTab = selectedTab,
                onTabSelected = viewModel::selectTab,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(16.dp))

            when (selectedTab) {
                AiTab.INSIGHTS -> InsightsContent(
                    insights = uiState.insights,
                    isLoading = uiState.isLoadingInsights,
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = viewModel::refresh,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
                AiTab.TIPS -> TipsContent(
                    tips = uiState.tips,
                    isLoading = uiState.isLoadingTips,
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = viewModel::refresh,
                    onTipClick = viewModel::openTipDetail,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
                AiTab.CHAT -> ChatContent(
                    messages = uiState.chatMessages,
                    chatInput = uiState.chatInput,
                    isSending = uiState.isSendingMessage,
                    onInputChange = viewModel::onChatInputChange,
                    onSend = viewModel::sendMessage,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
            }
        }

        uiState.error?.let { error ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                containerColor = RedNegative
            ) { Text(text = error) }
        }
    }
    }

    val selectedTip = uiState.selectedTip
    if (selectedTip != null) {
        TipDetailBottomSheet(
            tip = selectedTip,
            detail = uiState.tipDetail,
            isLoading = uiState.isLoadingTipDetail,
            onDismiss = viewModel::closeTipDetail
        )
    }
}

// Верхняя панель

@Composable
private fun AiTopBar() {
    val colors = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .statusBarsPadding()
    ) {
        Text(
            text = "ИИ-Помощник",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        HorizontalDivider(color = colors.cardBorder, thickness = 1.dp)
    }
}

// Информационная карточка
@Composable
private fun AiInfoCard(healthScore: AiHealthScore?) {
    val gradient = Brush.linearGradient(
        colors = listOf(GradientPurple, GradientBlue),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    val score = healthScore ?: AiHealthScore(overall = 0, expenses = 0, savings = 0, goals = 0, discipline = 0)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(gradient)
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // Верхняя строка: иконка + название | счёт
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Иконка ИИ
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0x33FFFFFF), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "ИИ-Помощник",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Box(
                            modifier = Modifier
                                .background(Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Beta",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }

                // Общий счёт
                Column(horizontalAlignment = Alignment.End) {
                    if (healthScore != null) {
                        Text(
                            text = "${score.overall}",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    }
                    Text(
                        text = "Здоровье",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Нижняя строка: 4 метрики
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    (if (healthScore != null) "${score.expenses}" else "--") to "Расходы",
                    (if (healthScore != null) "${score.savings}" else "--") to "Накопления",
                    (if (healthScore != null) "${score.goals}" else "--") to "Цели",
                    (if (healthScore != null) "${score.discipline}" else "--") to "Дисциплина"
                ).forEach { (value, label) ->
                    MetricBox(value = value, label = label, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun MetricBox(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0x1AFFFFFF), RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center
        )
    }
}

// Вкладки на экране

@Composable
private fun AiTabBar(
    selectedTab: AiTab,
    onTabSelected: (AiTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        AiTab.INSIGHTS to "Выводы",
        AiTab.TIPS to "Советы",
        AiTab.CHAT to "Чат"
    )

    val colors = LocalAppColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(colors.inputBackground, RoundedCornerShape(14.dp))
            .padding(3.dp)
    ) {
        tabs.forEach { (tab, label) ->
            val isSelected = selectedTab == tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        color = if (isSelected) colors.cardBackground else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected(tab) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary
                )
            }
        }
    }
}

// Вкладка "Инсайты"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InsightsContent(
    insights: List<AiInsight>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AiPurple)
            }
        } else if (insights.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Нет инсайтов", color = LocalAppColors.current.textSecondary, fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(insights, key = { it.id }) { insight ->
                    InsightCard(insight = insight)
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

// Вкладка "Советы"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TipsContent(
    tips: List<AiTip>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onTipClick: (AiTip) -> Unit,
    modifier: Modifier = Modifier
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AiPurple)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tips, key = { it.id }) { tip ->
                    TipCard(tip = tip, onClick = { onTipClick(tip) })
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

// Вкладка "Чат"

@Composable
private fun ChatContent(
    messages: List<ChatMessage>,
    chatInput: String,
    isSending: Boolean,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier.imePadding()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                ChatBubble(message = message)
            }
            if (isSending) {
                item { AiTypingIndicator() }
            }
        }

        // Поле ввода
        val colors = LocalAppColors.current
        HorizontalDivider(color = colors.cardBorder, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.cardBackground)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = chatInput,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Спросите что-нибудь...",
                        color = colors.textSecondary,
                        fontSize = 14.sp
                    )
                },
                maxLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AiPurple,
                    unfocusedBorderColor = colors.cardBorder,
                    focusedContainerColor = colors.cardBackground,
                    unfocusedContainerColor = colors.cardBackground
                )
            )
            IconButton(
                onClick = onSend,
                enabled = chatInput.isNotBlank() && !isSending,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (chatInput.isNotBlank() && !isSending) AiPurple else Color(0xFFE5E7EB),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Отправить",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.isFromUser
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        val colors = LocalAppColors.current
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = if (isUser) AiPurple else colors.cardBackground,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .then(
                    if (!isUser) Modifier.border(
                        width = 1.dp,
                        color = colors.cardBorder,
                        shape = RoundedCornerShape(
                            topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp
                        )
                    ) else Modifier
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.content,
                fontSize = 14.sp,
                color = if (isUser) Color.White else colors.textPrimary,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun AiTypingIndicator() {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .background(
                colors.cardBackground,
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
            )
            .border(
                1.dp, colors.cardBorder,
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(14.dp),
            color = AiPurple,
            strokeWidth = 2.dp
        )
        Text(text = "ИИ печатает...", fontSize = 12.sp, color = colors.textSecondary)
    }
}

// Карточка "Инсайты"

private data class InsightColors(
    val bg: Color, val border: Color, val iconBg: Color, val icon: Color
)

@Composable
private fun InsightCard(insight: AiInsight, modifier: Modifier = Modifier) {
    val colors = when (insight.type) {
        InsightType.DANGER  -> InsightColors(InsightRedBg, InsightRedBorder, InsightRedIconBg, InsightRedIcon)
        InsightType.SUCCESS -> InsightColors(InsightGreenBg, InsightGreenBorder, InsightGreenIconBg, InsightGreenIcon)
        InsightType.WARNING -> InsightColors(InsightOrangeBg, InsightOrangeBorder, InsightOrangeIconBg, InsightOrangeIcon)
        InsightType.INFO    -> InsightColors(InsightInfoBg, InsightInfoBorder, InsightInfoIconBg, PrimaryBlue)
    }
    val icon: ImageVector = when (insight.type) {
        InsightType.DANGER  -> Icons.Outlined.Error
        InsightType.SUCCESS -> Icons.Filled.CheckCircle
        InsightType.WARNING -> Icons.Outlined.Lightbulb
        InsightType.INFO    -> Icons.Default.Info
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.bg, RoundedCornerShape(14.dp))
            .border(1.dp, colors.border, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Заголовок: иконка + название + описание
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(colors.iconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.icon,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = insight.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TitleText
                )
                Text(
                    text = insight.description,
                    fontSize = 12.sp,
                    color = BodyText
                )
            }
        }

        // Рекомендация
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x1AFFFFFF), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = null,
                tint = BodyText,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = insight.recommendation,
                fontSize = 12.sp,
                color = BodyText
            )
        }
    }
}

// Карточка "Советы"

private val TipIconBg      = Color(0xFFFAF5FF)
private val TipEffectBg    = Color(0xFFF0FDF4)
private val TipEffectBorder= Color(0xFFB9F8CF)
private val TipEffectText  = Color(0xFF008236)
private val TipPromoBorder = Color(0xFFD1D5DC)

@Composable
private fun TipCard(tip: AiTip, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    val icon: ImageVector = when (tip.category) {
        "Оптимизация" -> Icons.Default.PhoneAndroid
        "Доход"       -> Icons.Default.TrendingUp
        "Цели"        -> Icons.Default.TrackChanges
        "Бюджет"      -> Icons.Default.AccountBalance
        "Расходы"     -> Icons.Default.ShoppingCart
        "Накопления"  -> Icons.Default.Savings
        "Инвестиции"  -> Icons.Default.ShowChart
        else          -> Icons.Default.AutoAwesome
    }

    val colors = LocalAppColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.cardBackground, RoundedCornerShape(14.dp))
            .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(start = 17.dp, top = 17.dp, bottom = 17.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Иконка в фиолетовом боксе
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(TipIconBg, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AiPurple,
                modifier = Modifier.size(24.dp)
            )
        }

        // Текст: категория + заголовок + эффект
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = tip.category,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary
            )
            Text(
                text = tip.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textPrimary
            )
            Box(
                modifier = Modifier
                    .background(TipEffectBg, RoundedCornerShape(8.dp))
                    .border(1.dp, TipEffectBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = tip.effect,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TipEffectText
                )
            }
        }

        // Стрелка вправо
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

// Модальное окно "Советы"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TipDetailBottomSheet(
    tip: AiTip,
    detail: String?,
    isLoading: Boolean,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val maxSheetHeight = LocalConfiguration.current.screenHeightDp.dp * 0.82f

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = LocalAppColors.current.cardBackground,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = maxSheetHeight)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Серая плашка (категория) сверху, зелёная (эффект) под ней
            val sheetColors = LocalAppColors.current
            Box(
                modifier = Modifier
                    .background(sheetColors.inputBackground, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = tip.category,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = sheetColors.textSecondary
                )
            }
            Box(
                modifier = Modifier
                    .background(TipEffectBg, RoundedCornerShape(8.dp))
                    .border(1.dp, TipEffectBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = tip.effect,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TipEffectText
                )
            }

            // Заголовок
            Text(
                text = tip.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = sheetColors.textPrimary
            )

            HorizontalDivider(color = sheetColors.cardBorder)

            // Детали от ИИ
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = AiPurple,
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "ИИ готовит рекомендации...",
                        fontSize = 14.sp,
                        color = sheetColors.textSecondary
                    )
                }
            } else if (detail != null) {
                SimpleMarkdownText(
                    text = detail,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = "Не удалось загрузить детали. Попробуйте снова.",
                    fontSize = 14.sp,
                    color = sheetColors.textSecondary
                )
            }
        }
    }
}

// Упрощенный MarkDown

@Composable
private fun SimpleMarkdownText(text: String, modifier: Modifier = Modifier) {
    val lines = text.split("\n")
    val colors = LocalAppColors.current

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        lines.forEach { line ->
            when {
                line.isBlank() -> Spacer(Modifier.height(4.dp))

                line.startsWith("### ") -> Text(
                    text = parseBold(line.removePrefix("### ")),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    lineHeight = 22.sp
                )

                line.startsWith("## ") -> Text(
                    text = parseBold(line.removePrefix("## ")),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    lineHeight = 24.sp
                )

                line.startsWith("# ") -> Text(
                    text = parseBold(line.removePrefix("# ")),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    lineHeight = 26.sp
                )

                line.startsWith("- ") || line.startsWith("* ") -> {
                    val content = line.removePrefix("- ").removePrefix("* ")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Text(
                            text = "•",
                            fontSize = 14.sp,
                            color = AiPurple,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = parseBold(content),
                            fontSize = 14.sp,
                            color = colors.textSecondary,
                            lineHeight = 22.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                else -> Text(
                    text = parseBold(line),
                    fontSize = 14.sp,
                    color = colors.textSecondary,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

/** Парсит **bold** внутри строки → AnnotatedString с Bold span */
private fun parseBold(input: String): androidx.compose.ui.text.AnnotatedString {
    return androidx.compose.ui.text.buildAnnotatedString {
        val parts = input.split("**")
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) {
                pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold))
                append(part)
                pop()
            } else {
                append(part)
            }
        }
    }
}
