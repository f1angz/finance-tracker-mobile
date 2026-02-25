package mobile.tracker.finance.ui.screens.operations

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import mobile.tracker.finance.R
import mobile.tracker.finance.data.models.Transaction
import mobile.tracker.finance.data.models.TransactionFilter
import mobile.tracker.finance.data.models.TransactionGroup
import mobile.tracker.finance.data.models.TransactionType
import mobile.tracker.finance.navigation.Screen
import mobile.tracker.finance.ui.components.BottomNavBar
import mobile.tracker.finance.ui.theme.*
import java.text.DecimalFormat

/**
 * Экран "Операции" — список всех транзакций с фильтрацией и поиском
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperationsScreen(
    navController: NavHostController,
    viewModel: OperationsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { OperationsTopBar() },
        bottomBar = {
            BottomNavBar(
                selectedTab = 1,
                onTabSelected = { tab ->
                    when (tab) {
                        0 -> navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
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
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(Modifier.height(8.dp))

            SearchAndFilterBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged
            )

            Spacer(Modifier.height(12.dp))

            FilterTabsRow(
                activeFilter = uiState.activeFilter,
                onFilterChanged = viewModel::onFilterChanged
            )

            Spacer(Modifier.height(4.dp))

            when {
                uiState.isLoading && uiState.transactionGroups.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }

                !uiState.isLoading && uiState.transactionGroups.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = BottomNavUnselected,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "Операции не найдены",
                                color = TextSecondary,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                else -> {
                    TransactionList(groups = uiState.transactionGroups)
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
private fun OperationsTopBar() {
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
            IconButton(onClick = { /* TODO: Open navigation drawer */ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Меню",
                    tint = TextPrimary
                )
            }
            Text(
                text = "Операции",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box {
                IconButton(onClick = { /* TODO: Open notifications */ }) {
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
                onClick = { /* TODO: Add transaction */ },
                modifier = Modifier.background(PrimaryBlue, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить операцию",
                    tint = White
                )
            }
        }
    }
}

// ─── Search & Filter bar ──────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchAndFilterBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = "Поиск операций...",
                    color = BottomNavUnselected,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = BottomNavUnselected,
                    modifier = Modifier.size(20.dp)
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = InputBackground,
                unfocusedContainerColor = InputBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = PrimaryBlue,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            singleLine = true
        )

        // Filter icon button
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(White)
                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                .clickable { /* TODO: Show advanced filter sheet */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.filter_list),
                contentDescription = "Фильтры",
                tint = TextPrimary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ─── Filter Tabs (Все / Доходы / Расходы) ────────────────────────────────────

@Composable
private fun FilterTabsRow(
    activeFilter: TransactionFilter,
    onFilterChanged: (TransactionFilter) -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChipTab(
            label = "Все",
            isSelected = activeFilter == TransactionFilter.ALL,
            onClick = { onFilterChanged(TransactionFilter.ALL) }
        )
        FilterChipTab(
            label = "Доходы",
            isSelected = activeFilter == TransactionFilter.INCOME,
            onClick = { onFilterChanged(TransactionFilter.INCOME) }
        )
        FilterChipTab(
            label = "Расходы",
            isSelected = activeFilter == TransactionFilter.EXPENSE,
            onClick = { onFilterChanged(TransactionFilter.EXPENSE) }
        )
    }
}

@Composable
private fun FilterChipTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PrimaryBlue else White)
            .then(
                if (!isSelected) Modifier.border(
                    width = 1.dp,
                    color = Color(0xFFE5E7EB),
                    shape = RoundedCornerShape(20.dp)
                ) else Modifier
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) White else TextPrimary,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ─── Transaction List ─────────────────────────────────────────────────────────

@Composable
private fun TransactionList(groups: List<TransactionGroup>) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groups.forEach { group ->
            item(key = "header_${group.dateLabel}") {
                DateSectionHeader(label = group.dateLabel)
            }
            items(items = group.transactions, key = { it.id }) { transaction ->
                OperationsTransactionItem(transaction = transaction)
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun DateSectionHeader(label: String) {
    Text(
        text = label,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = TextSecondary,
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
    )
}

// ─── Transaction Item ─────────────────────────────────────────────────────────

@Composable
private fun OperationsTransactionItem(
    transaction: Transaction,
    modifier: Modifier = Modifier
) {
    val formatter = DecimalFormat("#,###")
    val isIncome = transaction.type == TransactionType.INCOME

    val iconBgColor = if (isIncome) GreenPositive.copy(alpha = 0.1f) else RedNegative.copy(alpha = 0.1f)
    val iconColor = if (isIncome) GreenPositive else RedNegative
    val amountColor = if (isIncome) GreenPositive else RedNegative
    val amountText = "${if (isIncome) "+" else "-"}₽ ${formatter.format(kotlin.math.abs(transaction.amount).toLong())}"
    val typeLabel = if (isIncome) "Доход" else "Расход"
    val badgeBgColor = if (isIncome) GreenPositive.copy(alpha = 0.1f) else RedNegative.copy(alpha = 0.1f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CardBackground, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Иконка типа операции
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        if (isIncome) R.drawable.income_operations else R.drawable.expense_operations
                    ),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Название, описание, бейдж + время
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                if (transaction.description.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = transaction.description,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                Spacer(Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Тип операции — цветной бейдж-чип
                    Box(
                        modifier = Modifier
                            .background(badgeBgColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = typeLabel,
                            fontSize = 12.sp,
                            color = amountColor,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (transaction.time.isNotEmpty()) {
                        Text(
                            text = transaction.time,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(Modifier.width(8.dp))

        // Сумма
        Text(
            text = amountText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }
}
