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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobile.tracker.finance.data.models.CategoryExpense
import mobile.tracker.finance.ui.theme.CardBackground
import mobile.tracker.finance.ui.theme.LocalAppColors
import mobile.tracker.finance.ui.theme.TextPrimary
import mobile.tracker.finance.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.sin

/**
 * Круговая диаграмма расходов по категориям
 * @param expenses Список расходов по категориям
 */
@Composable
fun PieChartSection(
    expenses: List<CategoryExpense>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(LocalAppColors.current.cardBackground, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Расходы по категориям",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = LocalAppColors.current.textPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val chartSize = (maxWidth * 0.38f).coerceIn(80.dp, 160.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Диаграмма
                    Box(
                        modifier = Modifier.size(chartSize),
                        contentAlignment = Alignment.Center
                    ) {
                        PieChart(expenses = expenses, chartSize = chartSize)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Легенда
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        expenses.forEach { expense ->
                            LegendItem(
                                color = Color(expense.category.color),
                                label = expense.category.displayName
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Компонент круговой диаграммы
 */
@Composable
private fun PieChart(expenses: List<CategoryExpense>, chartSize: Dp = 160.dp) {
    Canvas(modifier = Modifier.size(chartSize)) {
        val totalPercentage = expenses.sumOf { it.percentage.toDouble() }.toFloat()
        var startAngle = -90f
        val strokeWidth = (chartSize.toPx() * 0.2f).coerceIn(16f, 32f)
        val radius = (size.minDimension - strokeWidth) / 2

        expenses.forEach { expense ->
            val sweepAngle = (expense.percentage / totalPercentage) * 360f

            drawArc(
                color = Color(expense.category.color),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(
                    (size.width - radius * 2) / 2,
                    (size.height - radius * 2) / 2
                ),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth)
            )

            startAngle += sweepAngle
        }

        // Белый круг в центре
        drawCircle(
            color = Color.White,
            radius = radius - strokeWidth / 2,
            center = center
        )
    }
}

/**
 * Элемент легенды
 */
@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = LocalAppColors.current.textSecondary,
            fontWeight = FontWeight.Normal
        )
    }
}
