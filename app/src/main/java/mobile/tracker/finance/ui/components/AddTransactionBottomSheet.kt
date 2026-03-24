package mobile.tracker.finance.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobile.tracker.finance.data.models.Transaction
import mobile.tracker.finance.data.models.TransactionCategory
import mobile.tracker.finance.data.models.TransactionType
import mobile.tracker.finance.ui.screens.operations.TransactionDraft
import mobile.tracker.finance.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionBottomSheet(
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit,
    initialDraft: TransactionDraft? = null,
    onDraftSave: ((TransactionDraft) -> Unit)? = null
) {

    var selectedType by remember { mutableStateOf(initialDraft?.type ?: TransactionType.EXPENSE) }
    var title by remember { mutableStateOf(initialDraft?.title ?: "") }
    var amountText by remember { mutableStateOf(initialDraft?.amountText ?: "") }
    var selectedCategory by remember { mutableStateOf<TransactionCategory?>(initialDraft?.category) }
    var selectedDate by remember {
        mutableStateOf(Calendar.getInstance().apply {
            timeInMillis = initialDraft?.dateMillis ?: System.currentTimeMillis()
        })
    }
    var comment by remember { mutableStateOf(initialDraft?.comment ?: "") }

    fun buildDraft() = TransactionDraft(
        type       = selectedType,
        title      = title,
        amountText = amountText,
        category   = selectedCategory,
        dateMillis = selectedDate.timeInMillis,
        comment    = comment
    )

    fun handleDismiss() {
        onDraftSave?.invoke(buildDraft())
        onDismiss()
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = { handleDismiss() },
        sheetState = sheetState,
        containerColor = LocalAppColors.current.cardBackground,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        AddTransactionContent(
            selectedType     = selectedType,
            title            = title,
            amountText       = amountText,
            selectedCategory = selectedCategory,
            selectedDate     = selectedDate,
            comment          = comment,
            onTypeChange     = { selectedType = it },
            onTitleChange    = { title = it },
            onAmountChange   = { amountText = it },
            onCategoryChange = { selectedCategory = it },
            onDateChange     = { selectedDate = it },
            onCommentChange  = { comment = it },
            onClose          = { handleDismiss() },
            onSave           = { transaction ->
                onSave(transaction)
                onDismiss()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTransactionContent(
    selectedType: TransactionType,
    title: String,
    amountText: String,
    selectedCategory: TransactionCategory?,
    selectedDate: Calendar,
    comment: String,
    onTypeChange: (TransactionType) -> Unit,
    onTitleChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onCategoryChange: (TransactionCategory?) -> Unit,
    onDateChange: (Calendar) -> Unit,
    onCommentChange: (String) -> Unit,
    onClose: () -> Unit,
    onSave: (Transaction) -> Unit
) {
    val context = LocalContext.current
    val colors = LocalAppColors.current
    var categoryExpanded by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy", Locale("ru")) }
    val displayDate = remember(selectedDate) { dateFormatter.format(selectedDate.time) }

    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.93f)) {
        // ── Header ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Новая операция",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Закрыть",
                    tint = colors.textSecondary
                )
            }
        }
        HorizontalDivider(color = colors.cardBorder)

        // ── Scrollable form ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Type toggle: Расход | Доход
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TransactionTypeButton(
                    label = "Расход",
                    icon = Icons.Default.TrendingDown,
                    isSelected = selectedType == TransactionType.EXPENSE,
                    selectedBg = Color(0xFFFEF2F2),
                    selectedBorder = Color(0xFFFB2C36),
                    selectedIconTint = Color(0xFFE7000B),
                    selectedTextColor = Color(0xFF82181A),
                    modifier = Modifier.weight(1f),
                    onClick = { onTypeChange(TransactionType.EXPENSE) }
                )
                TransactionTypeButton(
                    label = "Доход",
                    icon = Icons.Default.TrendingUp,
                    isSelected = selectedType == TransactionType.INCOME,
                    selectedBg = Color(0xFFF0FDF4),
                    selectedBorder = Color(0xFF00A63E),
                    selectedIconTint = Color(0xFF00A63E),
                    selectedTextColor = Color(0xFF008236),
                    modifier = Modifier.weight(1f),
                    onClick = { onTypeChange(TransactionType.INCOME) }
                )
            }

            // Title field
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Название",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary
                )
                TextField(
                    value = title,
                    onValueChange = onTitleChange,
                    placeholder = {
                        Text(
                            text = "Введите название операции",
                            fontSize = 16.sp,
                            color = colors.textSecondary
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = colors.inputBackground,
                        focusedContainerColor = colors.inputBackground,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedTextColor = colors.textPrimary,
                        focusedTextColor = colors.textPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            }

            // Amount field
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Сумма (₽)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary
                )
                TextField(
                    value = amountText,
                    onValueChange = { v ->
                        if (v.isEmpty() || v.matches(Regex("^\\d*\\.?\\d*$"))) onAmountChange(v)
                    },
                    placeholder = {
                        Text(
                            text = "0",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textSecondary
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = colors.inputBackground,
                        focusedContainerColor = colors.inputBackground,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedTextColor = colors.textPrimary,
                        focusedTextColor = colors.textPrimary
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
                // Quick amount buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(500 to "500", 1000 to "1К", 2000 to "2К", 5000 to "5К").forEach { (value, label) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(colors.inputBackground, RoundedCornerShape(10.dp))
                                .clickable { onAmountChange(value.toString()) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colors.textPrimary
                            )
                        }
                    }
                }
            }

            // Category dropdown
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Категория",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary
                )
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .background(colors.inputBackground, RoundedCornerShape(8.dp))
                            .padding(horizontal = 13.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedCategory?.displayName ?: "Выберите категорию",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (selectedCategory != null) colors.textPrimary else colors.textSecondary
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        modifier = Modifier.background(colors.cardBackground)
                    ) {
                        TransactionCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(text = category.displayName) },
                                onClick = {
                                    onCategoryChange(category)
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Date picker
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Дата",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(8.dp))
                        .clickable {
                            val cal = selectedDate
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    onDateChange(Calendar.getInstance().apply { set(year, month, day) })
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                        .padding(horizontal = 13.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = displayDate,
                        fontSize = 14.sp,
                        color = colors.textPrimary
                    )
                }
            }

            // Comment
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Комментарий (необязательно)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary
                )
                TextField(
                    value = comment,
                    onValueChange = onCommentChange,
                    placeholder = {
                        Text(
                            text = "Добавьте описание операции...",
                            fontSize = 16.sp,
                            color = colors.textSecondary
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = colors.inputBackground,
                        focusedContainerColor = colors.inputBackground,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedTextColor = colors.textPrimary,
                        focusedTextColor = colors.textPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    minLines = 3,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        // ── Footer ──
        HorizontalDivider(color = colors.cardBorder)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 17.dp)
        ) {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0 && selectedCategory != null && title.isNotBlank()) {
                        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                        onSave(
                            Transaction(
                                id = "",
                                title = title.trim(),
                                description = comment,
                                amount = amount,
                                category = selectedCategory!!,
                                date = getDateLabel(selectedDate),
                                time = timeFormatter.format(Calendar.getInstance().time),
                                type = selectedType
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(
                    text = "Сохранить операцию",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun TransactionTypeButton(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    selectedBg: Color,
    selectedBorder: Color,
    selectedIconTint: Color,
    selectedTextColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = LocalAppColors.current
    Box(
        modifier = modifier
            .height(94.dp)
            .background(
                color = if (isSelected) selectedBg else colors.cardBackground,
                shape = RoundedCornerShape(14.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) selectedBorder else colors.cardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) selectedIconTint else colors.textSecondary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) selectedTextColor else colors.textSecondary
            )
        }
    }
}

private fun getDateLabel(calendar: Calendar): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
}
