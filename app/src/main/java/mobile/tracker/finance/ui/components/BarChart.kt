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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobile.tracker.finance.data.models.MonthlyStats
import mobile.tracker.finance.ui.theme.*

/**
 * Секция столбчатой диаграммы
 * @param monthlyStats Статистика по месяцам
 */
@Composable
fun BarChartSection(
    monthlyStats: List<MonthlyStats>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(CardBackground, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Динамика",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                // Легенда
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LegendItem(color = GreenPositive, label = "Доходы")
                    LegendItem(color = RedNegative, label = "Расходы")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Диаграмма
            BarChart(monthlyStats = monthlyStats)
        }
    }
}

/**
 * Компонент столбчатой диаграммы
 */
@Composable
private fun BarChart(monthlyStats: List<MonthlyStats>) {
    val maxValue = monthlyStats.maxOfOrNull { maxOf(it.income, it.expense) } ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        // График
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            monthlyStats.forEach { stats ->
                MonthBar(
                    income = stats.income,
                    expense = stats.expense,
                    maxValue = maxValue,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Подписи месяцев
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            monthlyStats.forEach { stats ->
                Text(
                    text = stats.month,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

/**
 * Столбец для одного месяца
 */
@Composable
private fun MonthBar(
    income: Double,
    expense: Double,
    maxValue: Double,
    modifier: Modifier = Modifier
) {
    val incomeHeight = if (maxValue > 0) (income / maxValue).toFloat() else 0f
    val expenseHeight = if (maxValue > 0) (expense / maxValue).toFloat() else 0f

    Row(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        // Столбец дохода
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(incomeHeight)
                .widthIn(min = 12.dp)
        ) {
            drawRoundRect(
                color = GreenPositive,
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )
        }

        // Столбец расхода
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(expenseHeight)
                .widthIn(min = 12.dp)
        ) {
            drawRoundRect(
                color = RedNegative,
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )
        }
    }
}

/**
 * Элемент легенды для графика
 */
@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Normal
        )
    }
}
