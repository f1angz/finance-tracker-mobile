package mobile.tracker.finance.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// ─── Фиксированные цвета (не меняются в зависимости от темы) ─────────────────

val PrimaryBlue         = Color(0xFF155DFC)
val GreenPositive       = Color(0xFF10B981)
val RedNegative         = Color(0xFFEF4444)
val AiAssistantColor    = Color(0xFF9810FA)
val BottomNavSelected   = Color(0xFF155DFC)
val BottomNavUnselected = Color(0xFF9CA3AF)
val White               = Color(0xFFFFFFFF)
val Black               = Color(0xFF0A0A0A)

// Градиент фона Auth-экранов (не меняется)
val BackgroundGradientStart = Color(0xFFEFF6FF)
val BackgroundGradientEnd   = Color(0xFFE0E7FF)

// ─── Статические алиасы (используются в Auth-экранах, не переключаются) ──────
val TextPlaceholder = Color(0x800A0A0A)
val InputTextColor  = Color(0xFF717182)

// ─── AppColors — семантическая палитра, зависящая от темы ────────────────────

data class AppColors(
    val textPrimary: Color,
    val textSecondary: Color,
    val cardBackground: Color,
    val background: Color,
    val inputBackground: Color,
    val cardBorder: Color,
    val divider: Color,
    val switchTrackOn: Color,
    val switchTrackOff: Color,
    val isDark: Boolean
)

val LightAppColors = AppColors(
    textPrimary     = Color(0xFF101828),
    textSecondary   = Color(0xFF4A5565),
    cardBackground  = Color(0xFFFFFFFF),
    background      = Color(0xFFF8F9FA),
    inputBackground = Color(0xFFF3F3F5),
    cardBorder      = Color(0x1A000000),
    divider         = Color(0x1A000000),
    switchTrackOn   = Color(0xFF030213),
    switchTrackOff  = Color(0xFFCBCED4),
    isDark          = false
)

val DarkAppColors = AppColors(
    textPrimary     = Color(0xFFF9FAFB),
    textSecondary   = Color(0xFF9CA3AF),
    cardBackground  = Color(0xFF1E2433),
    background      = Color(0xFF111827),
    inputBackground = Color(0xFF2D3748),
    cardBorder      = Color(0x33FFFFFF),
    divider         = Color(0x1FFFFFFF),
    switchTrackOn   = Color(0xFF155DFC),
    switchTrackOff  = Color(0xFF4B5563),
    isDark          = true
)

val LocalAppColors = compositionLocalOf { LightAppColors }

// ─── Обратная совместимость: алиасы на LightAppColors для auth-экранов ────────
// (используются в Login/Register, которые не поддерживают тёмную тему)
val TextPrimary   get() = LightAppColors.textPrimary
val TextSecondary get() = LightAppColors.textSecondary
val CardBackground  get() = LightAppColors.cardBackground
val BackgroundLight get() = LightAppColors.background
val InputBackground get() = LightAppColors.inputBackground
