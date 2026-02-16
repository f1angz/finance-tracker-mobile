package mobile.tracker.finance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobile.tracker.finance.ui.theme.*
import java.text.DecimalFormat

/**
 * Карточка статистики
 * @param title Заголовок карточки
 * @param amount Сумма
 * @param changePercent Процент изменения
 * @param icon Иконка
 * @param iconBackgroundColor Цвет фона иконки
 */
@Composable
fun StatsCard(
    title: String,
    amount: Double,
    changePercent: Double,
    icon: Painter,
    iconBackgroundColor: Color,
    modifier: Modifier = Modifier
) {
    val formatter = DecimalFormat("#,###")
    val isPositive = changePercent >= 0

    Box(
        modifier = modifier
            .background(CardBackground, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            // Иконка в круглом фоне
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBackgroundColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = title,
                    tint = iconBackgroundColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Заголовок
            Text(
                text = title,
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Сумма
            Text(
                text = "₽ ${formatter.format(amount.toLong())}",
                fontSize = 20.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Процент изменения
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isPositive) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (isPositive) GreenPositive else RedNegative,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${if (isPositive) "+" else ""}${String.format("%.1f", changePercent)}%",
                    fontSize = 12.sp,
                    color = if (isPositive) GreenPositive else RedNegative,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
