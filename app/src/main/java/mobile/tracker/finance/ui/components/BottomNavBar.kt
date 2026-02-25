package mobile.tracker.finance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobile.tracker.finance.R
import mobile.tracker.finance.ui.theme.*

/**
 * Нижняя навигационная панель
 * @param selectedTab Индекс выбранной вкладки
 * @param onTabSelected Коллбэк при выборе вкладки
 */
@Composable
fun BottomNavBar(
    selectedTab: Int = 0,
    onTabSelected: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        BottomNavItem("Главная", ImageVector.vectorResource(R.drawable.main),
            ImageVector.vectorResource(R.drawable.main)),
        BottomNavItem("Операции", ImageVector.vectorResource(R.drawable.operations),
            ImageVector.vectorResource(R.drawable.operations)),
        BottomNavItem("Категории", ImageVector.vectorResource(R.drawable.category),
            ImageVector.vectorResource(R.drawable.category)),
        BottomNavItem("Лимиты", ImageVector.vectorResource(R.drawable.limits),
            ImageVector.vectorResource(R.drawable.limits)),
        BottomNavItem("Цели", ImageVector.vectorResource(R.drawable.goals),
            ImageVector.vectorResource(R.drawable.goals)),
        BottomNavItem("Ещё", ImageVector.vectorResource(R.drawable.settings),

            ImageVector.vectorResource(R.drawable.settings))
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CardBackground)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, item ->
            BottomNavItemView(
                item = item,
                isSelected = selectedTab == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
}

/**
 * Модель элемента нижней навигации
 */
private data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

/**
 * Отображение элемента нижней навигации
 */
@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(8.dp)
    ) {
        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.label,
            tint = if (isSelected) BottomNavSelected else BottomNavUnselected,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = item.label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) BottomNavSelected else BottomNavUnselected
        )
    }
}
