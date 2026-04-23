package mobile.tracker.finance.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary            = PrimaryBlue,
    onPrimary          = White,
    secondary          = Color(0xFF4A5565),
    onSecondary        = White,
    background         = Color(0xFFF8F9FA),
    onBackground       = Color(0xFF101828),
    surface            = Color(0xFFFFFFFF),
    onSurface          = Color(0xFF101828),
    surfaceVariant     = Color(0xFFF3F3F5),
    onSurfaceVariant   = Color(0xFF4A5565),
    error              = Color(0xFFDC2626),
    onError            = White
)

private val DarkColorScheme = darkColorScheme(
    primary            = PrimaryBlue,
    onPrimary          = White,
    secondary          = Color(0xFF9CA3AF),
    onSecondary        = White,
    background         = Color(0xFF111827),
    onBackground       = Color(0xFFF9FAFB),
    surface            = Color(0xFF1E2433),
    onSurface          = Color(0xFFF9FAFB),
    surfaceVariant     = Color(0xFF2D3748),
    onSurfaceVariant   = Color(0xFF9CA3AF),
    error              = Color(0xFFDC2626),
    onError            = White
)

@Composable
fun FinanceTrackerTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val appColors   = if (darkTheme) DarkAppColors else LightAppColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = Typography,
            content     = content
        )
    }
}
