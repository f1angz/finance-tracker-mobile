package mobile.tracker.finance.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import mobile.tracker.finance.ui.screens.auth.login.LoginScreen
import mobile.tracker.finance.ui.screens.auth.register.RegisterScreen
import mobile.tracker.finance.ui.screens.home.HomeScreen

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
                    // Переход на главный экран после успешного входа
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
                    // Переход на главный экран после успешной регистрации
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // Главный экран
        composable(route = Screen.Home.route) {
            HomeScreen()
        }
    }
}
