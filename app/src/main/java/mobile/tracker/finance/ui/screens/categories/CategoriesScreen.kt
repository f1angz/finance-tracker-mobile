package mobile.tracker.finance.ui.screens.categories

import androidx.annotation.DrawableRes
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import mobile.tracker.finance.R
import mobile.tracker.finance.data.models.Category
import mobile.tracker.finance.data.models.CategoryFilter
import mobile.tracker.finance.navigation.Screen
import mobile.tracker.finance.ui.components.AddCategoryDialog
import mobile.tracker.finance.ui.components.AddTransactionBottomSheet
import mobile.tracker.finance.ui.components.BottomNavBar
import mobile.tracker.finance.ui.screens.operations.DraftViewModel
import mobile.tracker.finance.ui.theme.*
import java.text.DecimalFormat

/**
 * Экран "Категории" — сводка по категориям с фильтрацией
 */
@Composable
fun CategoriesScreen(
    navController: NavHostController,
    viewModel: CategoriesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val draftViewModel: DraftViewModel = viewModel(context as ViewModelStoreOwner)
    val draft by draftViewModel.draft.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddTransactionBottomSheet(
            onDismiss    = { showAddDialog = false },
            onSave       = { viewModel.addTransaction(it); draftViewModel.clearDraft() },
            initialDraft = draft,
            onDraftSave  = draftViewModel::saveDraft
        )
    }

    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onSave = { viewModel.addCategory(it) }
        )
    }

    Scaffold(
        topBar = { CategoriesTopBar(onAddClick = { showAddDialog = true }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddCategoryDialog = true },
                containerColor = PrimaryBlue,
                contentColor = White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить категорию"
                )
            }
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = 2,
                onTabSelected = { tab ->
                    when (tab) {
                        0 -> navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                        1 -> navController.navigate(Screen.Operations.route) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(Modifier.height(8.dp))

            CategoryFilterRow(
                activeFilter = uiState.activeFilter,
                counts = uiState.counts,
                onFilterChanged = viewModel::onFilterChanged
            )

            Spacer(Modifier.height(8.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }

                uiState.categories.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Нет категорий",
                            color = LocalAppColors.current.textSecondary,
                            fontSize = 16.sp
                        )
                    }
                }

                else -> {
                    CategoryList(categories = uiState.categories)
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
private fun CategoriesTopBar(onAddClick: () -> Unit) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = { /* TODO: открыть боковое меню */ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Меню",
                    tint = colors.textPrimary
                )
            }
            Text(
                text = "Категории",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box {
                IconButton(onClick = { /* TODO: уведомления */ }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Уведомления",
                        tint = colors.textPrimary
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
                    contentDescription = "Добавить операцию",
                    tint = White
                )
            }
        }
    }
}

// ─── Filter Row ───────────────────────────────────────────────────────────────

@Composable
private fun CategoryFilterRow(
    activeFilter: CategoryFilter,
    counts: Map<CategoryFilter, Int>,
    onFilterChanged: (CategoryFilter) -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CategoryFilterChip(
            label = "Расходы",
            count = counts[CategoryFilter.EXPENSE] ?: 0,
            isSelected = activeFilter == CategoryFilter.EXPENSE,
            onClick = { onFilterChanged(CategoryFilter.EXPENSE) }
        )
        CategoryFilterChip(
            label = "Доходы",
            count = counts[CategoryFilter.INCOME] ?: 0,
            isSelected = activeFilter == CategoryFilter.INCOME,
            onClick = { onFilterChanged(CategoryFilter.INCOME) }
        )
        CategoryFilterChip(
            label = "Прочие",
            count = counts[CategoryFilter.OTHER] ?: 0,
            isSelected = activeFilter == CategoryFilter.OTHER,
            onClick = { onFilterChanged(CategoryFilter.OTHER) }
        )
    }
}

@Composable
private fun CategoryFilterChip(
    label: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val colors = LocalAppColors.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) colors.cardBackground else Color.Transparent)
            .then(
                if (isSelected) Modifier.border(
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
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) colors.textPrimary else colors.textSecondary
        )
        Spacer(Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .background(colors.inputBackground, RoundedCornerShape(10.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary
            )
        }
    }
}

// ─── Category List ────────────────────────────────────────────────────────────

@Composable
private fun CategoryList(categories: List<Category>) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = categories, key = { it.id }) { category ->
            CategoryCard(category = category)
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

// ─── Category Card ────────────────────────────────────────────────────────────

@Composable
private fun CategoryCard(category: Category) {
    val formatter = DecimalFormat("#,###")
    val amountFormatted = formatter.format(category.totalAmount.toLong())

    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.cardBackground, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Иконка с цветным фоном
        val iconConfig = categoryIconConfig(category.slug)
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(iconConfig.bgColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconConfig.iconRes),
                contentDescription = category.name,
                tint = iconConfig.iconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        // Название и кол-во операций
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = category.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${category.operationsCount} операций",
                fontSize = 13.sp,
                color = colors.textSecondary
            )
        }

        Spacer(Modifier.width(8.dp))

        // Потрачено / сумма
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Потрачено",
                fontSize = 12.sp,
                color = colors.textSecondary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "₽ $amountFormatted",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        }
    }
}

// ─── Icon config mapping ──────────────────────────────────────────────────────

private data class CategoryIconConfig(
    @DrawableRes val iconRes: Int,
    val bgColor: Color,
    val iconColor: Color
)

private fun categoryIconConfig(slug: String): CategoryIconConfig {
    val iconKey = slug.substringBefore(":")
    return when (iconKey) {
    "products"      -> CategoryIconConfig(
        iconRes   = R.drawable.ic_cat_products,
        bgColor   = Color(0xFFEEF2FF),
        iconColor = Color(0xFF4F46E5)
    )
    "transport"     -> CategoryIconConfig(
        iconRes   = R.drawable.ic_cat_transport,
        bgColor   = Color(0xFFF5F3FF),
        iconColor = Color(0xFF7C3AED)
    )
    "health"        -> CategoryIconConfig(
        iconRes   = R.drawable.ic_cat_health,
        bgColor   = Color(0xFFECFDF5),
        iconColor = Color(0xFF10B981)
    )
    "clothing"      -> CategoryIconConfig(
        iconRes   = R.drawable.ic_cat_clothing,
        bgColor   = Color(0xFFFDF2F8),
        iconColor = Color(0xFFEC4899)
    )
    "entertainment" -> CategoryIconConfig(
        iconRes   = R.drawable.ic_cat_entertainment,
        bgColor   = Color(0xFFFFFBEB),
        iconColor = Color(0xFFF59E0B)
    )
    "utilities"     -> CategoryIconConfig(
        iconRes   = R.drawable.ic_cat_utilities,
        bgColor   = Color(0xFFF3F4F6),
        iconColor = Color(0xFF6B7280)
    )
    "salary"        -> CategoryIconConfig(
        iconRes   = R.drawable.ic_cat_salary,
        bgColor   = Color(0xFFECFDF5),
        iconColor = Color(0xFF059669)
    )
    "freelance"     -> CategoryIconConfig(
        iconRes   = R.drawable.ic_cat_freelance,
        bgColor   = Color(0xFFEFF6FF),
        iconColor = Color(0xFF3B82F6)
    )
    else            -> CategoryIconConfig(
        iconRes   = R.drawable.ic_cat_other,
        bgColor   = Color(0xFFF3F4F6),
        iconColor = Color(0xFF9CA3AF)
    )
}
}
