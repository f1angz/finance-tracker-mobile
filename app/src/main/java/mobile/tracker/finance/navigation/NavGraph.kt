package mobile.tracker.finance.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import mobile.tracker.finance.ui.screens.auth.login.LoginScreen
import mobile.tracker.finance.ui.screens.auth.register.RegisterScreen
import mobile.tracker.finance.ui.screens.ai_assistant.AiAssistantScreen
import mobile.tracker.finance.ui.screens.categories.CategoriesScreen
import mobile.tracker.finance.ui.screens.home.HomeScreen
import mobile.tracker.finance.ui.screens.goals.GoalsScreen
import mobile.tracker.finance.ui.screens.operations.OperationsScreen
import mobile.tracker.finance.ui.screens.settings.SettingsScreen

/**
 * Граф навигации приложения
 *
 * @param navController Контроллер навигации
 * @param startDestination Начальный экран (по умолчанию - Login)
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Экран входа
        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Экран регистрации
        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // Главный экран
        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        // Экран операций
        composable(route = Screen.Operations.route) {
            OperationsScreen(navController = navController)
        }

        // Экран категорий
        composable(route = Screen.Categories.route) {
            CategoriesScreen(navController = navController)
        }

        // Экран ИИ-помощника
        composable(route = Screen.AiAssistant.route) {
            AiAssistantScreen(navController = navController)
        }

        // Экран целей и долгов
        composable(route = Screen.Goals.route) {
            GoalsScreen(navController = navController)
        }

        // Экран настроек
        composable(route = Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
