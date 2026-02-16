package mobile.tracker.finance.navigation

/**
 * Sealed класс для определения экранов приложения
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    // TODO: Добавить другие экраны (главный экран, транзакции и т.д.)
    object Home : Screen("home")
}
