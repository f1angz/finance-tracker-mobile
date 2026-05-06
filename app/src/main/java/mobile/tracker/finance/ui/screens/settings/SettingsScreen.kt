package mobile.tracker.finance.ui.screens.settings

import android.Manifest
import android.app.DatePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import mobile.tracker.finance.navigation.Screen
import mobile.tracker.finance.ui.components.BottomNavBar
import mobile.tracker.finance.ui.components.GradientBackground
import mobile.tracker.finance.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// ─── Фиксированные local colors (не зависят от темы) ─────────────────────────

private val LogoutBg   = Color(0xFFFEF2F2)
private val LogoutRed  = Color(0xFFE7000B)
private val BadgeRed   = Color(0xFFFB2C36)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showExportDialog by remember { mutableStateOf(false) }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.importBankStatement(it, context) }
    }

    // Запрос разрешения POST_NOTIFICATIONS (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.onTogglePushNotifications()
    }

    // Выход из аккаунта
    LaunchedEffect(uiState.loggedOut) {
        if (uiState.loggedOut) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Toast notifications
    LaunchedEffect(uiState.importMessage) {
        uiState.importMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearImportMessage()
        }
    }
    LaunchedEffect(uiState.exportMessage) {
        uiState.exportMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearExportMessage()
        }
    }

    if (showExportDialog) {
        ExportDateRangeDialog(
            onDismiss = { showExportDialog = false },
            onConfirm = { from, to ->
                showExportDialog = false
                viewModel.exportPdf(from, to, context)
            }
        )
    }

    GradientBackground {
    Scaffold(
        topBar = {
            SettingsTopBar()
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
                        3 -> navController.navigate(Screen.AiAssistant.route) {
                            launchSingleTop = true
                        }
                        4 -> navController.navigate(Screen.Goals.route) {
                            launchSingleTop = true
                        }
                    }
                }
            )
        },
        containerColor = Color.Transparent
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
                    subtitle    = "",
                    checked     = uiState.isDarkTheme,
                    onToggle    = { viewModel.onToggleDarkTheme() }
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
                    subtitle    = "Ежедневное напоминание в 20:00",
                    checked     = uiState.pushNotificationsEnabled,
                    onToggle    = { enabled ->
                        if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.onTogglePushNotifications()
                        }
                    }
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
            }

            Spacer(Modifier.height(16.dp))

            // ─── Данные ──────────────────────────────────────────────────
            SettingsSectionLabel("Данные")
            Spacer(Modifier.height(8.dp))
            SettingsSectionCard {
                NavigationRow(
                    leadingIcon = {
                        if (uiState.isExporting) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            ExportIcon()
                        }
                    },
                    title    = "Экспорт данных",
                    subtitle = "Выгрузить отчёт в PDF",
                    onClick  = { if (!uiState.isExporting) showExportDialog = true }
                )
                SettingsDivider()
                NavigationRow(
                    leadingIcon = {
                        if (uiState.isImporting) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            ImportIcon()
                        }
                    },
                    title    = "Импорт данных",
                    subtitle = "Загрузить выписку (PDF)",
                    onClick  = { if (!uiState.isImporting) importLauncher.launch("application/pdf") }
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
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────

@Composable
private fun SettingsTopBar() {
    val colors = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .statusBarsPadding()
    ) {
        Text(
            text = "Настройки",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        HorizontalDivider(color = colors.divider, thickness = 1.dp)
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
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .background(colors.cardBackground, RoundedCornerShape(14.dp))
            .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
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
                color = colors.textPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = email,
                fontSize = 14.sp,
                color = colors.textSecondary
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = colors.textSecondary,
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
        color = LocalAppColors.current.textSecondary,
        modifier = Modifier.padding(start = 20.dp)
    )
}

@Composable
private fun SettingsSectionCard(content: @Composable ColumnScope.() -> Unit) {
    val colors = LocalAppColors.current
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .background(colors.cardBackground, RoundedCornerShape(14.dp))
            .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp)),
        content = content
    )
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(color = LocalAppColors.current.divider, thickness = 1.dp)
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
    val colors = LocalAppColors.current
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
                color = colors.textPrimary
            )
            if (subtitle.isNotEmpty()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textSecondary
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor   = White,
                checkedTrackColor   = colors.switchTrackOn,
                checkedBorderColor  = Color.Transparent,
                uncheckedThumbColor = White,
                uncheckedTrackColor = colors.switchTrackOff,
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
    val colors = LocalAppColors.current
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
                color = colors.textPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = colors.textSecondary,
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
    val colors = LocalAppColors.current
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
            color = colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun InfoRow(
    title: String,
    value: String
) {
    val colors = LocalAppColors.current
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
            color = colors.textPrimary
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = colors.textSecondary
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

// ─── Export Date Range Dialog ─────────────────────────────────────────────────

@Composable
private fun ExportDateRangeDialog(
    onDismiss: () -> Unit,
    onConfirm: (from: String, to: String) -> Unit
) {
    val context = LocalContext.current
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val displaySdf = remember { SimpleDateFormat("dd.MM.yyyy", Locale("ru")) }

    val todayCal = remember { Calendar.getInstance() }
    val firstDayCal = remember {
        Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }
    }

    var fromCal by remember { mutableStateOf(firstDayCal) }
    var toCal by remember { mutableStateOf(todayCal) }

    fun pickDate(initial: Calendar, onPicked: (Calendar) -> Unit) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                onPicked(Calendar.getInstance().apply { set(year, month, day) })
            },
            initial.get(Calendar.YEAR),
            initial.get(Calendar.MONTH),
            initial.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text("Период отчёта", fontWeight = FontWeight.SemiBold, color = TextPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Выберите диапазон дат для выгрузки PDF-отчёта.",
                    fontSize = 14.sp,
                    color = LocalAppColors.current.textSecondary
                )
                // From date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("С:", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    OutlinedButton(
                        onClick = { pickDate(fromCal) { fromCal = it } },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(displaySdf.format(fromCal.time), fontSize = 14.sp)
                    }
                }
                // To date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("По:", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    OutlinedButton(
                        onClick = { pickDate(toCal) { toCal = it } },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(displaySdf.format(toCal.time), fontSize = 14.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(sdf.format(fromCal.time), sdf.format(toCal.time)) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = !fromCal.after(toCal)
            ) {
                Text("Выгрузить", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = LocalAppColors.current.textSecondary)
            }
        }
    )
}

// ─── Icon Helpers ─────────────────────────────────────────────────────────────

@Composable
private fun DarkModeIcon() {
    Icon(
        imageVector = Icons.Outlined.DarkMode,
        contentDescription = null,
        tint = LocalAppColors.current.textPrimary,
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
        color = LocalAppColors.current.textSecondary
    )
}

@Composable
private fun LockIcon() {
    Icon(
        imageVector = Icons.Outlined.Lock,
        contentDescription = null,
        tint = LocalAppColors.current.textPrimary,
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
        tint = LocalAppColors.current.textPrimary,
        modifier = Modifier.size(20.dp)
    )
}

@Composable
private fun ImportIcon() {
    Icon(
        imageVector = Icons.Default.FileUpload,
        contentDescription = null,
        tint = LocalAppColors.current.textPrimary,
        modifier = Modifier.size(20.dp)
    )
}
