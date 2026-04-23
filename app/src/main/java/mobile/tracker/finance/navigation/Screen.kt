package mobile.tracker.finance.navigation

/**
 * Sealed класс для определения экранов приложения
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Operations : Screen("operations")
    object Categories : Screen("categories")
    object Limits : Screen("limits")
    object AiAssistant : Screen("ai_assistant")
    object Goals : Screen("goals")
    object Settings : Screen("settings")
}
