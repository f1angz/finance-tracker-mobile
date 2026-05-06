package mobile.tracker.finance.ui.screens.operations

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.async
import mobile.tracker.finance.R
import mobile.tracker.finance.data.models.Category
import mobile.tracker.finance.data.models.CategoryFilter
import mobile.tracker.finance.data.models.Transaction
import mobile.tracker.finance.data.models.TransactionFilter
import mobile.tracker.finance.data.models.TransactionGroup
import mobile.tracker.finance.data.models.TransactionType
import mobile.tracker.finance.data.repository.ApiCategoryRepository
import mobile.tracker.finance.navigation.Screen
import mobile.tracker.finance.ui.components.AddTransactionBottomSheet
import mobile.tracker.finance.ui.components.BottomNavBar
import mobile.tracker.finance.ui.components.GradientBackground
import mobile.tracker.finance.ui.components.TransactionDetailBottomSheet
import mobile.tracker.finance.ui.screens.categories.categoryIconConfig
import mobile.tracker.finance.ui.theme.*
import mobile.tracker.finance.utils.Result
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

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
    val context = LocalContext.current
    val draftViewModel: DraftViewModel = viewModel(context as ViewModelStoreOwner)
    val draft by draftViewModel.draft.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }

    if (showAddDialog) {
        AddTransactionBottomSheet(
            onDismiss    = { showAddDialog = false },
            onSave       = { viewModel.addTransaction(it); draftViewModel.clearDraft() },
            initialDraft = draft,
            onDraftSave  = draftViewModel::saveDraft
        )
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            currentFilter = uiState.extraFilter,
            onApply       = { viewModel.onExtraFilterChanged(it); showFilterSheet = false },
            onDismiss     = { showFilterSheet = false }
        )
    }

    selectedTransaction?.let { transaction ->
        TransactionDetailBottomSheet(
            transaction = transaction,
            onDismiss = { selectedTransaction = null },
            onDelete = { id ->
                viewModel.deleteTransaction(id)
                selectedTransaction = null
            }
        )
    }

    GradientBackground {
    Scaffold(
        topBar = { OperationsTopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryBlue,
                contentColor = White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить операцию"
                )
            }
        },
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
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(Modifier.height(8.dp))

            SearchAndFilterBar(
                query            = uiState.searchQuery,
                onQueryChange    = viewModel::onSearchQueryChanged,
                hasActiveFilters = uiState.extraFilter.isActive,
                onFilterClick    = { showFilterSheet = true }
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
                                color = LocalAppColors.current.textSecondary,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                else -> {
                    TransactionList(
                        groups = uiState.transactionGroups,
                        onTransactionClick = { selectedTransaction = it }
                    )
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
}

// ─── Top Bar ─────────────────────────────────────────────────────────────────

@Composable
private fun OperationsTopBar() {
    val colors = LocalAppColors.current
    Text(
        text = "Операции",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = colors.textPrimary,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

// ─── Search & Filter bar ──────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchAndFilterBar(
    query: String,
    onQueryChange: (String) -> Unit,
    hasActiveFilters: Boolean,
    onFilterClick: () -> Unit
) {
    val colors = LocalAppColors.current
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
                Text(text = "Поиск операций...", color = BottomNavUnselected, fontSize = 14.sp)
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
                focusedContainerColor   = colors.inputBackground,
                unfocusedContainerColor = colors.inputBackground,
                focusedIndicatorColor   = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor  = Color.Transparent,
                cursorColor             = PrimaryBlue,
                focusedTextColor        = colors.textPrimary,
                unfocusedTextColor      = colors.textPrimary
            ),
            modifier  = Modifier.weight(1f).height(50.dp),
            singleLine = true
        )

        // Кнопка фильтра — с точкой-индикатором, если есть активные фильтры
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (hasActiveFilters) PrimaryBlue else colors.cardBackground)
                .border(
                    1.dp,
                    if (hasActiveFilters) PrimaryBlue else colors.cardBorder,
                    RoundedCornerShape(12.dp)
                )
                .clickable(onClick = onFilterClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.filter_list),
                contentDescription = "Фильтры",
                tint = if (hasActiveFilters) White else colors.textPrimary,
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

    val colors = LocalAppColors.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PrimaryBlue else colors.cardBackground)
            .then(
                if (!isSelected) Modifier.border(
                    width = 1.dp,
                    color = colors.cardBorder,
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
private fun TransactionList(
    groups: List<TransactionGroup>,
    onTransactionClick: (Transaction) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groups.forEach { group ->
            item(key = "header_${group.dateLabel}") {
                DateSectionHeader(label = group.dateLabel)
            }
            items(items = group.transactions, key = { it.id }) { transaction ->
                OperationsTransactionItem(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction) }
                )
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
        color = LocalAppColors.current.textSecondary,
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
    )
}

// ─── Transaction Item ─────────────────────────────────────────────────────────

@Composable
private fun OperationsTransactionItem(
    transaction: Transaction,
    onClick: () -> Unit,
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

    val colors = LocalAppColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.cardBackground, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
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
                    color = LocalAppColors.current.textPrimary
                )

                if (transaction.description.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = transaction.description,
                        fontSize = 13.sp,
                        color = LocalAppColors.current.textSecondary
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
                            color = LocalAppColors.current.textSecondary
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

// ─── Filter Bottom Sheet ──────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    currentFilter: OperationsExtraFilter,
    onApply: (OperationsExtraFilter) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val colors  = LocalAppColors.current

    var datePreset   by remember { mutableStateOf(currentFilter.datePreset) }
    var dateFrom     by remember { mutableStateOf(currentFilter.dateFrom) }
    var dateTo       by remember { mutableStateOf(currentFilter.dateTo) }
    var categorySlug by remember { mutableStateOf(currentFilter.categorySlug) }
    var amountMinText by remember { mutableStateOf(currentFilter.amountMin?.toLong()?.toString() ?: "") }
    var amountMaxText by remember { mutableStateOf(currentFilter.amountMax?.toLong()?.toString() ?: "") }

    var allCategories by remember { mutableStateOf<List<Category>>(emptyList()) }
    LaunchedEffect(Unit) {
        val repo             = ApiCategoryRepository()
        val expenseDeferred  = async { repo.getCategories(CategoryFilter.EXPENSE) }
        val incomeDeferred   = async { repo.getCategories(CategoryFilter.INCOME) }
        val list = mutableListOf<Category>()
        val exp = expenseDeferred.await(); if (exp is Result.Success) list += exp.data
        val inc = incomeDeferred.await();  if (inc is Result.Success) list += inc.data
        allCategories = list
    }

    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    fun buildFilter() = OperationsExtraFilter(
        datePreset   = datePreset,
        dateFrom     = if (datePreset == DatePreset.CUSTOM) dateFrom else null,
        dateTo       = if (datePreset == DatePreset.CUSTOM) dateTo   else null,
        categorySlug = categorySlug,
        amountMin    = amountMinText.toDoubleOrNull(),
        amountMax    = amountMaxText.toDoubleOrNull()
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor   = colors.cardBackground,
        dragHandle       = null,
        shape            = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            // ── Header ────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = "Фильтры",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = colors.textPrimary
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Close, null, tint = colors.textSecondary)
                }
            }
            HorizontalDivider(color = colors.cardBorder)

            // ── Scrollable content ────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // ── Период ────────────────────────────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Период", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
                    val presets = DatePreset.entries
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        presets.chunked(3).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { preset ->
                                    val selected = datePreset == preset
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (selected) PrimaryBlue else colors.inputBackground)
                                            .clickable { datePreset = preset }
                                            .padding(vertical = 9.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text       = preset.label,
                                            fontSize   = 13.sp,
                                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                            color      = if (selected) White else colors.textSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Кастомный период — два date picker
                    if (datePreset == DatePreset.CUSTOM) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "От"  to dateFrom,
                                "До" to dateTo
                            ).forEach { (label, millis) ->
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(1.dp, colors.cardBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            val cal = Calendar.getInstance().apply {
                                                if (millis != null) timeInMillis = millis
                                            }
                                            DatePickerDialog(
                                                context,
                                                { _, y, m, d ->
                                                    val picked = Calendar.getInstance()
                                                        .apply { set(y, m, d) }.timeInMillis
                                                    if (label == "От") dateFrom = picked
                                                    else               dateTo   = picked
                                                },
                                                cal.get(Calendar.YEAR),
                                                cal.get(Calendar.MONTH),
                                                cal.get(Calendar.DAY_OF_MONTH)
                                            ).show()
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment     = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.DateRange, null,
                                        tint     = colors.textSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text     = if (millis != null) dateFormatter.format(Date(millis)) else label,
                                        fontSize = 13.sp,
                                        color    = if (millis != null) colors.textPrimary else colors.textSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Категория ─────────────────────────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Категория", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Чип "Все"
                        item {
                            val selected = categorySlug == null
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (selected) PrimaryBlue else colors.inputBackground)
                                    .clickable { categorySlug = null }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text       = "Все",
                                    fontSize   = 13.sp,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                    color      = if (selected) White else colors.textSecondary
                                )
                            }
                        }
                        items(allCategories) { category ->
                            val selected = categorySlug == category.slug
                            val cfg      = categoryIconConfig(category.slug, category.name)
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (selected) PrimaryBlue else colors.inputBackground)
                                    .clickable { categorySlug = category.slug }
                                    .padding(start = 6.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            if (selected) White.copy(alpha = 0.25f) else cfg.bgColor,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = cfg.icon,
                                        contentDescription = null,
                                        tint     = if (selected) White else cfg.iconColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text       = category.name,
                                    fontSize   = 13.sp,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                    color      = if (selected) White else colors.textSecondary
                                )
                            }
                        }
                    }
                }

                // ── Сумма ─────────────────────────────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Сумма (₽)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf(
                            "От"  to amountMinText,
                            "До" to amountMaxText
                        ).forEachIndexed { idx, (label, value) ->
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(label, fontSize = 12.sp, color = colors.textSecondary)
                                TextField(
                                    value         = value,
                                    onValueChange = { v ->
                                        if (v.isEmpty() || v.matches(Regex("^\\d*$"))) {
                                            if (idx == 0) amountMinText = v else amountMaxText = v
                                        }
                                    },
                                    placeholder   = { Text("0", color = colors.textSecondary) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine    = true,
                                    colors        = TextFieldDefaults.colors(
                                        unfocusedContainerColor = colors.inputBackground,
                                        focusedContainerColor   = colors.inputBackground,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedIndicatorColor   = Color.Transparent,
                                        unfocusedTextColor      = colors.textPrimary,
                                        focusedTextColor        = colors.textPrimary
                                    ),
                                    shape    = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().height(56.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))
            }

            // ── Footer ────────────────────────────────────────────────────────
            HorizontalDivider(color = colors.cardBorder)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick  = {
                        onApply(OperationsExtraFilter())
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape    = RoundedCornerShape(8.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)
                ) {
                    Text("Сбросить", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Button(
                    onClick  = { onApply(buildFilter()) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape    = RoundedCornerShape(8.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Применить", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = White)
                }
            }
        }
    }
}
