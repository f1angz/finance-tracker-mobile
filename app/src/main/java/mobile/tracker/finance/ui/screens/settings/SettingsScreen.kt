package mobile.tracker.finance.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import mobile.tracker.finance.navigation.Screen
import mobile.tracker.finance.ui.components.BottomNavBar
import mobile.tracker.finance.ui.theme.*

// ─── Local colors ─────────────────────────────────────────────────────────────

private val ScreenBg          = Color(0xFFF9FAFB)
private val CardBorder        = Color(0x1A000000)
private val DividerColor      = Color(0x1A000000)
private val SectionLabelColor = Color(0xFF6A7282)
private val SubtitleColor     = Color(0xFF6A7282)
private val SwitchOffTrack    = Color(0xFFCBCED4)
private val SwitchOnTrack     = Color(0xFF030213)
private val LogoutBg          = Color(0xFFFEF2F2)
private val LogoutRed         = Color(0xFFE7000B)
private val HeaderBorder      = Color(0xFFE5E7EB)
private val BadgeRed          = Color(0xFFFB2C36)
private val DateTextColor     = Color(0xFF364153)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            SettingsTopBar(onEditProfile = viewModel::onEditProfile)
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = 5,
                onTabSelected = { tab ->
                    when (tab) {
                        0 -> navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                        1 -> navController.navigate(Screen.Operations.route) {
                            launchSingleTop = true
                        }
                        2 -> navController.navigate(Screen.Categories.route) {
                            launchSingleTop = true
                        }
                        3 -> navController.navigate(Screen.Limits.route) {
                            launchSingleTop = true
                        }
                        4 -> navController.navigate(Screen.Goals.route) {
                            launchSingleTop = true
                        }
                    }
                }
            )
        },
        containerColor = ScreenBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))

            // ─── Профиль ─────────────────────────────────────────────────
            ProfileCard(
                initials = uiState.userInitials,
                name     = uiState.userName,
                email    = uiState.userEmail,
                onClick  = viewModel::onEditProfile
            )

            Spacer(Modifier.height(16.dp))

            // ─── Внешний вид ─────────────────────────────────────────────
            SettingsSectionLabel("Внешний вид")
            Spacer(Modifier.height(8.dp))
            SettingsSectionCard {
                ToggleRow(
                    leadingIcon = { DarkModeIcon() },
                    title       = "Тёмная тема",
                    subtitle    = "Автоматически ночью",
                    checked     = uiState.isDarkTheme,
                    onToggle    = { viewModel.onToggleDarkTheme() }
                )
                SettingsDivider()
                NavigationRow(
                    leadingIcon = { RubleIcon() },
                    title       = "Валюта",
                    subtitle    = uiState.currency,
                    onClick     = viewModel::onCurrencyClick
                )
                SettingsDivider()
                NavigationRow(
                    leadingIcon = { DateIcon() },
                    title       = "Формат даты",
                    subtitle    = uiState.dateFormat,
                    onClick     = viewModel::onDateFormatClick
                )
            }

            Spacer(Modifier.height(16.dp))

            // ─── Уведомления ─────────────────────────────────────────────
            SettingsSectionLabel("Уведомления")
            Spacer(Modifier.height(8.dp))
            SettingsSectionCard {
                ToggleRow(
                    leadingIcon = null,
                    title       = "Push-уведомления",
                    subtitle    = "О лимитах и целях",
                    checked     = uiState.pushNotificationsEnabled,
                    onToggle    = { viewModel.onTogglePushNotifications() }
                )
                SettingsDivider()
                ToggleRow(
                    leadingIcon = null,
                    title       = "Email-рассылка",
                    subtitle    = "Еженедельный отчёт",
                    checked     = uiState.emailNewsletterEnabled,
                    onToggle    = { viewModel.onToggleEmailNewsletter() }
                )
            }

            Spacer(Modifier.height(16.dp))

            // ─── Безопасность ────────────────────────────────────────────
            SettingsSectionLabel("Безопасность")
            Spacer(Modifier.height(8.dp))
            SettingsSectionCard {
                NavigationRowSingleLine(
                    leadingIcon = { LockIcon() },
                    title       = "Сменить пароль",
                    onClick     = viewModel::onChangePassword
                )
                SettingsDivider()
                ToggleRow(
                    leadingIcon = { BiometricsIcon() },
                    title       = "Биометрия",
                    subtitle    = "Face ID / Отпечаток",
                    checked     = uiState.biometricsEnabled,
                    onToggle    = { viewModel.onToggleBiometrics() }
                )
            }

            Spacer(Modifier.height(16.dp))

            // ─── Данные ──────────────────────────────────────────────────
            SettingsSectionLabel("Данные")
            Spacer(Modifier.height(8.dp))
            SettingsSectionCard {
                NavigationRow(
                    leadingIcon = { ExportIcon() },
                    title       = "Экспорт данных",
                    subtitle    = "Скачать в CSV",
                    onClick     = viewModel::onExportData
                )
                SettingsDivider()
                NavigationRow(
                    leadingIcon = { ImportIcon() },
                    title       = "Импорт данных",
                    subtitle    = "Загрузить из CSV",
                    onClick     = viewModel::onImportData
                )
            }

            Spacer(Modifier.height(16.dp))

            // ─── О приложении ────────────────────────────────────────────
            SettingsSectionLabel("О приложении")
            Spacer(Modifier.height(8.dp))
            SettingsSectionCard {
                InfoRow(
                    title = "Версия",
                    value = uiState.appVersion
                )
                SettingsDivider()
                NavigationRowSingleLine(
                    leadingIcon = null,
                    title       = "Правовая информация",
                    onClick     = viewModel::onLegalInfoClick
                )
            }

            Spacer(Modifier.height(16.dp))

            // ─── Выход ───────────────────────────────────────────────────
            LogoutButton(onClick = viewModel::onLogout)

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────

@Composable
private fun SettingsTopBar(onEditProfile: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "Настройки",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { /* TODO: открыть уведомления */ }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Уведомления",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 0.dp, y = 2.dp)
                            .size(8.dp)
                            .background(BadgeRed, CircleShape)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(PrimaryBlue, CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onEditProfile
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Редактировать профиль",
                        tint = White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        HorizontalDivider(color = HeaderBorder, thickness = 1.dp)
    }
}

// ─── Profile Card ─────────────────────────────────────────────────────────────

@Composable
private fun ProfileCard(
    initials: String,
    name: String,
    email: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .background(CardBackground, RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(PrimaryBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = White
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = email,
                fontSize = 14.sp,
                color = SubtitleColor
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = SubtitleColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ─── Section Shell ────────────────────────────────────────────────────────────

@Composable
private fun SettingsSectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = SectionLabelColor,
        modifier = Modifier.padding(start = 20.dp)
    )
}

@Composable
private fun SettingsSectionCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .background(CardBackground, RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
        content = content
    )
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(color = DividerColor, thickness = 1.dp)
}

// ─── Row Types ────────────────────────────────────────────────────────────────

@Composable
private fun ToggleRow(
    leadingIcon: (@Composable () -> Unit)?,
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                leadingIcon()
            }
            Spacer(Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = SubtitleColor
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = White,
                checkedTrackColor = SwitchOnTrack,
                checkedBorderColor = Color.Transparent,
                uncheckedThumbColor = White,
                uncheckedTrackColor = SwitchOffTrack,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun NavigationRow(
    leadingIcon: (@Composable () -> Unit)?,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                leadingIcon()
            }
            Spacer(Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = SubtitleColor
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = SubtitleColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun NavigationRowSingleLine(
    leadingIcon: (@Composable () -> Unit)?,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                leadingIcon()
            }
            Spacer(Modifier.width(12.dp))
        }
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = SubtitleColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun InfoRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = SubtitleColor
        )
    }
}

// ─── Logout Button ────────────────────────────────────────────────────────────

@Composable
private fun LogoutButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .background(LogoutBg, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ExitToApp,
            contentDescription = null,
            tint = LogoutRed,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Выйти из аккаунта",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = LogoutRed
        )
    }
}

// ─── Icon Helpers ─────────────────────────────────────────────────────────────

@Composable
private fun DarkModeIcon() {
    Icon(
        imageVector = Icons.Outlined.DarkMode,
        contentDescription = null,
        tint = TextPrimary,
        modifier = Modifier.size(20.dp)
    )
}

@Composable
private fun RubleIcon() {
    Text(
        text = "₽",
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF0A0A0A)
    )
}

@Composable
private fun DateIcon() {
    Text(
        text = "12",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = DateTextColor
    )
}

@Composable
private fun LockIcon() {
    Icon(
        imageVector = Icons.Outlined.Lock,
        contentDescription = null,
        tint = TextPrimary,
        modifier = Modifier.size(20.dp)
    )
}

@Composable
private fun BiometricsIcon() {
    Text(text = "🔐", fontSize = 16.sp)
}

@Composable
private fun ExportIcon() {
    Icon(
        imageVector = Icons.Default.FileDownload,
        contentDescription = null,
        tint = TextPrimary,
        modifier = Modifier.size(20.dp)
    )
}

@Composable
private fun ImportIcon() {
    Icon(
        imageVector = Icons.Default.FileUpload,
        contentDescription = null,
        tint = TextPrimary,
        modifier = Modifier.size(20.dp)
    )
}
