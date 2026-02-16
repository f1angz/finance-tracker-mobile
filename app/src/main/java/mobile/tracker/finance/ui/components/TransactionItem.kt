package mobile.tracker.finance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobile.tracker.finance.R
import mobile.tracker.finance.data.models.Transaction
import mobile.tracker.finance.data.models.TransactionType
import mobile.tracker.finance.ui.theme.*
import java.text.DecimalFormat

/**
 * Элемент списка транзакций
 * @param transaction Транзакция
 */
@Composable
fun TransactionItem(
    transaction: Transaction,
    modifier: Modifier = Modifier
) {
    val formatter = DecimalFormat("#,###")
    val isIncome = transaction.type == TransactionType.INCOME

    // Определяем иконку на основе типа транзакции
    val icon = when {
        isIncome -> painterResource(R.drawable.income_operations)
        else -> painterResource(R.drawable.expense_operations)
    }

    val iconBackgroundColor = when {
        isIncome -> GreenPositive.copy(alpha = 0.1f)
        else -> RedNegative.copy(alpha = 0.1f)
    }

    val iconColor = when {
        isIncome -> GreenPositive
        else -> RedNegative
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Иконка
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBackgroundColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Название и дата
            Column {
                Text(
                    text = transaction.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = transaction.date,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Сумма
        Text(
            text = "${if (isIncome) "+" else "-"}₽ ${formatter.format(kotlin.math.abs(transaction.amount).toLong())}",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isIncome) GreenPositive else RedNegative
        )
    }
}
