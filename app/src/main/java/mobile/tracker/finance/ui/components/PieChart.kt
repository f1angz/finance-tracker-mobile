package mobile.tracker.finance.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobile.tracker.finance.data.models.CategoryExpense
import mobile.tracker.finance.ui.theme.LocalAppColors
import java.text.DecimalFormat

// Палитра для диаграммы — достаточно цветов для любого числа категорий
internal val pieChartColors = listOf(
    Color(0xFF4F46E5),
    Color(0xFF10B981),
    Color(0xFFEC4899),
    Color(0xFFF59E0B),
    Color(0xFF3B82F6),
    Color(0xFF7C3AED),
    Color(0xFFEF4444),
    Color(0xFF14B8A6),
    Color(0xFF8B5CF6),
    Color(0xFF06B6D4),
    Color(0xFFEA580C),
    Color(0xFF65A30D),
)

private const val MAX_SLICES = 6

/**
 * Секция круговой диаграммы расходов по категориям.
 * Топ-[MAX_SLICES] категорий, остаток — "Прочие".
 */
@Composable
fun PieChartSection(
    expenses: List<CategoryExpense>,
    modifier: Modifier = Modifier
) {
    if (expenses.isEmpty()) return

    val colors = LocalAppColors.current
    val fmt    = DecimalFormat("#,###")

    // Ограничиваем до MAX_SLICES, остаток объединяем
    val sorted  = expenses.sortedByDescending { it.amount }
    val topN    = sorted.take(MAX_SLICES)
    val rest    = sorted.drop(MAX_SLICES)
    val display = if (rest.isEmpty()) topN
    else topN + CategoryExpense(
        categoryName = "Прочие",
        amount       = rest.sumOf { it.amount },
        percentage   = rest.sumOf { it.percentage.toDouble() }.toFloat()
    )

    val total = display.sumOf { it.amount }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.cardBackground, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text       = "Расходы по категориям",
                fontSize   = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color      = colors.textPrimary
            )

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val chartSize = (maxWidth * 0.38f).coerceIn(80.dp, 150.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Donut chart
                    Box(
                        modifier         = Modifier.size(chartSize),
                        contentAlignment = Alignment.Center
                    ) {
                        DonutChart(slices = display, chartSize = chartSize)
                        // Сумма в центре
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text       = "₽${fmt.format(total.toLong())}",
                                fontSize   = (chartSize.value * 0.1f).coerceIn(9f, 14f).sp,
                                fontWeight = FontWeight.Bold,
                                color      = colors.textPrimary
                            )
                            Text(
                                text     = "всего",
                                fontSize = (chartSize.value * 0.08f).coerceIn(8f, 11f).sp,
                                color    = colors.textSecondary
                            )
                        }
                    }

                    // Легенда
                    Column(
                        modifier            = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        display.forEachIndexed { idx, expense ->
                            val color = pieChartColors[idx % pieChartColors.size]
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(color, CircleShape)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text     = expense.categoryName,
                                        fontSize = 12.sp,
                                        color    = colors.textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text     = "₽${fmt.format(expense.amount.toLong())}  ${
                                            "%.1f".format(expense.percentage)
                                        }%",
                                        fontSize = 11.sp,
                                        color    = colors.textSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DonutChart(slices: List<CategoryExpense>, chartSize: Dp) {
    Canvas(modifier = Modifier.size(chartSize)) {
        val totalPct  = slices.sumOf { it.percentage.toDouble() }.toFloat().takeIf { it > 0f } ?: 1f
        val stroke    = (chartSize.toPx() * 0.18f).coerceIn(14f, 28f)
        val radius    = (size.minDimension - stroke) / 2f
        val topLeft   = Offset((size.width - radius * 2) / 2f, (size.height - radius * 2) / 2f)
        var angle     = -90f

        slices.forEachIndexed { idx, slice ->
            val sweep = (slice.percentage / totalPct) * 360f
            drawArc(
                color      = pieChartColors[idx % pieChartColors.size],
                startAngle = angle,
                sweepAngle = sweep,
                useCenter  = false,
                topLeft    = topLeft,
                size       = Size(radius * 2f, radius * 2f),
                style      = Stroke(width = stroke)
            )
            angle += sweep
        }
    }
}
