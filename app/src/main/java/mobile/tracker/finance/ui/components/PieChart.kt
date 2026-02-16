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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobile.tracker.finance.data.models.CategoryExpense
import mobile.tracker.finance.ui.theme.CardBackground
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
            .background(CardBackground, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Расходы по категориям",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Диаграмма
                Box(
                    modifier = Modifier
                        .size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PieChart(expenses = expenses)
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

/**
 * Компонент круговой диаграммы
 */
@Composable
private fun PieChart(expenses: List<CategoryExpense>) {
    Canvas(modifier = Modifier.size(160.dp)) {
        val totalPercentage = expenses.sumOf { it.percentage.toDouble() }.toFloat()
        var startAngle = -90f
        val strokeWidth = 32.dp.toPx()
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
            color = TextSecondary,
            fontWeight = FontWeight.Normal
        )
    }
}
