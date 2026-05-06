package mobile.tracker.finance.ui.screens.categories

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import mobile.tracker.finance.navigation.Screen
import mobile.tracker.finance.data.models.Category
import mobile.tracker.finance.data.models.CategoryFilter
import mobile.tracker.finance.data.models.CategoryType
import mobile.tracker.finance.ui.components.createIconColors
import mobile.tracker.finance.ui.components.createIconVectors
import mobile.tracker.finance.ui.components.AddCategoryDialog
import mobile.tracker.finance.ui.components.AddTransactionBottomSheet
import mobile.tracker.finance.ui.components.BottomNavBar
import mobile.tracker.finance.ui.components.GradientBackground
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

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    LaunchedEffect(currentRoute) {
        if (currentRoute == Screen.Categories.route) {
            viewModel.reloadCategories()
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

    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onSave    = { viewModel.addCategory(it) }
        )
    }

    uiState.editingCategory?.let { category ->
        AddCategoryDialog(
            initialCategory = category,
            onDismiss       = viewModel::onDismissEdit,
            onSave          = viewModel::updateCategory,
            onDelete        = { viewModel.deleteCategory(category.id) }
        )
    }

    GradientBackground {
    Scaffold(
        topBar = { CategoriesTopBar() },
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
        containerColor = Color.Transparent
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

                    CategoryList(
                        categories      = uiState.categories,
                        onCategoryClick = viewModel::onEditCategory
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
private fun CategoriesTopBar() {
    val colors = LocalAppColors.current
    Text(
        text = "Категории",
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
private fun CategoryList(
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit
) {
    LazyColumn(
        contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = categories, key = { it.id }) { category ->
            CategoryCard(
                category        = category,
                onCategoryClick = { onCategoryClick(category) }
            )
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

// ─── Category Card ────────────────────────────────────────────────────────────

@Composable
private fun CategoryCard(
    category: Category,
    onCategoryClick: () -> Unit
) {
    val formatter = DecimalFormat("#,###")
    val amountFormatted = formatter.format(category.totalAmount.toLong())
    val interactionSource = remember { MutableInteractionSource() }

    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.cardBackground, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication        = null,
                onClick           = onCategoryClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Иконка с цветным фоном
        val iconConfig = categoryIconConfig(category.slug, category.name)
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(iconConfig.bgColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconConfig.icon,
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

        // Получено / Потрачено / Оборот — в зависимости от типа категории
        val amountLabel = when (category.type) {
            CategoryType.INCOME  -> "Получено"
            CategoryType.EXPENSE -> "Потрачено"
            CategoryType.OTHER   -> "Оборот"
        }
        val amountColor = when (category.type) {
            CategoryType.INCOME  -> GreenPositive
            CategoryType.EXPENSE -> colors.textPrimary
            CategoryType.OTHER   -> colors.textPrimary
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = amountLabel,
                fontSize = 12.sp,
                color = colors.textSecondary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "₽ $amountFormatted",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}

// ─── Icon config mapping ──────────────────────────────────────────────────────

internal data class CategoryIconConfig(
    val icon: ImageVector,
    val bgColor: Color,
    val iconColor: Color
)

internal fun categoryIconConfig(slug: String, name: String = ""): CategoryIconConfig {
    val slugKey = slug.substringBefore(":").lowercase().trim()
    val nameKey = name.lowercase().trim()

    // Кастомная иконка: slug вида "custom_<vecIdx>_<colorIdx>"
    if (slugKey.startsWith("custom_")) {
        val parts    = slugKey.removePrefix("custom_").split("_")
        val vecIdx   = parts.getOrNull(0)?.toIntOrNull()
        val colorIdx = parts.getOrNull(1)?.toIntOrNull()
        val vector   = vecIdx?.let { createIconVectors.getOrNull(it) }
        val color    = colorIdx?.let { createIconColors.getOrNull(it) }
        if (vector != null && color != null) {
            return CategoryIconConfig(vector, color.copy(alpha = 0.15f), color)
        }
    }

    fun hit(vararg keywords: String) =
        keywords.any { k -> slugKey == k || slugKey.contains(k) || nameKey.contains(k) }

    return when {
        hit("кафе", "ресторан", "cafe", "restaurant", "fastfood", "столов", "обед") ->
            CategoryIconConfig(Icons.Default.Restaurant,     Color(0xFFFFF7ED), Color(0xFFEA580C))

        hit("образован", "school", "учеб", "education", "study", "курс", "универ") ->
            CategoryIconConfig(Icons.Default.School,         Color(0xFFEFF6FF), Color(0xFF2563EB))

        hit("покупк", "shopping", "магазин", "shop", "market") ->
            CategoryIconConfig(Icons.Default.ShoppingCart,   Color(0xFFFAF5FF), Color(0xFF7C3AED))

        hit("перевод", "transfer", "payment", "отправ", "платёж", "платеж") ->
            CategoryIconConfig(Icons.Default.CreditCard,     Color(0xFFF0FDF4), Color(0xFF16A34A))

        hit("products", "продукт", "еда", "food", "groceries", "grocery", "питан") ->
            CategoryIconConfig(Icons.Default.ShoppingCart,   Color(0xFFEEF2FF), Color(0xFF4F46E5))

        hit("transport", "транспорт", "taxi", "такси", "car", "авто", "проезд") ->
            CategoryIconConfig(Icons.Default.DirectionsCar,  Color(0xFFF5F3FF), Color(0xFF7C3AED))

        hit("health", "здоровь", "медицин", "medicine", "аптек", "pharmacy", "doctor", "врач") ->
            CategoryIconConfig(Icons.Default.LocalHospital,  Color(0xFFECFDF5), Color(0xFF10B981))

        hit("clothing", "одежд", "clothes", "обувь", "shoes", "fashion", "гардероб") ->
            CategoryIconConfig(Icons.Default.LocalMall,      Color(0xFFFDF2F8), Color(0xFFEC4899))

        hit("entertainment", "развлечен", "кино", "cinema", "игр", "game", "fun", "досуг", "спорт") ->
            CategoryIconConfig(Icons.Default.Movie,          Color(0xFFFFFBEB), Color(0xFFF59E0B))

        hit("utilities", "коммунал", "bills", "жкх", "связь", "интернет", "internet", "телефон") ->
            CategoryIconConfig(Icons.Default.Build,          Color(0xFFF3F4F6), Color(0xFF6B7280))

        hit("salary", "зарплат", "wage", "оклад") ->
            CategoryIconConfig(Icons.Default.AccountBalance, Color(0xFFECFDF5), Color(0xFF059669))

        hit("freelance", "фриланс", "подработ", "remote", "консалт") ->
            CategoryIconConfig(Icons.Default.Laptop,         Color(0xFFEFF6FF), Color(0xFF3B82F6))

        else ->
            CategoryIconConfig(Icons.Default.MoreHoriz,      Color(0xFFF3F4F6), Color(0xFF9CA3AF))
    }
}
