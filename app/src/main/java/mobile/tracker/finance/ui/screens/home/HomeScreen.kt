package mobile.tracker.finance.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobile.tracker.finance.R
import mobile.tracker.finance.ui.components.GradientBackground
import mobile.tracker.finance.ui.theme.PrimaryBlue
import mobile.tracker.finance.ui.theme.TextPrimary
import mobile.tracker.finance.ui.theme.White

/**
 * Главный экран приложения Finance Tracker
 */
@Composable
fun HomeScreen() {
    GradientBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Логотип приложения
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(PrimaryBlue, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon),
                        contentDescription = "App Logo",
                        tint = White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Заголовок
                Text(
                    text = "Finance Tracker",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Описание
                Text(
                    text = "Главный экран приложения\nдля учёта личных финансов",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Дополнительный текст
                Text(
                    text = "Добро пожаловать!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = PrimaryBlue,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
