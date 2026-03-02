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
import mobile.tracker.finance.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionBottomSheet(
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        AddTransactionContent(
            onClose = onDismiss,
            onSave = { transaction ->
                onSave(transaction)
                onDismiss()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTransactionContent(
    onClose: () -> Unit,
    onSave: (Transaction) -> Unit
) {
    val context = LocalContext.current

    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<TransactionCategory?>(null) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var comment by remember { mutableStateOf("") }

    val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy", Locale("ru")) }
    val displayDate = remember(selectedDate) { dateFormatter.format(selectedDate.time) }

    Column(modifier = Modifier.fillMaxWidth()) {
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
                color = TextPrimary
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Закрыть",
                    tint = TextSecondary
                )
            }
        }
        HorizontalDivider(color = Color(0xFFE5E7EB))

        // ── Scrollable form ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                    onClick = { selectedType = TransactionType.EXPENSE }
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
                    onClick = { selectedType = TransactionType.INCOME }
                )
            }

            // Amount field
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Сумма (₽)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                TextField(
                    value = amountText,
                    onValueChange = { v ->
                        if (v.isEmpty() || v.matches(Regex("^\\d*\\.?\\d*$"))) amountText = v
                    },
                    placeholder = {
                        Text(
                            text = "0",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF717182)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = InputBackground,
                        focusedContainerColor = InputBackground,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
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
                                .background(Color(0xFFF3F4F6), RoundedCornerShape(10.dp))
                                .clickable { amountText = value.toString() }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
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
                    color = TextPrimary
                )
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .background(InputBackground, RoundedCornerShape(8.dp))
                            .padding(horizontal = 13.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedCategory?.displayName ?: "Выберите категорию",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (selectedCategory != null) TextPrimary else Color(0xFF717182)
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        TransactionCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(text = category.displayName) },
                                onClick = {
                                    selectedCategory = category
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
                    color = TextPrimary
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0x1A000000), RoundedCornerShape(8.dp))
                        .clickable {
                            val cal = selectedDate
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    selectedDate = Calendar.getInstance().apply { set(year, month, day) }
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
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = displayDate,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }
            }

            // Comment
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Комментарий (необязательно)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                TextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = {
                        Text(
                            text = "Добавьте описание операции...",
                            fontSize = 16.sp,
                            color = Color(0xFF717182)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = InputBackground,
                        focusedContainerColor = InputBackground,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
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
        HorizontalDivider(color = Color(0xFFE5E7EB))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 17.dp)
        ) {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0 && selectedCategory != null) {
                        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                        onSave(
                            Transaction(
                                id = UUID.randomUUID().toString(),
                                title = selectedCategory!!.displayName,
                                description = comment,
                                amount = if (selectedType == TransactionType.EXPENSE) -amount else amount,
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
    Box(
        modifier = modifier
            .height(94.dp)
            .background(
                color = if (isSelected) selectedBg else Color.White,
                shape = RoundedCornerShape(14.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) selectedBorder else Color(0xFFE5E7EB),
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
                tint = if (isSelected) selectedIconTint else TextSecondary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) selectedTextColor else Color(0xFF364153)
            )
        }
    }
}

private fun getDateLabel(calendar: Calendar): String {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val sel = (calendar.clone() as Calendar).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val diffDays = ((today.timeInMillis - sel.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
    return when (diffDays) {
        0 -> "Сегодня"
        1 -> "Вчера"
        in 2..6 -> "$diffDays дня назад"
        else -> SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(calendar.time)
    }
}
