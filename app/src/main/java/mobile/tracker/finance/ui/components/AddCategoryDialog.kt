package mobile.tracker.finance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.window.Dialog
import mobile.tracker.finance.data.models.Category
import mobile.tracker.finance.data.models.CategoryType
import mobile.tracker.finance.ui.theme.LocalAppColors
import mobile.tracker.finance.ui.theme.PrimaryBlue
import mobile.tracker.finance.ui.theme.RedNegative
import java.util.UUID

// ─── Иконка: может быть drawable-ресурс или Material vector ──────────────────

private data class IconDef(
    val slug: String,
    val iconVector: ImageVector,
    val bgColor: Color,
    val iconColor: Color
)

// ─── Пресет-иконки ────────────────────────────────────────────────────────────

private val allPresetIconDefs = listOf(
    IconDef("products",      Icons.Default.ShoppingCart,   Color(0xFFEEF2FF), Color(0xFF4F46E5)),
    IconDef("transport",     Icons.Default.DirectionsCar,  Color(0xFFF5F3FF), Color(0xFF7C3AED)),
    IconDef("health",        Icons.Default.LocalHospital,  Color(0xFFECFDF5), Color(0xFF10B981)),
    IconDef("clothing",      Icons.Default.LocalMall,      Color(0xFFFDF2F8), Color(0xFFEC4899)),
    IconDef("entertainment", Icons.Default.Movie,          Color(0xFFFFFBEB), Color(0xFFF59E0B)),
    IconDef("utilities",     Icons.Default.Build,          Color(0xFFF3F4F6), Color(0xFF6B7280)),
    IconDef("salary",        Icons.Default.AccountBalance, Color(0xFFECFDF5), Color(0xFF059669)),
    IconDef("freelance",     Icons.Default.Laptop,         Color(0xFFEFF6FF), Color(0xFF3B82F6)),
    IconDef("other",         Icons.Default.MoreHoriz,      Color(0xFFF3F4F6), Color(0xFF9CA3AF)),
)

private fun presetIconsForType(type: CategoryType): List<IconDef> = when (type) {
    CategoryType.EXPENSE -> allPresetIconDefs.filter {
        it.slug in listOf("products", "transport", "health", "clothing", "entertainment", "utilities", "other")
    }
    CategoryType.INCOME -> allPresetIconDefs.filter {
        it.slug in listOf("salary", "freelance", "other")
    }
    CategoryType.OTHER -> allPresetIconDefs
}

// ─── Цвета и иконки для "Создать иконку" ─────────────────────────────────────

internal val createIconColors = listOf(
    Color(0xFF3B82F6), Color(0xFF8B5CF6), Color(0xFF10B981),
    Color(0xFFEC4899), Color(0xFFF59E0B), Color(0xFF6B7280),
    Color(0xFFEF4444), Color(0xFFF97316), Color(0xFF14B8A6),
    Color(0xFF06B6D4), Color(0xFFEAB308), Color(0xFF84CC16),
)

internal val createIconVectors: List<ImageVector> = listOf(
    Icons.Default.ShoppingCart,
    Icons.Default.Favorite,
    Icons.Default.Star,
    Icons.Default.Home,
    Icons.Default.Phone,
    Icons.Default.Email,
    Icons.Default.Person,
    Icons.Default.Work,
    Icons.Default.School,
    Icons.Default.LocalHospital,
    Icons.Default.DirectionsCar,
    Icons.Default.Restaurant,
    Icons.Default.AttachMoney,
    Icons.Default.AccountBalance,
    Icons.Default.CreditCard,
    Icons.Default.Savings,
    Icons.Default.TrendingUp,
    Icons.Default.Flight,
    Icons.Default.Train,
    Icons.Default.DirectionsBus,
    Icons.Default.MusicNote,
    Icons.Default.Movie,
    Icons.Default.SportsEsports,
    Icons.Default.FitnessCenter,
    Icons.Default.Spa,
    Icons.Default.LocalCafe,
    Icons.Default.Fastfood,
    Icons.Default.Pets,
    Icons.Default.Build,
    Icons.Default.LocalMall,
    Icons.Default.BeachAccess,
    Icons.Default.BusinessCenter,
    Icons.Default.CameraAlt,
    Icons.Default.Laptop,
    Icons.Default.Code,
)

// ─── Метка типа ───────────────────────────────────────────────────────────────

private val CategoryType.label: String
    get() = when (this) {
        CategoryType.EXPENSE -> "Расход"
        CategoryType.INCOME  -> "Доход"
        CategoryType.OTHER   -> "Прочие"
    }

// ─── Диалог "Добавить категорию" ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onSave: (Category) -> Unit,
    initialCategory: Category? = null,
    onDelete: (() -> Unit)? = null
) {
    val isEditing = initialCategory != null

    val initialBaseSlug = initialCategory?.slug?.substringBefore(":")
    var name by remember { mutableStateOf(initialCategory?.name ?: "") }
    var selectedType by remember {
        val initial = initialCategory?.type ?: CategoryType.EXPENSE
        mutableStateOf(if (initial == CategoryType.OTHER) CategoryType.EXPENSE else initial)
    }
    var selectedIcon by remember {
        mutableStateOf(
            if (initialBaseSlug != null)
                allPresetIconDefs.find { it.slug == initialBaseSlug } ?: allPresetIconDefs.last()
            else
                allPresetIconDefs.first { it.slug == "products" }
        )
    }
    var typeExpanded by remember { mutableStateOf(false) }
    var showCreateIconDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var customIconEntries by remember { mutableStateOf(listOf<Pair<IconDef, CategoryType>>()) }

    val displayIcons = presetIconsForType(selectedType) +
        customIconEntries.filter { it.second == selectedType }.map { it.first }

    if (showCreateIconDialog) {
        CreateIconDialog(
            initialType = if (selectedType == CategoryType.OTHER) CategoryType.EXPENSE else selectedType,
            onDismiss = { showCreateIconDialog = false },
            onConfirm = { color, vector, type ->
                val vecIdx   = createIconVectors.indexOfFirst { it.name == vector.name }.coerceAtLeast(0)
                val colorIdx = createIconColors.indexOf(color).coerceAtLeast(0)
                val newIcon = IconDef(
                    slug       = "custom_${vecIdx}_${colorIdx}",
                    iconVector = vector,
                    bgColor    = color.copy(alpha = 0.15f),
                    iconColor  = color
                )
                customIconEntries = customIconEntries + (newIcon to type)
                selectedIcon = newIcon
                selectedType = type
                showCreateIconDialog = false
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor   = LocalAppColors.current.cardBackground,
            title = {
                Text(
                    "Удалить категорию?",
                    fontWeight = FontWeight.SemiBold,
                    color = LocalAppColors.current.textPrimary
                )
            },
            text = {
                Text(
                    "«${initialCategory?.name}» будет удалена. Это действие нельзя отменить.",
                    color = LocalAppColors.current.textSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { onDelete?.invoke(); showDeleteConfirm = false; onDismiss() },
                    colors = ButtonDefaults.buttonColors(containerColor = RedNegative)
                ) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Отмена", color = LocalAppColors.current.textSecondary)
                }
            }
        )
    }

    val colors = LocalAppColors.current
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape          = RoundedCornerShape(10.dp),
            color          = colors.cardBackground,
            shadowElevation = 10.dp
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text       = if (isEditing) "Редактировать категорию" else "Добавить категорию",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = colors.textPrimary,
                        textAlign  = TextAlign.Center,
                        modifier   = Modifier.fillMaxWidth().padding(end = 16.dp)
                    )

                    // Название
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DialogFieldLabel("Название")
                        OutlinedTextField(
                            value         = name,
                            onValueChange = { name = it },
                            placeholder   = {
                                Text("Название категории", color = colors.textSecondary, fontSize = 16.sp)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(8.dp),
                            colors   = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = colors.inputBackground,
                                focusedContainerColor   = colors.inputBackground,
                                unfocusedBorderColor    = Color.Transparent,
                                focusedBorderColor      = PrimaryBlue,
                                unfocusedTextColor      = colors.textPrimary,
                                focusedTextColor        = colors.textPrimary
                            ),
                            singleLine = true
                        )
                    }

                    // Тип
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DialogFieldLabel("Тип")
                        ExposedDropdownMenuBox(
                            expanded        = typeExpanded,
                            onExpandedChange = { typeExpanded = it }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .background(colors.inputBackground, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 13.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Text(selectedType.label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.textPrimary)
                                Icon(Icons.Default.KeyboardArrowDown, null, tint = colors.textPrimary, modifier = Modifier.size(16.dp))
                            }
                            ExposedDropdownMenu(
                                expanded        = typeExpanded,
                                onDismissRequest = { typeExpanded = false },
                                modifier        = Modifier.background(colors.cardBackground)
                            ) {
                                listOf(CategoryType.EXPENSE, CategoryType.INCOME).forEach { type ->
                                    DropdownMenuItem(
                                        text    = { Text(type.label, color = colors.textPrimary) },
                                        onClick = {
                                            selectedType = type
                                            val newDisplay = presetIconsForType(type) +
                                                customIconEntries.filter { it.second == type }.map { it.first }
                                            if (selectedIcon !in newDisplay) selectedIcon = newDisplay.first()
                                            typeExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Иконка
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DialogFieldLabel("Иконка")
                        IconPickerGrid(
                            icons          = displayIcons,
                            selectedSlug   = selectedIcon.slug,
                            onIconSelected = { selectedIcon = it }
                        )
                        OutlinedButton(
                            onClick  = { showCreateIconDialog = true },
                            modifier = Modifier.fillMaxWidth().height(36.dp),
                            shape    = RoundedCornerShape(8.dp),
                            border   = ButtonDefaults.outlinedButtonBorder,
                            colors   = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Создать иконку", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    // Кнопки сохранить / отмена
                    Row(
                        modifier              = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick  = onDismiss,
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape    = RoundedCornerShape(8.dp),
                            colors   = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)
                        ) {
                            Text("Отмена", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    onSave(
                                        Category(
                                            id              = initialCategory?.id ?: UUID.randomUUID().toString(),
                                            name            = name.trim(),
                                            slug            = "${selectedIcon.slug}:${UUID.randomUUID().toString().take(8)}",
                                            operationsCount = initialCategory?.operationsCount ?: 0,
                                            totalAmount     = initialCategory?.totalAmount ?: 0.0,
                                            type            = selectedType
                                        )
                                    )
                                    onDismiss()
                                }
                            },
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape    = RoundedCornerShape(8.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = Color.White)
                        ) {
                            Text(
                                if (isEditing) "Сохранить" else "Создать",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Кнопка удаления — только в режиме редактирования
                    if (isEditing && onDelete != null) {
                        OutlinedButton(
                            onClick  = { showDeleteConfirm = true },
                            modifier = Modifier.fillMaxWidth().height(36.dp),
                            shape    = RoundedCornerShape(8.dp),
                            border   = BorderStroke(1.dp, RedNegative.copy(alpha = 0.5f)),
                            colors   = ButtonDefaults.outlinedButtonColors(contentColor = RedNegative)
                        ) {
                            Icon(Icons.Default.Delete, null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Удалить категорию", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                IconButton(
                    onClick  = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(32.dp)
                ) {
                    Icon(Icons.Default.Close, "Закрыть", tint = colors.textSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// ─── Диалог "Создать иконку" ──────────────────────────────────────────────────

@Composable
private fun CreateIconDialog(
    initialType: CategoryType,
    onDismiss: () -> Unit,
    onConfirm: (color: Color, vector: ImageVector, type: CategoryType) -> Unit
) {
    var selectedColor by remember { mutableStateOf(createIconColors[0]) }
    var selectedVector by remember { mutableStateOf(createIconVectors[0]) }
    var selectedType by remember { mutableStateOf(initialType) }

    val colors = LocalAppColors.current
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = colors.cardBackground,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Заголовок
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Создать иконку",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterEnd).size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, null, tint = colors.textSecondary, modifier = Modifier.size(16.dp))
                    }
                }

                // Тип: Расход / Доход
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DialogFieldLabel("Тип")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.inputBackground, RoundedCornerShape(8.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(CategoryType.EXPENSE to "Расход", CategoryType.INCOME to "Доход").forEach { (type, label) ->
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
                                    text = label,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else colors.textSecondary
                                )
                            }
                        }
                    }
                }

                // Цвет
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DialogFieldLabel("Цвет")
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        createIconColors.chunked(6).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { color ->
                                    val isSelected = color == selectedColor
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(color)
                                            .then(
                                                if (isSelected) Modifier.border(3.dp, colors.cardBackground, RoundedCornerShape(8.dp))
                                                else Modifier
                                            )
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) { selectedColor = color }
                                    )
                                }
                            }
                        }
                    }
                }

                // Иконки (прокручиваемая сетка)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DialogFieldLabel("Иконка")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(188.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            createIconVectors.chunked(5).forEach { row ->
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    row.forEach { vector ->
                                        val isSelected = vector == selectedVector
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(selectedColor.copy(alpha = 0.15f))
                                                .then(
                                                    if (isSelected) Modifier.border(2.dp, PrimaryBlue, RoundedCornerShape(10.dp))
                                                    else Modifier
                                                )
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) { selectedVector = vector },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = vector,
                                                contentDescription = null,
                                                tint = selectedColor,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Предпросмотр
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Предпросмотр:", fontSize = 13.sp, color = colors.textSecondary)
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(selectedColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = selectedVector,
                            contentDescription = null,
                            tint = selectedColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Кнопки
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)
                    ) {
                        Text("Отмена", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Button(
                        onClick = { onConfirm(selectedColor, selectedVector, selectedType) },
                        modifier = Modifier.weight(1f).height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = Color.White)
                    ) {
                        Text("Создать", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

// ─── Сетка иконок ─────────────────────────────────────────────────────────────

@Composable
private fun IconPickerGrid(
    icons: List<IconDef>,
    selectedSlug: String,
    onIconSelected: (IconDef) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        icons.chunked(5).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { icon ->
                    val isSelected = icon.slug == selectedSlug
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(icon.bgColor)
                            .then(
                                if (isSelected) Modifier.border(2.dp, PrimaryBlue, RoundedCornerShape(10.dp))
                                else Modifier
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onIconSelected(icon) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon.iconVector,
                            contentDescription = null,
                            tint = icon.iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── Метка поля ───────────────────────────────────────────────────────────────

@Composable
private fun DialogFieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = LocalAppColors.current.textPrimary
    )
}
