package mobile.tracker.finance.ui.screens.ai_assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import mobile.tracker.finance.data.models.AiHealthScore
import mobile.tracker.finance.data.models.AiInsight
import mobile.tracker.finance.data.models.AiTip
import mobile.tracker.finance.data.models.ChatMessage
import mobile.tracker.finance.data.models.InsightType
import mobile.tracker.finance.navigation.Screen
import mobile.tracker.finance.ui.components.BottomNavBar
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

// ─── Экран ───────────────────────────────────────────────────────────────────

@Composable
fun AiAssistantScreen(
    navController: NavHostController,
    viewModel: AiAssistantViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

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
        containerColor = Color(0xFFF9FAFB)
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
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
                AiTab.TIPS -> TipsContent(
                    tips = uiState.tips,
                    isLoading = uiState.isLoadingTips,
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

// ─── Top Bar ─────────────────────────────────────────────────────────────────

@Composable
private fun AiTopBar() {
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
                    text = "ИИ-Помощник",
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
                    onClick = { /* TODO: новый диалог */ },
                    modifier = Modifier.background(PrimaryBlue, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Новый запрос",
                        tint = White
                    )
                }
            }
        }
        HorizontalDivider(color = HeaderBorderColor, thickness = 1.dp)
    }
}

// ─── Info Card ───────────────────────────────────────────────────────────────

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

// ─── Tab Bar ─────────────────────────────────────────────────────────────────

@Composable
private fun AiTabBar(
    selectedTab: AiTab,
    onTabSelected: (AiTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        AiTab.INSIGHTS to "Инсайты",
        AiTab.TIPS to "Советы",
        AiTab.CHAT to "Чат"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(TabBarBg, RoundedCornerShape(14.dp))
            .padding(3.dp)
    ) {
        tabs.forEach { (tab, label) ->
            val isSelected = selectedTab == tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        color = if (isSelected) CardBackground else Color.Transparent,
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
                    color = TitleText
                )
            }
        }
    }
}

// ─── Insights Content ────────────────────────────────────────────────────────

@Composable
private fun InsightsContent(
    insights: List<AiInsight>,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AiPurple)
        }
        return
    }
    if (insights.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("Нет инсайтов", color = SubtextGray, fontSize = 16.sp)
        }
        return
    }
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(insights, key = { it.id }) { insight ->
            InsightCard(insight = insight)
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

// ─── Tips Content ─────────────────────────────────────────────────────────────

@Composable
private fun TipsContent(
    tips: List<AiTip>,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AiPurple)
        }
        return
    }
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tips, key = { it.id }) { tip ->
            TipCard(tip = tip)
        }
        item { TipPromoCard() }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

// ─── Chat Content ────────────────────────────────────────────────────────────

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
        HorizontalDivider(color = HeaderBorderColor, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackground)
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
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                },
                maxLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AiPurple,
                    unfocusedBorderColor = HeaderBorderColor,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
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
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = if (isUser) AiPurple else CardBackground,
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
                        color = Color(0x1A000000),
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
                color = if (isUser) Color.White else TitleText,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun AiTypingIndicator() {
    Row(
        modifier = Modifier
            .background(
                CardBackground,
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
            )
            .border(
                1.dp, Color(0x1A000000),
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
        Text(text = "ИИ печатает...", fontSize = 12.sp, color = SubtextGray)
    }
}

// ─── Insight Card ────────────────────────────────────────────────────────────

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
                .background(Color(0xB3FFFFFF), RoundedCornerShape(10.dp))
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
                color = TitleText
            )
        }
    }
}

// ─── Tip Card ────────────────────────────────────────────────────────────────

private val TipIconBg      = Color(0xFFFAF5FF)
private val TipEffectBg    = Color(0xFFF0FDF4)
private val TipEffectBorder= Color(0xFFB9F8CF)
private val TipEffectText  = Color(0xFF008236)
private val TipPromoBorder = Color(0xFFD1D5DC)

@Composable
private fun TipCard(tip: AiTip, modifier: Modifier = Modifier) {
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

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CardBackground, RoundedCornerShape(14.dp))
            .border(1.dp, Color(0x1A000000), RoundedCornerShape(14.dp))
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

        // Текст: категория/эффект + заголовок
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tip.category,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = SubtextGray
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
            Text(
                text = tip.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TitleText
            )
        }

        // Стрелка вправо
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = SubtextGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ─── Tip Promo Card ───────────────────────────────────────────────────────────

@Composable
private fun TipPromoCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground, RoundedCornerShape(14.dp))
            .border(1.dp, TipPromoBorder, RoundedCornerShape(14.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = AiPurple,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = "Персональные рекомендации",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TitleText,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Получайте умные советы на основе ваших данных",
            fontSize = 12.sp,
            color = SubtextGray,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .background(AiPurple, RoundedCornerShape(8.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* TODO: создать план */ }
                .padding(horizontal = 24.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Создать план",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}
