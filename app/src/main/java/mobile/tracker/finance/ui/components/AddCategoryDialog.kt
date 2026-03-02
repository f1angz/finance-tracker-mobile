package mobile.tracker.finance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import mobile.tracker.finance.data.models.Category
import mobile.tracker.finance.data.models.CategoryType
import java.util.UUID

private val paletteColors = listOf(
    Color(0xFF3B82F6),
    Color(0xFF8B5CF6),
    Color(0xFF10B981),
    Color(0xFFEC4899),
    Color(0xFFF59E0B),
    Color(0xFF6B7280),
    Color(0xFFEF4444)
)

private data class IconOption(val displayName: String, val slug: String)

private val iconOptions = listOf(
    IconOption("Покупки", "products"),
    IconOption("Транспорт", "transport"),
    IconOption("Здоровье", "health"),
    IconOption("Одежда", "clothing"),
    IconOption("Развлечения", "entertainment"),
    IconOption("Коммунальные", "utilities"),
    IconOption("Зарплата", "salary"),
    IconOption("Фриланс", "freelance"),
    IconOption("Прочее", "other")
)

private val CategoryType.label: String
    get() = when (this) {
        CategoryType.EXPENSE -> "Расход"
        CategoryType.INCOME  -> "Доход"
        CategoryType.OTHER   -> "Прочие"
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onSave: (Category) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(CategoryType.EXPENSE) }
    var selectedIcon by remember { mutableStateOf(iconOptions[0]) }
    var selectedColorIndex by remember { mutableStateOf(0) }
    var typeExpanded by remember { mutableStateOf(false) }
    var iconExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color.White,
            shadowElevation = 10.dp
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title
                    Text(
                        text = "Добавить категорию",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0A0A0A),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                    )

                    // Название
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DialogFieldLabel("Название")
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = {
                                Text(
                                    text = "Название категории",
                                    color = Color(0xFF717182),
                                    fontSize = 16.sp
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFF3F3F5),
                                focusedContainerColor = Color(0xFFF3F3F5),
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color(0xFF155DFC)
                            ),
                            singleLine = true
                        )
                    }

                    // Тип
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DialogFieldLabel("Тип")
                        ExposedDropdownMenuBox(
                            expanded = typeExpanded,
                            onExpandedChange = { typeExpanded = it }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .background(Color(0xFFF3F3F5), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 13.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedType.label,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF0A0A0A)
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color(0xFF0A0A0A),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            ExposedDropdownMenu(
                                expanded = typeExpanded,
                                onDismissRequest = { typeExpanded = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                CategoryType.values().forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type.label) },
                                        onClick = {
                                            selectedType = type
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
                        ExposedDropdownMenuBox(
                            expanded = iconExpanded,
                            onExpandedChange = { iconExpanded = it }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .background(Color(0xFFF3F3F5), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 13.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedIcon.displayName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF0A0A0A)
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color(0xFF0A0A0A),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            ExposedDropdownMenu(
                                expanded = iconExpanded,
                                onDismissRequest = { iconExpanded = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                iconOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.displayName) },
                                        onClick = {
                                            selectedIcon = option
                                            iconExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Цвет
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DialogFieldLabel("Цвет")
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            paletteColors.forEachIndexed { index, color ->
                                val isSelected = index == selectedColorIndex
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(color)
                                        .then(
                                            if (isSelected) Modifier.border(
                                                3.dp, Color.White, RoundedCornerShape(10.dp)
                                            ) else Modifier
                                        )
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { selectedColorIndex = index }
                                )
                            }
                        }
                    }

                    // Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF0A0A0A)
                            )
                        ) {
                            Text(
                                text = "Отмена",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    onSave(
                                        Category(
                                            id = UUID.randomUUID().toString(),
                                            name = name.trim(),
                                            slug = selectedIcon.slug,
                                            operationsCount = 0,
                                            totalAmount = 0.0,
                                            type = selectedType
                                        )
                                    )
                                    onDismiss()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF030213),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Создать",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // X close button
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Закрыть",
                        tint = Color(0xFF0A0A0A).copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogFieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF0A0A0A)
    )
}
